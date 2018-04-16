package ch.bubendorf.rupi

import org.slf4j.LoggerFactory

class BBoxBlock(categoryName: String,
                waypoints: List<Waypoint>) : Block(categoryName, waypoints) {

    override val marker: Int
        get() = 0

    private val blocks: MutableList<Block> = ArrayList()

    init {
        // Abhängig von der Anzahl der Waypoints werden 1 bis 8 Blocks erzeugt.
        // Der Typ des Blocks, PoiBlock oder BBoxBlock, wiederum ist abhängig von
        // der Anzahl der Waypoints welche in diesen Block gehen!
        if (waypoints.size == 0) {
            // Nix zu tun!
        } else if (waypoints.size < DEFAULT_POIS_PER_BBOXBLOCK + MAX_POIS_PER_POIBLOCK - DEFAULT_POIS_PER_POIBLOCK) {
            // Nur wenige Waypoints => Wir erzeugen in diesem Falle 1 bis 8 PoiBlocks
            val numberBlocks = calcNumberOfBlocks(waypoints.size, DEFAULT_POIS_PER_POIBLOCK)
            var sizeOfFirstBlock = waypoints.size - (numberBlocks - 1) * DEFAULT_POIS_PER_POIBLOCK
            if (numberBlocks > 1 && sizeOfFirstBlock < DEFAULT_POIS_PER_POIBLOCK) {
                sizeOfFirstBlock += DEFAULT_POIS_PER_POIBLOCK
            }

            if (sizeOfFirstBlock > 0) {
                blocks.add(PoiBlock(categoryName, waypoints.subList(0, sizeOfFirstBlock)))
            }

            var index = sizeOfFirstBlock
            while (index < waypoints.size) {
                blocks.add(PoiBlock(categoryName, waypoints.subList(index, index + DEFAULT_POIS_PER_POIBLOCK)))
                index += DEFAULT_POIS_PER_POIBLOCK
            }
        } else if (waypoints.size < MAX_POIS_PER_BBOXBLOCK) {
            // Viele Waypoints => Wir erzeugen 1 bis 7 BBoxBlocks und einen PoiBlock
            val numberBlocks = calcNumberOfBlocks(waypoints.size, DEFAULT_POIS_PER_BBOXBLOCK)
            val sizeOfPoiBlock = waypoints.size - (numberBlocks - 1) * DEFAULT_POIS_PER_BBOXBLOCK

            if (sizeOfPoiBlock > 0) {
                blocks.add(PoiBlock(categoryName, waypoints.subList(0, sizeOfPoiBlock)))
            }

            var index = sizeOfPoiBlock
            while (index < waypoints.size) {
                blocks.add(BBoxBlock(categoryName, waypoints.subList(index, index + DEFAULT_POIS_PER_BBOXBLOCK)))
                index += DEFAULT_POIS_PER_BBOXBLOCK
            }
        } else {
            // Sehr viele Waypoints ==> Wir erzeugen 8 BBoxBlocks
            val sizeOfBBoxBlock = waypoints.size / MAX_BLOCKS_PER_BBOXBLOCK // Oder so? Todo: Runden auf Vielfaches von 128?
            val sizeOfFirstBlock = waypoints.size - (MAX_BLOCKS_PER_BBOXBLOCK - 1) * sizeOfBBoxBlock

            blocks.add(BBoxBlock(categoryName, waypoints.subList(0, sizeOfFirstBlock)))

            var index = sizeOfFirstBlock
            while (index < waypoints.size) {
                blocks.add(BBoxBlock(categoryName, waypoints.subList(index, index + sizeOfBBoxBlock)))
                index += sizeOfBBoxBlock
            }
        }
    }

    override fun write(outputStream: PoiOutputStream, level: Int) {
        LOGGER.debug("Write BBoxBlock (Level=$level, Offset=0x${outputStream.position.toString(16)}, Blocks=${blocks.size})")

        // Marker
        outputStream.write(blocks.size)
        outputStream.write(0)
        outputStream.write(0)
        outputStream.write(0)

        // Room for the BBox-Index
        var startPosition = outputStream.position
        for (i in 0 until blocks.size) {
            outputStream.writeBoundingBox(blocks[i].boundingBox)
            outputStream.writeInt(0) // Placeholder for the offset
        }

        // Write Blocks
        val offsets = IntArray(blocks.size)
        var number = 0
        for (block in blocks) {
            offsets[number] = outputStream.size() or block.marker
            block.write(outputStream, level + 1)

            number++
        }

        // Write Index
        val currentPositon = outputStream.position
        for (offset in offsets) {
            startPosition += 16 // BoundingBox überspringen
            startPosition = outputStream.writeIndex(startPosition, offset)
        }
        outputStream.position = currentPositon
    }

}
