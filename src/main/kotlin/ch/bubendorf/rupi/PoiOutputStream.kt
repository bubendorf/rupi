package ch.bubendorf.rupi

import java.io.ByteArrayOutputStream
import java.io.IOException

class PoiOutputStream() : ByteArrayOutputStream() {

    fun writeIndex(index: Int, value: Int): Int {
        val oldSize = size()
        if (index > oldSize + 4) {
            throw IOException("Rewrite isn't possible: index too large.")
        }
        count = index
        writeSwapInt(value)
        val newSize = size()
        count = oldSize
        return newSize
    }

    fun writeUTFStringWithLength(text: String) {
        val bytes = text.toByteArray(charset("utf-8"))
        if (bytes.size > text.length) {
            write(15)
        } else {
            write(0)
        }
        writeSwapInt(bytes.size or Integer.MIN_VALUE)
        write(bytes)
    }

    fun writeISOStringWithLength(text: String) {
//        val bytes = text.toByteArray(charset("iso-8859-1"))
        val bytes = text.toByteArray(charset("utf-8"))
        writeSwapInt(bytes.size or Integer.MIN_VALUE)
        write(bytes)
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
}
