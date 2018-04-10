package ch.bubendorf.rupi

import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Konvertiert eine einzelne CSV Datei ins RUPI Format
 * @property name Der Name der POI Kategorie in der RUPI Datei. Falls leer, dann wird der Dateiname ohne Endung verwendet.
 * @property inputFile Der Name der CSV Datei. Muss gesetzt sein und die Datei muss existieren und muss lesbar sein.
 * @property outputFile Der Name der RUPI Datei oder des Verzeichnisses in welches die RUPI Datei geschrieben wird.
 * Der Dateiname wird in diesem Falle vom Namen von inputFile abgeleitet. Falls leer dann wird der Name von inputFile genommen
 * und die Erweiterung durch ".rupi" ersetzt.
 */
class RupiConverter(
        var name: String = "",
        var inputFile: String = "",
        var outputFile: String = "") {

    private val LOGGER = LoggerFactory.getLogger(RupiConverter::class.java.simpleName)

    fun convert() {
        // Erst mal die Parameter ergänzen falls diese leer sind
        val filename = FilenameUtils.getBaseName(inputFile)!!
        val filenameWOExtension = FilenameUtils.removeExtension(filename)!!
        val inputPath = FilenameUtils.getPath(inputFile)!!
        val categoryName = if (name != "") name else filenameWOExtension

        val outFile = if (outputFile == "") {
            inputPath + filenameWOExtension + ".rupi"
        } else {
            if (Files.isDirectory(Paths.get(outputFile))) {
                outputFile + File.separator + filenameWOExtension + ".rupi"
            } else {
                outputFile
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

            // Write to RUPI file
            SygicPOIWriter(categoryName, outFile).write(waypoints)
            val bb = BoundingBox(waypoints)
            LOGGER.info("$categoryName-Converted ${waypoints.size} waypoints to $outFile ($bb)")
        }
    }
}
