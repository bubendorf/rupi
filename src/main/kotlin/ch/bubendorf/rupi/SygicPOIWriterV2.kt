package ch.bubendorf.rupi

import java.io.FileOutputStream

class SygicPOIWriterV2(
        private val categoryName: String,
        private val outputFile: String) {
    // categoryName: Name der Kategorie. Auch die *.bmp Datei muss genau diesen Namen haben!

    fun write(waypoints: List<Waypoint>) {

        val out = PoiOutputStream()
        out.writeString("IPUR") // Magic number to identify the file as a RUPI file
        out.writeSwapInt(codePointCount(categoryName))
        out.writeString("2000") // File Format Version Number
        out.writeSwapInt(waypoints.size + 1)
        out.writeShort(compress(categoryName.length))
        out.writeUnicodeString(categoryName)

        // Aufteilen der Waypoints!!
        if (waypoints.size < 128 + 16) {
            PoiBlock(categoryName, waypoints).write(out)
        } else {
            BBoxBlock(categoryName, waypoints).write(out)
        }

        // Write byte Array to disk
        val outStream = FileOutputStream(outputFile)
        outStream.write(out.toByteArray())
        outStream.close()
    }


    private fun combine(i: Int, i2: Int): Int {
        val i3 = i shl 8
        return (i3 and 268435455) + i2 xor (-268435456 and i3).ushr(24)
    }

    private fun compress(i: Int): Int {
        return i shr 8 and 0x00FF or (0xFF00 and (i shl 8))
    }

    private fun codePointCount(str: String): Int {
        var i = 0
        val codePointCount = str.codePointCount(0, str.length)
        for (i2 in 0 until codePointCount) {
            i = combine(i, str.codePointAt(i2))
        }
        return i
    }
}
