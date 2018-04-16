package ch.bubendorf.rupi

abstract class Block(val categoryName: String,
                     unsortedWaypoints: List<Waypoint>) {

    val boundingBox = BoundingBox(unsortedWaypoints)
    val waypoints: List<Waypoint>

    init {
        // Sortieren der Waypoints an der längeren Seite der BoundingBox
        val heightInMeters = boundingBox.getHeightInMeters()
        val widthtInMeters = boundingBox.getWidthtInMeters()
        waypoints = if (heightInMeters < widthtInMeters) {
            unsortedWaypoints.sortedBy { coordinate -> coordinate.longitude }
        } else {
            unsortedWaypoints.sortedBy { coordinate -> coordinate.latitude }
        }
    }

    abstract fun write(outputStream: PoiOutputStream)

    abstract val marker: Int
}
