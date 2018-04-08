package ch.bubendorf.rupi


class BoundingBox(list: Collection<Waypoint>) {
    var maxLatitude = -90.0
    var minLatitude = 90.0

    var maxLongitude = -180.0
    var minLongitude = 180.0

    val maxLatInt: Int
        get() = Math.round(maxLatitude * 100000.0).toInt()

    val minLatInt: Int
        get() = Math.round(minLatitude * 100000.0).toInt()

    val maxLonInt: Int
        get() = Math.round(maxLongitude * 100000.0).toInt()

    val minLonInt: Int
        get() = Math.round(minLongitude * 100000.0).toInt()

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
}
