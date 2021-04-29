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

    @Test
    fun testReduce_noReduced() {
        // Empty
        listOf<Point>().let {
            assertEquals(it, LineHelper.reduce(it))
        }

        // 1 point
        listOf(Point(0, 0)).let {
            assertEquals(it, LineHelper.reduce(it))
        }

        // 2 points
        listOf(Point(0, 0), Point(0, 0)).let {
            assertEquals(it, LineHelper.reduce(it))
        }

        // Different lines
        listOf(Point(0, 0), Point(10, 0), Point(10, 10)).let {
            assertEquals(it, LineHelper.reduce(it))
        }

        // Same line but not monotonic: horizontal
        listOf(Point(0, 0), Point(10, 0), Point(5, 0)).let {
            assertEquals(it, LineHelper.reduce(it))
        }

        // Same line but not monotonic: vertical
        listOf(Point(0, 0), Point(0, 10), Point(0, 5)).let {
            assertEquals(it, LineHelper.reduce(it))
        }
    }

    @Test
    fun testReduce_reduced() {
        // Same points: Identical
        assertEquals(
            listOf(Point(0, 0), Point(0, 0)),
            LineHelper.reduce(
                listOf(
                    Point(0, 0),
                    Point(0, 0),
                    Point(0, 0)
                )
            )
        )

        // Same points: Same first
        assertEquals(
            listOf(Point(0, 0), Point(10, 0)),
            LineHelper.reduce(
                listOf(
                    Point(0, 0),
                    Point(0, 0),
                    Point(0, 0),
                    Point(0, 0),
                    Point(10, 0)
                )
            )
        )

        // Same points: Same middle
        assertEquals(
            listOf(Point(0, 0), Point(0, 10), Point(10, 10)),
            LineHelper.reduce(
                listOf(
                    Point(0, 0),
                    Point(0, 10),
                    Point(0, 10),
                    Point(0, 10),
                    Point(0, 10),
                    Point(10, 10)
                )
            )
        )

        // Same points: Same last
        assertEquals(
            listOf(Point(0, 0), Point(10, 0)),
            LineHelper.reduce(
                listOf(
                    Point(0, 0),
                    Point(10, 0),
                    Point(10, 0),
                    Point(10, 0),
                    Point(10, 0)
                )
            )
        )

        // Monotonic
        assertEquals(
            listOf(Point(0, 0), Point(10, 0)),
            LineHelper.reduce(
                listOf(Point(0, 0), Point(5, 0), Point(10, 0))
            )
        )

        // Monotonic 2
        assertEquals(
            listOf(Point(0, 0), Point(10, 0)),
            LineHelper.reduce(
                listOf(
                    Point(0, 0),
                    Point(2, 0),
                    Point(4, 0),
                    Point(6, 0),
                    Point(8, 0),
                    Point(10, 0)
                )
            )
        )
    }
}
