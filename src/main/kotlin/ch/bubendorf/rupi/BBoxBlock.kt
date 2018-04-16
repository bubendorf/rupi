package ch.bubendorf.rupi

class BBoxBlock(categoryName: String,
                waypoints: List<Waypoint>) : Block(categoryName, waypoints) {

    override val marker: Int
        get() = 0

    private val blocks: MutableList<Block> = ArrayList()

    init {
        // Abhängig von der Anzahl der Waypoints werden 2 bis 8 Blocks erzeugt.
        // Der Typ des Blocks, PoiBlock oder BBoxBlock, wiederum ist abhängig von
        // der Anzahl der Waypoints welche in diesen Block gehen!
        if (waypoints.size < 32) {
            // Sollte nicht passieren
            throw Exception("Too few waypoints: ${waypoints.size}!")
        } else if (waypoints.size < 128 + 16) {
            // Nur wenige Waypoints => Wir erzeugen in diesem Falle 2 bis 8 PoiBlocks
            val numberOfBlocks = waypoints.size / 16
            val sizeOfPoiBlock = 16
            val sizeOfFirstBlock = waypoints.size - (numberOfBlocks - 1) * sizeOfPoiBlock

            blocks.add(PoiBlock(categoryName, waypoints.subList(0, sizeOfFirstBlock)))

            var index = sizeOfFirstBlock
            while (index < waypoints.size) {
                blocks.add(PoiBlock(categoryName, waypoints.subList(index, index + sizeOfPoiBlock)))
                index += sizeOfPoiBlock
            }
        } else if (waypoints.size < 7 * 128 + 16) {
            // Viele Waypoints => Wir erzeugen 1 bis 7 BBoxBlocks und einen PoiBlock
            val numberOfBBoxBlocks = waypoints.size / 128
            val sizeOfPoiBlock = waypoints.size - (128 * numberOfBBoxBlocks)
            val sizeOfBBoxBlock = 128

            if (sizeOfPoiBlock > 0) {
                blocks.add(PoiBlock(categoryName, waypoints.subList(0, sizeOfPoiBlock)))
            }

            var index = sizeOfPoiBlock
            while (index < waypoints.size) {
                blocks.add(BBoxBlock(categoryName, waypoints.subList(index, index + sizeOfBBoxBlock)))
                index += sizeOfBBoxBlock
            }
        } else {
            // Sehr viele Waypoints ==> Wir erzeugen 8 BBoxBlocks
            val numberOfBlocks = 8
            val sizeOfBBoxBlock = waypoints.size / numberOfBlocks // Oder so?
            val sizeOfFirstBlock = waypoints.size - (numberOfBlocks - 1) * sizeOfBBoxBlock

            blocks.add(BBoxBlock(categoryName, waypoints.subList(0, sizeOfFirstBlock)))

            var index = sizeOfFirstBlock
            while (index < waypoints.size) {
                blocks.add(BBoxBlock(categoryName, waypoints.subList(index, index + sizeOfBBoxBlock)))
                index += sizeOfBBoxBlock
            }
        }
    }

    override fun write(outputStream: PoiOutputStream) {
        // Marker
        outputStream.write(blocks.size)
        outputStream.write(0)
        outputStream.write(0)
        outputStream.write(0)

        // Room for the BBox-Index
        var startPosition = outputStream.size()
        for (i in 0 until blocks.size) {
            outputStream.writeBoundingBox(blocks[i].boundingBox)
            outputStream.writeInt(0)
        }

        // Write Blocks
        val offsets = IntArray(blocks.size)
        var number = 0
        for (block in blocks) {
            offsets[number] = outputStream.size() or block.marker
            block.write(outputStream)

            number++
        }

        // Write Index
        val currentPositon = outputStream.position
        for (offset in offsets) {
            startPosition += 16 // BoundBox überspringen
            startPosition = outputStream.writeIndex(startPosition, offset)
        }
        outputStream.position = currentPositon
    }

}
