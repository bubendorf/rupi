package ch.bubendorf.rupi

class BBoxBlock(categoryName: String,
                waypoints: List<Waypoint>,
                private val notice: String? = null) : Block(categoryName, waypoints) {

    override val marker: Int
        get() = 0

    override val type: String
        get() = "BBox"

    private val blocks: MutableList<Block> = ArrayList()

    init {
        // Abhängig von der Anzahl der Waypoints werden 1 bis 8 Blocks erzeugt.
        // Der Typ des Blocks, PoiBlock oder BBoxBlock, wiederum ist abhängig von
        // der Anzahl der Waypoints welche in diesen Block gehen!
        when {
            waypoints.isEmpty() -> {
                // Nix zu tun!
            }
            waypoints.size < DEFAULT_POIS_PER_BBOXBLOCK + MAX_POIS_PER_POIBLOCK - DEFAULT_POIS_PER_POIBLOCK -> {
                // Nur wenige Waypoints => Wir erzeugen in diesem Falle 1 bis 8 PoiBlocks
                val numberBlocks = calcNumberOfBlocks(waypoints.size, DEFAULT_POIS_PER_POIBLOCK)
                var sizeOfFirstBlock = waypoints.size - (numberBlocks - 1) * DEFAULT_POIS_PER_POIBLOCK
                if (numberBlocks > 1 && sizeOfFirstBlock < DEFAULT_POIS_PER_POIBLOCK) {
                    sizeOfFirstBlock += DEFAULT_POIS_PER_POIBLOCK
                }

                val listOfList = Splitter().split(waypoints, sizeOfFirstBlock,DEFAULT_POIS_PER_POIBLOCK)
                listOfList.forEach { wps ->  blocks.add(PoiBlock(categoryName, wps))
                }
            }
            waypoints.size < MAX_POIS_PER_BBOXBLOCK -> {
                // Viele Waypoints => Wir erzeugen 1 bis 7 BBoxBlocks und einen PoiBlock
                val numberBlocks = calcNumberOfBlocks(waypoints.size, DEFAULT_POIS_PER_BBOXBLOCK)
                val sizeOfPoiBlock = waypoints.size - (numberBlocks - 1) * DEFAULT_POIS_PER_BBOXBLOCK

                val listOfList = Splitter().split(waypoints, sizeOfPoiBlock,DEFAULT_POIS_PER_BBOXBLOCK)
                listOfList.forEach { wps ->
                    if (wps.size == DEFAULT_POIS_PER_BBOXBLOCK) {
                        blocks.add(BBoxBlock(categoryName, wps))
                    } else {
                        blocks.add(PoiBlock(categoryName, wps))
                    }
                }
            }
            else -> {
                // Sehr viele Waypoints ==> Wir erzeugen 8 BBoxBlocks
                val sizeOfBBoxBlock = ((waypoints.size / MAX_BLOCKS_PER_BBOXBLOCK)/DEFAULT_POIS_PER_BBOXBLOCK)*DEFAULT_POIS_PER_BBOXBLOCK
                val sizeOfFirstBlock = waypoints.size - (MAX_BLOCKS_PER_BBOXBLOCK - 1) * sizeOfBBoxBlock

                val listOfList = Splitter().split(waypoints, sizeOfFirstBlock,sizeOfBBoxBlock)
                listOfList.forEach { wps ->  blocks.add(BBoxBlock(categoryName, wps)) }
            }
        }
    }

    override fun write(outputStream: PoiOutputStream, level: String) {
        logger.debug("Write BBoxBlock (Level=$level, Offset=0x${outputStream.position.toString(16)}, Blocks=${blocks.size}, $boundingBox)")

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

        // If provided add a notice, remark, etc. between the index and the data
        if (notice != null) {
            outputStream.writeString(notice)
        }

        // Write Blocks
        val offsets = IntArray(blocks.size)
        for ((number, block) in blocks.withIndex()) {
            offsets[number] = outputStream.size() or block.marker
            block.write(outputStream, "$level$number.")
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
