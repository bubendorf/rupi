package ch.bubendorf.rupi

import java.io.FileOutputStream

class SygicPOIWriterV1(
        private val categoryName: String,
        private val outputFile: String) {
    // categoryName: Name der Kategorie. Auch die *.bmp Datei muss genau diesen Namen haben!
    fun write(waypoints: Collection<Waypoint>) {

        val out = PoiOutputStream()
        out.writeString("IPUR") // Magic number to identify the file as a RUPI file
        out.writeSwapInt(codePointCount(categoryName))
        out.writeString("2000") // File Format Version Number
        out.writeSwapInt(waypoints.size + 1)
        out.writeShort(compress(categoryName.length))
        out.writeUnicodeString(categoryName)
        out.write(1) // Anzahl der Bounding Boxes (1, 2, 4, 7 oder 8)!
        out.write(0)
        out.write(0)
        out.write(0)

        val boundingBox = BoundingBox(waypoints)
        writeBoundingBox(out, boundingBox)
        out.writeSwapInt(out.size() + 4 or Integer.MIN_VALUE) // Index zu den Indizes dieser BBox?
        out.writeSwapInt(waypoints.size)

        // Make room for the index
        var position = out.size()
        for (i in 0 until waypoints.size) {
            out.writeInt(0)
        }

        val offsets = IntArray(waypoints.size)
        var i = 0
        for (waypoint in waypoints) {
            offsets[i] = out.size()
            writeWaypoint(out, i, waypoint)

            i++
        }

        // Write Index
        for (offset in offsets) {
            position = out.writeIndex(position, offset)
        }
        val outStream = FileOutputStream(outputFile)
        outStream.write(out.toByteArray())
        outStream.close()
    }

    private fun writeBoundingBox(outputStream: PoiOutputStream, boundingBox: BoundingBox) {
        outputStream.writeSwapInt(boundingBox.minLonInt)
        outputStream.writeSwapInt(boundingBox.maxLatInt)
        outputStream.writeSwapInt(boundingBox.maxLonInt)
        outputStream.writeSwapInt(boundingBox.minLatInt)
    }

    private fun writeWaypoint(outputStream: PoiOutputStream, number: Int, waypoint: Waypoint) {
        outputStream.writeSwapInt(number + 1)
        outputStream.writeSwapInt(codePointCount(categoryName))
        outputStream.write(1)
        outputStream.writeSwapInt(number + 1)
        outputStream.write(byteArrayOf(0.toByte(), 1.toByte(), 0.toByte(), 0.toByte(), 0.toByte()))
        outputStream.writeSpecialStringWthLength(waypoint.name)
        outputStream.writeSwapInt(waypoint.longitudeInt)
        outputStream.writeSwapInt(waypoint.latitudeInt)

        outputStream.writeStringWithLength(waypoint.address)
        outputStream.writeInt(0x80)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeInt(0x80)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeInt(0x80)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0x80)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeStringWithLength(waypoint.shortDescription)
        outputStream.write(1)
        outputStream.writeInt(0)
        outputStream.writeStringWithLength(waypoint.longDescription)
        outputStream.writeInt(-1)
        outputStream.writeInt(-1)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0x80)
        outputStream.writeStringWithLength(waypoint.phone)
        outputStream.writeStringWithLength(waypoint.fax)
        outputStream.writeStringWithLength(waypoint.email)
        outputStream.writeStringWithLength(waypoint.web)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0x80)
        outputStream.writeInt(0)
        outputStream.write(0)
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
