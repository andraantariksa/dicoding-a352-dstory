package my.id.andraaa.dstory.stories.domain

import com.google.android.gms.maps.model.LatLng

data class Coordinate(
    val latitude: Double,
    val longitude: Double,
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}