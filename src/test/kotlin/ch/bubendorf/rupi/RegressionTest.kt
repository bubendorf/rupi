package ch.bubendorf.rupi

import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RegressionTest {

    @Test
    fun rupiTest() {
        rupiTest("TestCSV", 4)
    }

    @Test
    fun rupiTest150() {
        rupiTest("RupiTest150", 150)
    }

    @Test
    fun rupiTest040() {
        rupiTest("RupiTest040", 40)
    }

    @Test
    fun umlauteISO8859() {
        rupiTest("umlauteISO8859", 4)
    }

    @Test
    fun umlauteUTF8() {
        rupiTest("umlauteUTF8", 4, "utf-8")
    }

    private fun rupiTest(filename: String, numberOfWaypoints: Int, encoding:String="iso8859-1") {
        val waypoints = CsvReader("src/test/resources/$filename.csv", encoding).read()
        assertEquals(numberOfWaypoints, waypoints.size)

        val poiWriter = SygicPOIWriter(filename)
        poiWriter.remark = "Rupi Creator by Markus Bubendorf"
        poiWriter.convert(waypoints)
        assertNotNull(poiWriter.byteArray)

        val allBytes = Files.readAllBytes(Paths.get("src/test/resources/$filename.rupi"))
        val equals = Arrays.equals(allBytes, poiWriter.byteArray)
        if (!equals) {
            poiWriter.writeToFile("src/test/resources/${filename}_New.rupi")
        }
        assertTrue(equals)
    }
}
