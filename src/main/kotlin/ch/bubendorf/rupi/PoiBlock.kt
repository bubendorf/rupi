package ch.bubendorf.rupi

class PoiBlock(categoryName: String,
               waypoints: List<Waypoint>) : Block(categoryName, waypoints) {

    override val marker: Int
        get() = Integer.MIN_VALUE

    override val type: String
        get() = "POI"

    override fun write(outputStream: PoiOutputStream, level: String) {
        logger.debug("Write PoiBlock (Level=$level, Offset=0x${outputStream.position.toString(16)}, Size=${waypoints.size}, $boundingBox)")

        outputStream.writeSwapInt(waypoints.size)

        // Make room for the index
        var startPosition = outputStream.position
        for (i in waypoints.indices) {
            outputStream.writeInt(0)
        }

        val offsets = IntArray(waypoints.size)
        for ((number, waypoint) in waypoints.withIndex()) {
            offsets[number] = outputStream.size()
            writeWaypoint(outputStream, number, waypoint)
        }

        // Write Index
        val currentPositon = outputStream.position
        for (offset in offsets) {
            startPosition = outputStream.writeIndex(startPosition, offset)
        }
        outputStream.position = currentPositon

    }

    private fun writeWaypoint(outputStream: PoiOutputStream, number: Int, waypoint: Waypoint) {
        outputStream.writeSwapInt(number + 1)
        outputStream.writeCodePointCount(categoryName)
        outputStream.write(1)
        outputStream.writeSwapInt(number + 1)
        outputStream.write(byteArrayOf(0.toByte(), 1.toByte(), 0.toByte(), 0.toByte(), 0.toByte()))
        outputStream.writeSpecialStringWthLength(waypoint.name)
        outputStream.writeSwapInt(waypoint.longitudeInt)
        outputStream.writeSwapInt(waypoint.latitudeInt)

        outputStream.writeStringWithLength(waypoint.address)
        outputStream.writeInt(0x80)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeInt(0x80)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeInt(0x80)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0x80)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeStringWithLength(waypoint.shortDescription)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeStringWithLength(waypoint.longDescription)
        outputStream.writeInt(-1)
        outputStream.writeInt(-1)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0x80)
        outputStream.writeStringWithLength(waypoint.phone)
        outputStream.writeStringWithLength(waypoint.fax)
        outputStream.writeStringWithLength(waypoint.email)
        outputStream.writeStringWithLength(waypoint.web)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0)
        outputStream.write(0)
    }
}
