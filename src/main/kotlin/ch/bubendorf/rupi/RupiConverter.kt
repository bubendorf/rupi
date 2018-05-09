package ch.bubendorf.rupi

import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import java.io.File
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
        private var encoding: String = "iso-8859-1") {

    private val LOGGER = LoggerFactory.getLogger(RupiConverter::class.java.simpleName)

    fun convert() {
        // Erst mal die Parameter ergänzen falls diese leer sind
        val filename = FilenameUtils.getBaseName(inputFile)!!
        val filenameWOExtension = FilenameUtils.removeExtension(filename)!!
        val inputPath = FilenameUtils.getPath(inputFile)!!
        val categoryName = if (name != "") name else filenameWOExtension

        val outputFile = if (outputPath == "") {
            inputPath + filenameWOExtension
        } else {
            if (Files.isDirectory(Paths.get(outputPath))) {
                outputPath + File.separator + filenameWOExtension
            } else {
                outputPath
            }
        }
        LOGGER.info("RupiCreator Version ${BuildVersion.getBuildVersion()} - Start converting $categoryName")
        val startTime = System.currentTimeMillis()

        // Die Datei in den Speicher laden
        val waypoints = CsvReader(inputFile, encoding).read()
        val bb = BoundingBox(waypoints)
        LOGGER.debug("BoundingBox: $bb, width=${bb.getWidthtInMeters().toInt()}m, height=${bb.getHeightInMeters().toInt()}m")

        // Convert to RUPI and write everything to RUPI file
        val poiWriter = SygicPOIWriter(categoryName)
        poiWriter.convert(waypoints)
        poiWriter.writeToFile(outputFile + ".rupi")

        val durationMillis = System.currentTimeMillis() - startTime
        LOGGER.info("$categoryName-Converted ${waypoints.size} waypoints to ${outputFile + ".rupi"} in ${durationMillis}ms")
    }
}
