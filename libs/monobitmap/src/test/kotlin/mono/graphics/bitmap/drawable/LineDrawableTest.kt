package mono.graphics.bitmap.drawable

import mono.graphics.geo.Point
import mono.shape.shape.Line
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [LineDrawable].
 */
class LineDrawableTest {
    @Test
    fun testToBitmap_simpleHorizontalLine() {
        val points = listOf(
            Point(0, 0),
            Point(4, 0)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals("0───1", bitmap.toString())
    }

    @Test
    fun testToBitmap_simpleHorizontalLine_reversed() {
        val points = listOf(
            Point(4, 0),
            Point(0, 0)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals("1───0", bitmap.toString())
    }

    @Test
    fun testToBitmap_3StraightHorizontalMonotonicPoints() {
        val points = listOf(
            Point(0, 0),
            Point(2, 0),
            Point(4, 0)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals("0───1", bitmap.toString())
    }

    @Test
    fun testToBitmap_3StraightHorizontalMonotonicPoints_reversed() {
        val points = listOf(
            Point(4, 0),
            Point(2, 0),
            Point(0, 0)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals("1───0", bitmap.toString())
    }

    @Test
    fun testToBitmap_3StraightHorizontalNonMonotonicPoints() {
        val points = listOf(
            Point(0, 0),
            Point(4, 0),
            Point(2, 0)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals("0─1──", bitmap.toString())
    }

    @Test
    fun testToBitmap_3StraightHorizontalNonMonotonicPoints_reversed() {
        val points = listOf(
            Point(4, 0),
            Point(0, 0),
            Point(2, 0)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals("──1─0", bitmap.toString())
    }

    @Test
    fun testToBitmap_simpleVerticalLine() {
        val points = listOf(
            Point(0, 0),
            Point(0, 4)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals(
            """
            0
            │
            │
            │
            1
            """.trimIndent(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_simpleVerticalLine_reversed() {
        val points = listOf(
            Point(0, 4),
            Point(0, 0)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals(
            """
            1
            │
            │
            │
            0
            """.trimIndent(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_3StraightVerticalMonotonicPoints() {
        val points = listOf(
            Point(0, 0),
            Point(0, 2),
            Point(0, 4)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals(
            """
            0
            │
            │
            │
            1
            """.trimIndent(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_3StraightVerticalMonotonicPoints_reversed() {
        val points = listOf(
            Point(0, 4),
            Point(0, 2),
            Point(0, 0)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals(
            """
            1
            │
            │
            │
            0
            """.trimIndent(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_3StraightVerticalNonMonotonicPoints() {
        val points = listOf(
            Point(0, 0),
            Point(0, 4),
            Point(0, 2)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals(
            """
            0
            │
            1
            │
            │
            """.trimIndent(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_3StraightVerticalNonMonotonicPoints_reversed() {
        val points = listOf(
            Point(0, 4),
            Point(0, 0),
            Point(0, 2)
        )
        val bitmap = LineDrawable.toBitmap(points, ANCHOR_CHAR_START, ANCHOR_CHAR_END)
        assertEquals(
            """
            │
            │
            1
            │
            0
            """.trimIndent(),
            bitmap.toString()
        )
    }

    companion object {
        private val ANCHOR_CHAR_START = Line.AnchorChar('0')
        private val ANCHOR_CHAR_END = Line.AnchorChar('1')
    }
}
