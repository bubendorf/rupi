package ch.bubendorf.rupi

open class Coordinate(
        val longitude: Double,
        val latitude: Double) {

    // Distance in meter
    fun getDistanceTo(coordinate: Coordinate): Double {
        val lon1 = Math.toRadians(longitude)
        val lat1 = Math.toRadians(latitude)
        val lon2 = Math.toRadians(coordinate.longitude)
        val lat2 = Math.toRadians(coordinate.latitude)
        return Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * 6378137 // 6378.135: approximate earth radius (WGS84 reference ellipsoid)
    }
}

