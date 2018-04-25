package ch.bubendorf.rupi

import org.slf4j.LoggerFactory

//const val DEFAULT_POIS_PER_POIBLOCK = 256
const val DEFAULT_POIS_PER_POIBLOCK = 16
const val MAX_POIS_PER_POIBLOCK = 2 * DEFAULT_POIS_PER_POIBLOCK - 1

const val MAX_BLOCKS_PER_BBOXBLOCK = 8

const val DEFAULT_POIS_PER_BBOXBLOCK = MAX_BLOCKS_PER_BBOXBLOCK * DEFAULT_POIS_PER_POIBLOCK
const val MAX_POIS_PER_BBOXBLOCK = MAX_BLOCKS_PER_BBOXBLOCK * DEFAULT_POIS_PER_BBOXBLOCK + MAX_POIS_PER_POIBLOCK - DEFAULT_POIS_PER_POIBLOCK

abstract class Block(val categoryName: String,
                     unsortedWaypoints: List<Waypoint>) {

    protected val LOGGER = LoggerFactory.getLogger(Block::class.java.simpleName)

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

    abstract fun write(outputStream: PoiOutputStream, level: String = "")

    abstract val marker: Int
    abstract val type : String

    protected fun calcNumberOfBlocks(waypoints: Int, maxBlockSize: Int): Int {
        var numberOfBlocks = waypoints / maxBlockSize
        if (waypoints % maxBlockSize > 0) {
            numberOfBlocks++
        }
        return numberOfBlocks
    }
}
