package ch.bubendorf.rupi

import java.io.ByteArrayOutputStream

class PoiOutputStream : ByteArrayOutputStream() {

    @Suppress("UNUSED_PARAMETER")
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
        for (position in 0 until codePointCount) {
            count = combine(count, str.codePointAt(position))
        }
        writeSwapInt(count)
    }

    fun writeStringWithLength(text: String) {
        val bytes = text.toByteArray(charset("utf-8"))
        writeSwapInt(bytes.size or Integer.MIN_VALUE)
        write(bytes)
    }

    fun writeUnicodeString(text: String) {
        for (i in text.indices) {
            writeSwapShort(text.codePointAt(i))
        }
    }

    fun writeString(t: String) {
        write(t.toByteArray(Charsets.UTF_8))
    }

    /**
     * Writes a Short in big endian to the OutputStream.
     * @param value The Short value to write
     */
    fun writeSwapShort(value: Int) {
        write(value)
        write(value.ushr(8))
    }

    /**
     * Writes an Integer in big endian to the OutputStream.
     * @param value The Integer value to write
     */
    fun writeSwapInt(value: Int) {
        write(value)
        write(value.ushr(8))
        write(value.ushr(16))
        write(value.ushr(24)
        )
    }

    /**
     * Writes an Integer in little endian to the OutputStream.
     * @param value The Integer value to write
     */
    fun writeInt(value: Int) {
        write(value.ushr(24))
        write(value.ushr(16))
        write(value.ushr(8))
        write(value)
    }

    private fun combine(val1: Int, val2: Int): Int {
        val val3 = val1 shl 8
        return (val3 and 0xFFFFFFF) + val2 xor (-0x10000000 and val3).ushr(24)
    }

    fun writeBoundingBox(boundingBox: BoundingBox) {
        writeSwapInt(boundingBox.minLonInt - 1)
        writeSwapInt(boundingBox.maxLatInt + 1)
        writeSwapInt(boundingBox.maxLonInt + 1)
        writeSwapInt(boundingBox.minLatInt - 1)
    }


}
