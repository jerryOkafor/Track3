package sdg.track3.track3.tag

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tag.*
import kotlinx.android.synthetic.main.content_tag.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import sdg.track3.track3.R
import sdg.track3.track3.model.TrackLatLng
import sdg.track3.track3.model.TrackPlace
import sdg.track3.track3.util.disAble
import sdg.track3.track3.util.enable
import sdg.track3.track3.util.toggle
import timber.log.Timber
import java.io.IOException
import java.util.*


class TagActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val RC_LOCATION = 101
        private const val REQUEST_CHECK_SETTINGS = 102
        private const val REQUEST_PLACE_PICKER = 103
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private var selectedPlace: Place? = null
    private lateinit var viewModel: TagViewModel
    private var addressString: String? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            p0?.locations?.forEach { it ->
                lastLocation = it
                var errorMessage = ""
                viewModel.getPlaceAddress(lastLocation!!.latitude, lastLocation!!.longitude, this@TagActivity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            val address = it[0]
                            val addressFragments = (0..address.maxAddressLineIndex)
                                    .mapTo(ArrayList<String>()) { address.getAddressLine(it) }

                            // Fetch the address lines using getAddressLine,
                            // join them, and send them to the thread.
                            Timber.i(getString(R.string.address_found))
                            addressString = (TextUtils.join(System.getProperty("line.separator"),
                                    addressFragments))
                            Timber.d("Address String: %s", addressString)
                        }, {
                            when (it) {
                                is IOException -> {
                                    errorMessage = getString(R.string.service_not_available)
                                    Timber.e("%s: %s", errorMessage, it.localizedMessage)
                                }
                                is IllegalAccessException -> {
                                    errorMessage = getString(R.string.invalid_lat_long_used)
                                    Timber.e("%s. %s", errorMessage, it.localizedMessage)

                                }
                            }
                            Toast.makeText(this@TagActivity, errorMessage, Toast.LENGTH_LONG).show()
                        })
                Timber.d("Location Changed: %s:: Lat: %f, Lng: %f", lastLocation.toString(), lastLocation?.latitude, lastLocation?.longitude)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(TagViewModel::class.java)

        viewModel.observeToastError()
                .observe(this, Observer<String> {
                    Toast.makeText(this@TagActivity, it, Toast.LENGTH_LONG).show()
                })


        //set home up enabled
        supportActionBar?.setHomeButtonEnabled(true)

        //get the fused location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkBox.checkedChanges()
                .skipInitialValue()
                .subscribe { checked -> btnChoosePlace.toggle(!checked) }
        btnChoosePlace.setOnClickListener({ openPlacePicker() })

        //tag
        btnTag.setOnClickListener({ startTag() })

    }

    private fun startTag() {
        btnTag.disAble()
        val placeName = tvName.text.toString();
        if (placeName.isEmpty()) {
            Toast.makeText(this, "TrackPlace name can not be null", Toast.LENGTH_LONG).show()
            return
        }
        var placeAddress = ""
        var placeCord = TrackLatLng(0.0, 0.0)
        if (selectedPlace != null) {
            placeAddress = selectedPlace!!.address.toString()
            placeCord = TrackLatLng.fromLatLng(selectedPlace!!.latLng)
        } else if (!addressString.isNullOrEmpty()) {
            placeAddress = addressString.orEmpty()
            placeCord = TrackLatLng(lastLocation!!.latitude, lastLocation!!.longitude)
        }

        if (placeAddress.isEmpty()) {
            Toast.makeText(this, "TrackPlace address can not be empty, Please select a place!", Toast.LENGTH_LONG).show()
            return
        }


        val place = TrackPlace(placeName, placeAddress, placeCord)
        FirebaseDatabase.getInstance().reference.child("people").push()
                .setValue(place)
                .addOnSuccessListener {
                    btnTag.enable()
                    Toast.makeText(this, "Place tagging successful", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener({
                    btnTag.enable()
                    Toast.makeText(this, "Error: :" + it.localizedMessage, Toast.LENGTH_LONG).show()
                })

    }

    private fun openPlacePicker() {
        val builder = PlacePicker.IntentBuilder()
//        builder.setLatLngBounds(LatLngBounds(...)); //Not set, use the devices current location
        startActivityForResult(builder.build(this), REQUEST_PLACE_PICKER)
    }

    override fun onResume() {
        super.onResume()
        if (isGooglePlayServiceAvailable())
            requestLocationUpdate()
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest

    }

    @AfterPermissionGranted(RC_LOCATION)
    private fun requestLocationUpdate() {
        val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(this, *perms)) {

            //ensure proper location settings
            val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(createLocationRequest())
            val client = LocationServices.getSettingsClient(this)

            client.checkLocationSettings(builder.build())
                    .addOnSuccessListener {
                        //All locations settings are satisfied. The client can initialize
                        //location request here
                        startLocationUpdate()
                    }
                    .addOnFailureListener { exception ->
                        val statusCode = (exception as ApiException).statusCode
                        when (statusCode) {
                            CommonStatusCodes.RESOLUTION_REQUIRED ->
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    val resolvable = exception as ResolvableApiException
                                    resolvable.startResolutionForResult(this@TagActivity,
                                            REQUEST_CHECK_SETTINGS)
                                } catch (sendEx: IntentSender.SendIntentException) {
                                    // Ignore the error.
                                }

                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            }
                        }// Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                    }


        } else {
            //do not have location, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.location_request_rationale), RC_LOCATION, *perms)
        }

    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, null)
    }

    private fun stopLocationUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun isGooglePlayServiceAvailable(): Boolean {
        val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        return when (result) {
            ConnectionResult.SUCCESS -> true
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, ConnectionResult.SERVICE_MISSING, ConnectionResult.SERVICE_DISABLED
            -> {
                GoogleApiAvailability.getInstance().getErrorDialog(this, result, 100).show()
                false
            }
            else -> false
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> if (resultCode == Activity.RESULT_OK) startLocationUpdate()
            REQUEST_PLACE_PICKER -> {
                if (resultCode == Activity.RESULT_OK) {
                    selectedPlace = PlacePicker.getPlace(this, data)
                    Timber.d("Selected TrackPlace: %s", selectedPlace.toString())
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Timber.d("Permission Denied: %s", requestCode)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.d("Permission Granted: %s", requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //Forward result to easy permission
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdate()
    }

}
