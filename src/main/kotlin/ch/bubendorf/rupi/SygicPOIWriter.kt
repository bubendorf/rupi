package ch.bubendorf.rupi

import java.io.FileOutputStream

class SygicPOIWriter(val name: String, val outputFile: String) {
    // name: Name der Kategorie. Auch die *.bmp Datei muss genau diesen Namen haben!
    fun write(waypoints: Collection<Waypoint>) {

        val out = PoiOutputStream()
        out.writeString("IPUR") // Magic number to identify the file as a RUPI file
        out.writeSwapInt(codePointCount(name));
        out.writeString("2000"); // File Format Version Number
        out.writeSwapInt(waypoints.size + 1);
        out.writeShort(compress(name.length))
        out.writeUnicodeString(name)
        out.write(1) // Oder 0x08 (Mehrteiliges RUPI?)
        out.write(0)
        out.write(0)
        out.write(0)

        val boundingBox = BoundingBox(waypoints)
        out.writeSwapInt(boundingBox.minLonInt)
        out.writeSwapInt(boundingBox.maxLatInt)
        out.writeSwapInt(boundingBox.maxLonInt)
        out.writeSwapInt(boundingBox.minLatInt)
        out.writeSwapInt(out.size() + 4 or Integer.MIN_VALUE)
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
            out.writeSwapInt(i + 1)
            out.writeSwapInt(codePointCount(name))
            out.write(1)
            out.writeSwapInt(i + 1)
            out.write(byteArrayOf(0.toByte(), 1.toByte(), 0.toByte(), 0.toByte(), 0.toByte()))
            out.writeSpecialStringWthLength(waypoint.name)
            out.writeSwapInt(waypoint.longitudeInt)
            out.writeSwapInt(waypoint.latitudeInt)

            out.writeStringWithLength(waypoint.address)
            out.writeInt(0x80)
            out.write(1)
            out.writeInt(0)
            out.writeInt(0x80)
            out.write(1)
            out.writeInt(0)
            out.writeInt(0x80)
            out.write(1)
            out.writeInt(0)
            out.writeInt(0x80)
            out.writeInt(0x80)
            out.write(1)
            out.writeInt(0)
            out.writeStringWithLength(waypoint.shortDescription)
            out.write(1)
            out.writeInt(0)
            out.writeStringWithLength(waypoint.longDescription)
            out.writeInt(-1)
            out.writeInt(-1)
            out.writeInt(0x80)
            out.writeInt(0x80)
            out.writeStringWithLength(waypoint.phone)
            out.writeStringWithLength(waypoint.fax)
            out.writeStringWithLength(waypoint.email)
            out.writeStringWithLength(waypoint.web)
            out.writeInt(0x80)
            out.writeInt(0x80)
            out.writeInt(0x80)
            out.writeInt(0)
            out.write(0)

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
