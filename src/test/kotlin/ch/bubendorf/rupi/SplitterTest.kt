package ch.bubendorf.rupi

import org.junit.Test
import kotlin.test.assertEquals

class SplitterTest {

    val splitter = Splitter()

    val wp10_10 = Waypoint(10.010, 10.010, "WP10_10")
    val wp10_11 = Waypoint(10.011, 11.010, "WP10_11")
    val wp10_12 = Waypoint(10.012, 12.010, "WP10_12")
    val wp11_10 = Waypoint(11.010, 10.011, "WP11_10")
    val wp11_11 = Waypoint(11.011, 11.011, "WP11_11")
    val wp11_12 = Waypoint(11.012, 12.011, "WP11_12")
    val wp12_10 = Waypoint(12.010, 10.012, "WP12_10")
    val wp12_11 = Waypoint(12.011, 11.012, "WP12_11")
    val wp12_12 = Waypoint(12.012, 12.012, "WP12_12")

    @Test
    fun emptyList() {
        val list = splitter.split(arrayListOf(), 10, 5)

        assertEquals(1, list.size)
        assertEquals(0, list[0].size)
    }

    @Test
    fun oneElement() {
        val list = splitter.split(arrayListOf(wp10_10), 10, 5)

        assertEquals(1, list.size)
        assertEquals(1, list[0].size)
    }

    @Test
    fun nineElements() {
        val list = splitter.split(arrayListOf(wp10_10, wp10_11, wp10_12, wp11_10, wp11_11, wp11_12, wp12_10, wp12_11, wp12_12),
                5, 3)

        assertEquals(3, list.size)
        assertEquals(5, list[0].size)
        assertEquals(3, list[1].size)
        assertEquals(1, list[2].size)
    }

    @Test
    fun elevenElements() {
        val list = splitter.split(arrayListOf(wp10_10, wp10_10, wp10_11, wp10_11, wp10_12, wp11_10, wp11_11, wp11_12, wp12_10, wp12_11, wp12_12),
                5, 3)

        assertEquals(3, list.size)
        assertEquals(5, list[0].size)
        assertEquals(3, list[1].size)
        assertEquals(3, list[2].size)
    }

    @Test
    fun alwaysTheSame() {
        val list = splitter.split(arrayListOf(wp10_10, wp10_10, wp10_10, wp10_10, wp10_10, wp10_10, wp10_10, wp10_10, wp10_10, wp10_10, wp10_10),
                5, 3)

        assertEquals(3, list.size)
        assertEquals(5, list[0].size)
        assertEquals(3, list[1].size)
        assertEquals(3, list[2].size)
    }
}
