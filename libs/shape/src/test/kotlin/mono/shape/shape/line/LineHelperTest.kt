package mono.shape.shape.line

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [LineHelper].
 */
class LineHelperTest {
    @Test
    fun testCreateJointPoints_sameHorizontalLine_HH() {
        val p1 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 10, 0)

        assertEquals(
            listOf(
                Point(0, 0),
                Point(10, 0)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_sameHorizontalLine_HV() {
        val p1 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 10, 0)

        assertEquals(
            listOf(
                Point(0, 0),
                Point(10, 0)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_sameHorizontalLine_VV() {
        val p1 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 10, 0)

        assertEquals(
            listOf(
                Point(0, 0),
                Point(10, 0)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_sameHorizontalLine_VH() {
        val p1 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 10, 0)

        assertEquals(
            listOf(
                Point(0, 0),
                Point(10, 0)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }


    @Test
    fun testCreateJointPoints_sameVerticalLine_HH() {
        val p1 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 0, 10)

        assertEquals(
            listOf(
                Point(0, 0),
                Point(0, 10)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_sameVerticalLine_HV() {
        val p1 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 0, 10)

        assertEquals(
            listOf(
                Point(0, 0),
                Point(0, 10)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_sameVerticalLine_VV() {
        val p1 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 0, 10)

        assertEquals(
            listOf(
                Point(0, 0),
                Point(0, 10)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_sameVerticalLine_VH() {
        val p1 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 0, 10)

        assertEquals(
            listOf(
                Point(0, 0),
                Point(0, 10)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_differentLines_HH() {
        val p1 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 10, 10)

        assertEquals(
            listOf(
                Point(left = 0, top = 0),
                Point(left = 5, top = 0),
                Point(left = 5, top = 10),
                Point(left = 10, top = 10)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_differentLines_HV() {
        val p1 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 10, 10)

        assertEquals(
            listOf(
                Point(left = 0, top = 0),
                Point(left = 10, top = 0),
                Point(left = 10, top = 10)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_differentLines_VV() {
        val p1 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 10, 10)
        assertEquals(
            listOf(
                Point(left = 0, top = 0),
                Point(left = 0, top = 5),
                Point(left = 10, top = 5),
                Point(left = 10, top = 10)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }

    @Test
    fun testCreateJointPoints_differentLines_VH() {
        val p1 = DirectedPoint(DirectedPoint.Direction.VERTICAL, 0, 0)
        val p2 = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 10, 10)
        println(LineHelper.createJointPoints(listOf(p1, p2)))
        assertEquals(
            listOf(
                Point(left = 0, top = 0),
                Point(left = 0, top = 10),
                Point(left = 10, top = 10)
            ),
            LineHelper.createJointPoints(listOf(p1, p2))
        )
    }
}
