package ch.bubendorf.rupi

class Splitter {

    fun split(waypoints :List<Waypoint>, sizeOfFirstBlock:Int, sizeOfOtherBlocks: Int) : List<List<Waypoint>> {
        val blockSizes = ArrayList<Int>()
        var numberOfWaypoints = 0
        if (sizeOfFirstBlock > 0) {
            blockSizes.add(sizeOfFirstBlock)
            numberOfWaypoints = sizeOfFirstBlock
        }

        while (numberOfWaypoints < waypoints.size) {
            blockSizes.add(sizeOfOtherBlocks)
            numberOfWaypoints += sizeOfOtherBlocks
        }
        return split(waypoints, blockSizes)
    }

    /**
     * Teilt die Liste der Waypoints in n Teillisten auf.
     * Die Anzahl und die Grössen der Teillisten ist durch das IntArray gegeben.
     * Die Summe der Grössen muss zwingend der Gesamtzahl der Wegpunkte entsprechen.
     *
     * Die Wegpunkte werden derart in die einzelnen Listen aufgeteilt, dass sich die
     * BoundingBox der Teillisten nicht überlappen.
     *
     * Algorithmus:
     * Wenn Anzahl der Teillisten = 1 ==> Liste der Waypoints 1:1 zurück geben
     * Wenn Anzahl der Teillisten >1 ==> Zwei Haufen bilden und die Wegpunkte entlang der längeren
     * Seite der BoundingBox auf die beiden Haufen verteilen. Rekursiv weitermachen bis alles verteilt ist.
     */
    private fun split(waypoints :List<Waypoint>, blockSizes: List<Int>) : List<List<Waypoint>> {
        if (blockSizes.size == 1){
            return listOf(waypoints)
        }

        var index = 0
        var splitPoint = 0
        while (splitPoint < waypoints.size / 2 && index < blockSizes.size - 1) {
            splitPoint += blockSizes[index]
            index++
        }

        // Sortieren der Waypoints an der längeren Seite der BoundingBox
        val boundingBox = BoundingBox(waypoints)
        val heightInMeters = boundingBox.getHeightInMeters()
        val widthInMeters = boundingBox.getWidthInMeters()
        val sortedWaypoints = if (heightInMeters < widthInMeters) {
            waypoints.sortedBy { coordinate -> coordinate.longitude }
        } else {
            waypoints.sortedBy { coordinate -> coordinate.latitude }
        }

        val subList1 = split(sortedWaypoints.subList(0, splitPoint), blockSizes.subList(0, index))
        val subList2 = split(sortedWaypoints.subList(splitPoint, waypoints.size), blockSizes.subList(index, blockSizes.size))

        val joined = ArrayList<List<Waypoint>>()
        joined.addAll(subList1)
        joined.addAll(subList2)
        return joined
    }
}
