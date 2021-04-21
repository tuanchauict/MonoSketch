package mono.shape.shape.line

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.DirectedPoint.Direction
import mono.graphics.geo.Point

/**
 * An utility object for line shape.
 */
internal object LineHelper {
    fun createJointPoints(seedPoints: List<DirectedPoint>): List<Point> {
        val mainPoints = createAnchorPoints(seedPoints)
        if (mainPoints.isEmpty()) {
            return emptyList()
        }
        val directedJointPoints = mutableListOf(mainPoints.first())
        for (i in 1 until mainPoints.size) {
            val startPoint = directedJointPoints.last()
            val endPoint = mainPoints[i]
            val middlePoint = when {
                startPoint.isOnSameStraightLine(endPoint) -> null
                startPoint.direction == Direction.HORIZONTAL ->
                    DirectedPoint(
                        endPoint.direction,
                        endPoint.left,
                        startPoint.top
                    )
                else ->
                    DirectedPoint(
                        endPoint.direction,
                        startPoint.left,
                        endPoint.top
                    )
            }
            if (middlePoint != null) {
                directedJointPoints.add(middlePoint)
            }
            directedJointPoints.add(endPoint)
        }
        val jointPoints = directedJointPoints.map { it.point }
        return reduce(jointPoints)
    }

    private fun createAnchorPoints(initAnchorPoints: List<DirectedPoint>): List<DirectedPoint> {
        if (initAnchorPoints.isEmpty()) {
            return emptyList()
        }
        val mainPoints = mutableListOf(initAnchorPoints.first())
        for (endPoint in initAnchorPoints.drop(1)) {
            val startPoint = mainPoints.last()
            if (startPoint.direction == endPoint.direction &&
                !startPoint.isOnSameStraightLine(endPoint)
            ) {
                val middleLeft = (startPoint.left + endPoint.left) / 2
                val middleTop = (startPoint.top + endPoint.top) / 2
                val middleDirection = startPoint.toRightAngleDirection()
                val middlePoint = DirectedPoint(middleDirection, middleLeft, middleTop)
                mainPoints.add(middlePoint)
            }
            mainPoints.add(endPoint)
        }
        return mainPoints
    }

    private fun reduce(points: List<Point>): List<Point> {
        if (points.size < 2) {
            return points
        }
        val list = mutableListOf<Point>()
        for (i in 1 until points.size) {
            val p1 = list.getOrNull(list.size - 2)
            val p2 = list.getOrNull(list.size - 1)
            val p3 = points[i]
            if (p1 == null || p2 == null || !isOnStraightLine(p1, p2, p3)) {
                list.add(p3)
            } else {
                list[list.size - 1] = p3
            }
        }

        val p1 = points.first()
        val p2 = list.getOrNull(0)
        val p3 = list.getOrNull(1)
        if (p2 == null || p3 == null || !isOnStraightLine(p1, p2, p3)) {
            list.add(0, p1)
        } else {
            list[0] = p1
        }
        return list
    }

    private fun isOnStraightLine(
        p1: Point,
        p2: Point,
        p3: Point,
        isInOrderedRequired: Boolean = true
    ): Boolean =
        if (isInOrderedRequired) {
            isEquals(p1.left, p2.left, p3.left) && isMonotonic(p1.top, p2.top, p3.top) ||
                isEquals(p1.top, p2.top, p3.top) && isMonotonic(p1.left, p2.left, p3.left)
        } else {
            isEquals(p1.left, p2.left, p3.left) || isEquals(p1.top, p2.top, p3.top)
        }

    private fun isEquals(a: Int, b: Int, c: Int): Boolean = a == b && b == c

    private fun isMonotonic(a: Int, b: Int, c: Int): Boolean =
        b in a..c || b in c..a

    private fun DirectedPoint.isOnSameStraightLine(another: DirectedPoint): Boolean =
        left == another.left || top == another.top

    private fun DirectedPoint.toRightAngleDirection(): Direction =
        if (direction == Direction.HORIZONTAL) Direction.VERTICAL else Direction.HORIZONTAL
}
