package ch.bubendorf.rupi

import java.io.FileInputStream
import java.io.InputStreamReader

fun main(args: Array<String>) {
    val firstLineReader = InputStreamReader(FileInputStream(args[0]), "iso-8859-1")
    val lines = firstLineReader.readLines()
    firstLineReader.close()
    if (lines.size > 0) {
        val firstLine = lines[0]
        val hasDelimiter = firstLine.startsWith("delimiter=")
        val linesToSkip = if (hasDelimiter) 1 else 0
        val delimiter = if (hasDelimiter) firstLine.substring(firstLine.indexOf("=") + 1)[0] else ';'

        val waypoints = lines.drop(linesToSkip)
                .map { it.split(delimiter) }
                .filter { it.size >= 3 && it[0].isNotBlank() && it[1].isNotBlank() && it[2].isNotBlank() }
                .map { Waypoint(it) }

/*        val reader = InputStreamReader(FileInputStream(args[0]), "iso-8859-1")
        val sygicCSVReader = SygicCSVReader(reader, linesToSkip, delimiter)
        val waypoints = sygicCSVReader.readWaypoints()*/
        firstLineReader.close()

//        println(waypoints[waypoints.lastIndex])

        // Write to RUPI file
        SygicPOIWriter("Traditional_CHE", args[1]).write(waypoints)

        println("Converted ${waypoints.size} waypoints")
    }
}

