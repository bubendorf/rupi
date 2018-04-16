package ch.bubendorf.rupi

import java.io.ByteArrayOutputStream
import java.io.IOException

class PoiOutputStream : ByteArrayOutputStream() {

    var position: Int
        get() = count
        set(value) {
            count = position
        }

    fun writeIndex(index: Int, value: Int): Int {
        val oldSize = size()
        count = index
        writeSwapInt(value)
        val newSize = size()
        count = oldSize
        return newSize
    }

    fun writeSpecialStringWthLength(text: String) {
        val bytes = text.toByteArray(charset("utf-8"))
        if (bytes.size > text.length) {
            write(15)
        } else {
            write(0)
        }
        writeSwapInt(bytes.size or Integer.MIN_VALUE)
        write(bytes)
    }

    fun writeCodePointCount(str: String) {
        var count = 0
        val codePointCount = str.codePointCount(0, str.length)
        for (i2 in 0 until codePointCount) {
            count = combine(count, str.codePointAt(i2))
        }
        writeSwapInt(count)
    }

    fun writeStringWithLength(text: String) {
        val bytes = text.toByteArray(charset("utf-8"))
        writeSwapInt(bytes.size or Integer.MIN_VALUE)
        write(bytes)
    }

    fun writeUnicodeString(text: String) {
        for (i in 0 until text.length) {
            writeShort(compress(text.codePointAt(i)))
        }
    }

    fun writeString(t: String) {
        write(t.toByteArray(Charsets.UTF_8))
    }

    fun writeShort(i: Int) {
        write(i.ushr(8) and 0xff)
        write(i.ushr(0) and 0xff)
    }

    fun writeSwapInt(i: Int) {
        writeInt(swap(i))
    }

    fun writeInt(i: Int) {
        write(i.ushr(24) and 0xff)
        write(i.ushr(16) and 0xff)
        write(i.ushr(8) and 0xff)
        write(i.ushr(0) and 0xff)
    }

    private fun swap(i: Int): Int {
        return i shr 24 and 255 or (65280 and (i shr 8)) or (16711680 and (i shl 8)) or (-16777216 and (i shl 24))
    }

    private fun compress(i: Int): Int {
        return i shr 8 and 0x00FF or (0xFF00 and (i shl 8))
    }

    private fun combine(i: Int, i2: Int): Int {
        val i3 = i shl 8
        return (i3 and 268435455) + i2 xor (-268435456 and i3).ushr(24)
    }

    fun writeBoundingBox(boundingBox: BoundingBox) {
        writeSwapInt(boundingBox.minLonInt - 1)
        writeSwapInt(boundingBox.maxLatInt + 1)
        writeSwapInt(boundingBox.maxLonInt + 1)
        writeSwapInt(boundingBox.minLatInt - 1)
    }


}
