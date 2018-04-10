package ch.bubendorf.rupi


class BoundingBox(list: Collection<Waypoint>) {
    var minLatitude = 90.0
    var maxLatitude = -90.0

    var minLongitude = 180.0
    var maxLongitude = -180.0

    val maxLatInt: Int
        get() = Math.round(maxLatitude * 100000.0).toInt()

    val minLatInt: Int
        get() = Math.round(minLatitude * 100000.0).toInt()

    val maxLonInt: Int
        get() = Math.round(maxLongitude * 100000.0).toInt()

    val minLonInt: Int
        get() = Math.round(minLongitude * 100000.0).toInt()

    fun getHeightInMeters(): Double {
        val p1 = Coordinate((maxLongitude + minLongitude) / 2, minLatitude)
        val p2 = Coordinate((maxLongitude + minLongitude) / 2, maxLatitude)
        return p1.getDistanceTo(p2)
    }

    fun getWidthtInMeters(): Double {
        val p1 = Coordinate((maxLatitude + minLatitude) / 2, minLongitude)
        val p2 = Coordinate((maxLatitude + minLatitude) / 2, maxLongitude)
        return p1.getDistanceTo(p2)
    }

    init {
        for (waypoint in list) {
            if (waypoint.latitude > maxLatitude) {
                maxLatitude = waypoint.latitude
            }
            if (waypoint.latitude < minLatitude) {
                minLatitude = waypoint.latitude
            }
            if (waypoint.longitude > maxLongitude) {
                maxLongitude = waypoint.longitude
            }
            if (waypoint.longitude < minLongitude) {
                minLongitude = waypoint.longitude
            }
        }
    }

    override fun toString(): String {
        return "BoundingBox(minLatitude=$minLatitude, maxLatitude=$maxLatitude, minLongitude=$minLongitude, maxLongitude=$maxLongitude)"
    }


}
