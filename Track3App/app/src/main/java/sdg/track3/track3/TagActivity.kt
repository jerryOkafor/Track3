package sdg.track3.track3

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.jakewharton.rxbinding2.widget.checkedChanges
import kotlinx.android.synthetic.main.activity_tag.*
import kotlinx.android.synthetic.main.content_tag.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import sdg.track3.track3.util.toggle
import timber.log.Timber


class TagActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val RC_LOCATION = 101
        private const val REQUEST_CHECK_SETTINGS = 102
        private const val REQUEST_PLACE_PICKER = 103
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private var selectedPlace: Place? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            p0?.locations?.forEach { it ->
                lastLocation = it
                Timber.d("Location Changed: %s:: Lat: %f, Lng: %f", lastLocation.toString(), lastLocation?.latitude, lastLocation?.longitude)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)
        setSupportActionBar(toolbar)


        //set home up enabled
        supportActionBar?.setHomeButtonEnabled(true)

        //get the fused location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkBox.checkedChanges()
                .skipInitialValue()
                .subscribe { checked -> btnChoosePlace.toggle(!checked) }
        btnChoosePlace.setOnClickListener({ openPlacePicker() })

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
                    Timber.d("Selected Place: %s", selectedPlace.toString())
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
