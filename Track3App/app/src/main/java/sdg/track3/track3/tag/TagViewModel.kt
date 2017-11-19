package sdg.track3.track3.tag

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.location.Address
import android.location.Geocoder
import io.reactivex.Single
import java.util.*


/**
 * Created by Kizito Nwose on 2017/11/19
 */
class TagViewModel : ViewModel() {

    private val toastError = MutableLiveData<String>()

    fun getPlaceAddress(lat: Double, lng: Double, context: Context): Single<List<Address>> {

        val geoCoder = Geocoder(context, Locale.getDefault())

        return Single.fromCallable({ geoCoder.getFromLocation(lat, lng, 1) })
    }

    fun observeToastError(): LiveData<String> = toastError
}
