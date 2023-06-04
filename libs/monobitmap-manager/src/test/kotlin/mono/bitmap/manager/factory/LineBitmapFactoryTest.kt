/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.bitmap.manager.factory

import kotlin.test.Test
import kotlin.test.assertEquals
import mono.graphics.geo.Point
import mono.shape.extra.LineExtra
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle
import mono.shape.extra.style.AnchorChar
import mono.shape.extra.style.StraightStrokeDashPattern

/**
 * A test for [LineBitmapFactory].
 */
class LineBitmapFactoryTest {
    @Test
    fun testToBitmap_simpleHorizontalLine() {
        val points = listOf(
            Point(2, 0),
            Point(6, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals("0───1", bitmap.toString())
    }

    @Test
    fun testToBitmap_simpleHorizontalLine_reversed() {
        val points = listOf(
            Point(6, 0),
            Point(2, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals("1───0", bitmap.toString())
    }

    @Test
    fun testToBitmap_3StraightHorizontalMonotonicPoints() {
        val points = listOf(
            Point(0, 0),
            Point(2, 0),
            Point(4, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals("0───1", bitmap.toString())
    }

    @Test
    fun testToBitmap_3StraightHorizontalMonotonicPoints_reversed() {
        val points = listOf(
            Point(4, 0),
            Point(2, 0),
            Point(0, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals("1───0", bitmap.toString())
    }

    @Test
    fun testToBitmap_3StraightHorizontalNonMonotonicPoints() {
        val points = listOf(
            Point(0, 0),
            Point(4, 0),
            Point(2, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals("0─1──", bitmap.toString())
    }

    @Test
    fun testToBitmap_3StraightHorizontalNonMonotonicPoints_reversed() {
        val points = listOf(
            Point(4, 0),
            Point(0, 0),
            Point(2, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals("──1─0", bitmap.toString())
    }

    @Test
    fun testToBitmap_simpleVerticalLine() {
        val points = listOf(
            Point(0, 2),
            Point(0, 6)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
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
            Point(0, 6),
            Point(0, 2)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
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
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
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
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
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
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
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
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
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

    @Test
    fun testToBitmap_upperLeft() {
        val points = listOf(
            Point(0, 4),
            Point(4, 4),
            Point(4, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals(
            """
            |    1
            |    │
            |    │
            |    │
            |0───┘
            """.trimMargin(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_upperLeft_reversed() {
        val points = listOf(
            Point(4, 0),
            Point(4, 4),
            Point(0, 4)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals(
            """
            |    0
            |    │
            |    │
            |    │
            |1───┘
            """.trimMargin(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_lowerLeft() {
        val points = listOf(
            Point(0, 0),
            Point(4, 0),
            Point(4, 4)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals(
            """
            |0───┐
            |    │
            |    │
            |    │
            |    1
            """.trimMargin(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_lowerLeft_reversed() {
        val points = listOf(
            Point(4, 4),
            Point(4, 0),
            Point(0, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals(
            """
            |1───┐
            |    │
            |    │
            |    │
            |    0
            """.trimMargin(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_upperRight() {
        val points = listOf(
            Point(4, 4),
            Point(0, 4),
            Point(0, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals(
            """
            |1    
            |│    
            |│    
            |│    
            |└───0
            """.trimMargin(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_upperRight_reversed() {
        val points = listOf(
            Point(0, 0),
            Point(0, 4),
            Point(4, 4)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals(
            """
            |0    
            |│    
            |│    
            |│    
            |└───1
            """.trimMargin(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_lowerRight() {
        val points = listOf(
            Point(4, 0),
            Point(0, 0),
            Point(0, 4)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals(
            """
            |┌───0
            |│    
            |│    
            |│    
            |1    
            """.trimMargin(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_lowerRight_reversed() {
        val points = listOf(
            Point(0, 4),
            Point(0, 0),
            Point(4, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA)
        assertEquals(
            """
            |┌───1
            |│    
            |│    
            |│    
            |0    
            """.trimMargin(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_anchorChar_leftToRight() {
        val anchorCharStart = AnchorChar("id", "name", 'L', 'R', 'T', 'B')
        val anchorCharEnd = AnchorChar("id", "name", 'l', 'r', 't', 'b')
        val lineExtra = LINE_EXTRA.copy(
            userSelectedStartAnchor = anchorCharStart,
            userSelectedEndAnchor = anchorCharEnd
        )
        val points = listOf(
            Point(0, 0),
            Point(4, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, lineExtra)
        assertEquals(
            "L───r",
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_anchorChar_rightToLeft() {
        val anchorCharStart = AnchorChar("id", "name", 'L', 'R', 'T', 'B')
        val anchorCharEnd = AnchorChar("id", "name", 'l', 'r', 't', 'b')
        val lineExtra = LINE_EXTRA.copy(
            userSelectedStartAnchor = anchorCharStart,
            userSelectedEndAnchor = anchorCharEnd
        )
        val points = listOf(
            Point(4, 0),
            Point(0, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, lineExtra)
        assertEquals(
            "l───R",
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_anchorChar_topToBottom() {
        val anchorCharStart = AnchorChar("id", "name", 'L', 'R', 'T', 'B')
        val anchorCharEnd = AnchorChar("id", "name", 'l', 'r', 't', 'b')
        val lineExtra = LINE_EXTRA.copy(
            userSelectedStartAnchor = anchorCharStart,
            userSelectedEndAnchor = anchorCharEnd
        )
        val points = listOf(
            Point(0, 0),
            Point(0, 4)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, lineExtra)
        assertEquals(
            """
            T
            │
            │
            │
            b
            """.trimIndent(),
            bitmap.toString()
        )
    }

    @Test
    fun testToBitmap_anchorChar_bottomToTop() {
        val anchorCharStart = AnchorChar("id", "name", 'L', 'R', 'T', 'B')
        val anchorCharEnd = AnchorChar("id", "name", 'l', 'r', 't', 'b')
        val lineExtra = LINE_EXTRA.copy(
            userSelectedStartAnchor = anchorCharStart,
            userSelectedEndAnchor = anchorCharEnd
        )
        val points = listOf(
            Point(0, 4),
            Point(0, 0)
        )
        val bitmap = LineBitmapFactory.toBitmap(points, lineExtra)
        assertEquals(
            """
            t
            │
            │
            │
            B
            """.trimIndent(),
            bitmap.toString()
        )
    }

    companion object {
        private val LINE_EXTRA = LineExtra(
            isStrokeEnabled = true,
            userSelectedStrokeStyle = PredefinedStraightStrokeStyle.PREDEFINED_STYLES.first(),
            isStartAnchorEnabled = true,
            userSelectedStartAnchor = AnchorChar("id", "name", '0'),
            isEndAnchorEnabled = true,
            userSelectedEndAnchor = AnchorChar("id", "name", '1'),
            dashPattern = StraightStrokeDashPattern.SOLID,
            isRoundedCorner = false
        )
    }
}
