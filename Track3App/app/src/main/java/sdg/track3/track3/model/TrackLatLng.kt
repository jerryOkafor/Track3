package sdg.track3.track3.model

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Kizito Nwose on 2017/11/19
 */
data class TrackLatLng(val lat: Double, val lng: Double) {
    companion object {
        fun fromLatLng(latLng: LatLng): TrackLatLng = TrackLatLng(latLng.latitude, latLng.longitude)
    }

}