package ch.bubendorf.rupi


class BoundingBox(list: Collection<Coordinate>) {
    private var minLatitude = 90.0
    private var maxLatitude = -90.0

    private var minLongitude = 180.0
    private var maxLongitude = -180.0

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

    fun getWidthInMeters(): Double {
        val p1 = Coordinate((maxLatitude + minLatitude) / 2, minLongitude)
        val p2 = Coordinate((maxLatitude + minLatitude) / 2, maxLongitude)
        return p1.getDistanceTo(p2)
    }

    init {
        for (coordinate in list) {
            if (coordinate.latitude > maxLatitude) {
                maxLatitude = coordinate.latitude
            }
            if (coordinate.latitude < minLatitude) {
                minLatitude = coordinate.latitude
            }
            if (coordinate.longitude > maxLongitude) {
                maxLongitude = coordinate.longitude
            }
            if (coordinate.longitude < minLongitude) {
                minLongitude = coordinate.longitude
            }
        }
    }

    override fun toString(): String {
        return "BBox(minLat=$minLatitude, maxLat=$maxLatitude, minLon=$minLongitude, maxLon=$maxLongitude)"
    }


}
