package ch.bubendorf.rupi

import java.io.FileOutputStream

/**
 *  @param categoryName: Name der Kategorie. Auch die *.bmp Datei muss genau diesen Namen haben!
 */
class SygicPOIWriter(
        private val categoryName: String) {

    var byteArray: ByteArray? = null
    var remark = ""

    fun convert(waypoints: List<Waypoint>) {

        val out = PoiOutputStream()
        out.writeString("IPUR") // Magic number to identify the file as a RUPI file
        out.writeCodePointCount(categoryName)
        out.writeString("2000") // File Format Version Number
        out.writeSwapInt(waypoints.size + 1)
        out.writeSwapShort(categoryName.length)
        out.writeUnicodeString(categoryName)

        BBoxBlock(categoryName, waypoints, remark).write(out)
        byteArray = out.toByteArray()
    }

    // Write byte Array to disk
    fun writeToFile(outputFile: String) {
        val outStream = FileOutputStream(outputFile)
        outStream.write(byteArray)
        outStream.close()
    }
}
