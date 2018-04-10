package ch.bubendorf.rupi

import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Konvertiert eine einzelne CSV Datei ins RUPI Format
 * @property name Der Name der POI Kategorie in der RUPI Datei. Falls leer, dann wird der Dateiname ohne Endung verwendet.
 * @property inputFile Der Name der CSV Datei. Muss gesetzt sein und die Datei muss existieren und muss lesbar sein.
 * @property outputPath Der Name der RUPI Datei oder des Verzeichnisses in welches die RUPI Datei geschrieben wird.
 * Der Dateiname wird in diesem Falle vom Namen von inputFile abgeleitet. Falls leer dann wird der Name von inputFile genommen
 * und die Erweiterung durch ".rupi" ersetzt.
 */
class RupiConverter(
        private var name: String = "",
        private var inputFile: String = "",
        private var outputPath: String = "",
        private val maxNumberOfWaypoints: Int = 1000) {

    private val LOGGER = LoggerFactory.getLogger(RupiConverter::class.java.simpleName)
    private var fileCount = 0

    fun convert() {
        // Erst mal die Parameter ergänzen falls diese leer sind
        val filename = FilenameUtils.getBaseName(inputFile)!!
        val filenameWOExtension = FilenameUtils.removeExtension(filename)!!
        val inputPath = FilenameUtils.getPath(inputFile)!!
        val categoryName = if (name != "") name else filenameWOExtension

        val outFile = if (outputPath == "") {
            inputPath + filenameWOExtension
        } else {
            if (Files.isDirectory(Paths.get(outputPath))) {
                outputPath + File.separator + filenameWOExtension
            } else {
                outputPath
            }
        }
        LOGGER.info("Converting $categoryName")

        // Die Datei in den Speicher laden
        val reader = InputStreamReader(FileInputStream(inputFile), "iso-8859-1")
        val lines = reader.readLines()
        reader.close()

        if (lines.size > 0) {
            // Aus der ersten Zeile den Delimiter lesen falls dieser gesetzt ist
            val firstLine = lines[0]
            val hasDelimiter = firstLine.startsWith("delimiter=")
            val linesToSkip = if (hasDelimiter) 1 else 0
            val delimiter = if (hasDelimiter) firstLine.substring(firstLine.indexOf("=") + 1)[0] else ';'

            // Zeile für Zeile in Waypoints konvertieren
            val waypoints = lines.drop(linesToSkip)
                    .map { it.split(delimiter) }
                    .filter { it.size >= 3 && it[0].isNotBlank() && it[1].isNotBlank() && it[2].isNotBlank() }
                    .map { Waypoint(it) }

            val bb = BoundingBox(waypoints)
            LOGGER.info("BoundingBox: $bb, width=${bb.getWidthtInMeters().toInt()}m, height=${bb.getHeightInMeters().toInt()}m")

            if (waypoints.size <= maxNumberOfWaypoints) {
                // Write everything to RUPI file
                SygicPOIWriter(categoryName, outFile + ".rupi").write(waypoints)
                LOGGER.info("$categoryName-Converted ${waypoints.size} waypoints to $outFile ($bb)")
            } else {
                // Split into multiple files!
                convert(categoryName, outFile, waypoints)
            }
        }
    }

    private fun convert(categoryName: String, outFile: String, waypoints: List<Waypoint>) {

        if (waypoints.size <= maxNumberOfWaypoints) {
            // Genug klein ==> Raus schreiben
            fileCount++
            val outputFile = outFile + "." + fileCount + ".rupi"
            SygicPOIWriter(categoryName, outputFile).write(waypoints)
            LOGGER.info("$categoryName-Converted ${waypoints.size} waypoints to $outputFile (${BoundingBox(waypoints)})")
        } else if (waypoints.size <= 2 * maxNumberOfWaypoints) {
            // Exakt Halbe-Halbe machen
            splitConvert(categoryName, outFile, waypoints, 0.5)
        } else if (waypoints.size <= 3 * maxNumberOfWaypoints) {
            // Ein Drittel, zwei Drittel machen
            splitConvert(categoryName, outFile, waypoints, 1.0 / 3)
        } else {
            // Auf  das nächste maxNumberOfWaypoints abgerundete verwenden
            val firstListSize = (waypoints.size / 2 / maxNumberOfWaypoints) * maxNumberOfWaypoints
            val splitPosition = firstListSize.toDouble() / waypoints.size
            splitConvert(categoryName, outFile, waypoints, splitPosition)
        }
    }

    private fun splitConvert(categoryName: String, outputFile: String, waypoints: List<Waypoint>, splitPosition: Double) {
        val bBox = BoundingBox(waypoints)
        LOGGER.debug("BBox=$bBox, size=${waypoints.size}")

        // Ja nach dem die Liste der Wegpunkte entlang der Latitude oder entlang der Longitude sortieren
        val heightInMeters = bBox.getHeightInMeters()
        val widthtInMeters = bBox.getWidthtInMeters()
        val sortedWaypoints = if (heightInMeters < widthtInMeters) {
            waypoints.sortedBy { coordinate -> coordinate.longitude }
        } else {
            waypoints.sortedBy { coordinate -> coordinate.latitude }
        }

        val splitIndex = (sortedWaypoints.size * splitPosition).toInt()
        val firstList = sortedWaypoints.subList(0, splitIndex)
        val secondList = sortedWaypoints.subList(splitIndex, waypoints.size)

        convert(categoryName, outputFile, firstList)
        convert(categoryName, outputFile, secondList)
    }
}
