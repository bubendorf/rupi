package ch.bubendorf.rupi

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

open class Coordinate(
        val longitude: Double,
        val latitude: Double) {

    // Distance in meter
    fun getDistanceTo(coordinate: Coordinate): Double {
        val lon1 = Math.toRadians(longitude)
        val lat1 = Math.toRadians(latitude)
        val lon2 = Math.toRadians(coordinate.longitude)
        val lat2 = Math.toRadians(coordinate.latitude)
        return acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2 - lon1)) * 6378137 // 6378.137: approximate earth radius (WGS84 reference ellipsoid)
    }
}

