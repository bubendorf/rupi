package ch.bubendorf.rupi

import java.io.FileInputStream
import java.io.InputStreamReader

class CsvReader(private val inputFile: String, private val encoding: String) {

    fun read(): List<Waypoint> {
        val reader = InputStreamReader(FileInputStream(inputFile), encoding)
        val lines = reader.readLines()
        reader.close()

        if (lines.isEmpty()) {
            return emptyList()
        }
        // Aus der ersten Zeile den Delimiter lesen falls dieser gesetzt ist
        val firstLine = lines[0]
        val hasDelimiter = firstLine.startsWith("delimiter=")
        val linesToSkip = if (hasDelimiter) 1 else 0
        val delimiter = if (hasDelimiter) firstLine.substring(firstLine.indexOf("=") + 1)[0] else ';'

        // Zeile für Zeile lesen und in Waypoints konvertieren
        return lines.drop(linesToSkip)
                .map { it.split(delimiter) }
                .filter { it.size >= 3 && it[0].isNotBlank() && it[1].isNotBlank() && it[2].isNotBlank() }
                .map { Waypoint(it) }
    }
}
