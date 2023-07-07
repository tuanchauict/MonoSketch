/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.connector

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.PointF
import mono.graphics.geo.Rect
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text

/**
 * A Use case object for shape connector
 */
object ShapeConnectorUseCase {
    private const val MAX_DISTANCE = 1

    fun getConnectableShape(
        point: Point,
        candidates: Sequence<AbstractShape>
    ): AbstractShape? = candidates.lastOrNull { canConnect(point, it) }

    private fun canConnect(point: Point, shape: AbstractShape): Boolean {
        val bound = when (shape) {
            is Rectangle -> shape.bound
            is Text -> shape.bound
            is Group,
            is Line,
            is MockShape -> null
        } ?: return false

        return point.detectAround(bound).any { it }
    }

    fun getAround(
        anchorPoint: DirectedPoint,
        boxBound: Rect
    ): Around? {
        val (isAroundLeft, isAroundRight, isAroundTop, isAroundBottom) =
            anchorPoint.point.detectAround(boxBound)

        val isHorizontal = anchorPoint.direction == DirectedPoint.Direction.HORIZONTAL
        return when {
            isAroundLeft -> {
                when {
                    isAroundTop && !isHorizontal -> Around.TOP
                    isAroundBottom && !isHorizontal -> Around.BOTTOM
                    else -> Around.LEFT
                }
            }

            isAroundTop -> {
                when {
                    isAroundRight && isHorizontal -> Around.RIGHT
                    else -> Around.TOP
                }
            }

            isAroundRight -> {
                when {
                    isAroundBottom && !isHorizontal -> Around.BOTTOM
                    else -> Around.RIGHT
                }
            }

            isAroundBottom -> Around.BOTTOM

            else -> null
        }
    }

    /**
     * Returns a list contains exactly 4 items which are the result of detecting around the
     * [boxBound] in order [left, right, top, bottom].
     */
    private fun Point.detectAround(boxBound: Rect): List<Boolean> {
        val isAroundLeft = left.isAround(boxBound.left) &&
            top.isAround(boxBound.top, boxBound.bottom)
        val isAroundRight = left.isAround(boxBound.right) &&
            top.isAround(boxBound.top, boxBound.bottom)
        val isAroundTop = top.isAround(boxBound.top) &&
            left.isAround(boxBound.left, boxBound.right)
        val isAroundBottom = top.isAround(boxBound.bottom) &&
            left.isAround(boxBound.left, boxBound.right)
        return listOf(isAroundLeft, isAroundRight, isAroundTop, isAroundBottom)
    }

    private fun Int.isAround(
        lower: Int,
        upper: Int = lower,
        distance: Int = MAX_DISTANCE
    ): Boolean = this in (lower - distance)..(upper + distance)

    fun calculateRatio(around: Around, anchorPoint: DirectedPoint, boxBound: Rect): PointF {
        val leftRatio =
            (anchorPoint.left - boxBound.left).toDouble() / boxBound.width.adjustSizeValue()
        val topRatio =
            (anchorPoint.top - boxBound.top).toDouble() / boxBound.height.adjustSizeValue()
        return when (around) {
            Around.LEFT -> PointF(left = 0.0, top = topRatio.coerceIn(0.0, 1.0))
            Around.TOP -> PointF(left = leftRatio.coerceIn(0.0, 1.0), top = 0.0)
            Around.RIGHT -> PointF(left = 1.0, top = topRatio.coerceIn(0.0, 1.0))
            Around.BOTTOM -> PointF(left = leftRatio.coerceIn(0.0, 1.0), top = 1.0)
        }
    }

    fun calculateOffset(around: Around, anchorPoint: DirectedPoint, boxBound: Rect): Point =
        when (around) {
            Around.LEFT -> Point(
                left = anchorPoint.left - boxBound.left,
                top = anchorPoint.top.offsetToRange(boxBound.top, boxBound.bottom)
            )

            Around.TOP -> Point(
                left = anchorPoint.left.offsetToRange(boxBound.left, boxBound.right),
                top = anchorPoint.top - boxBound.top
            )

            Around.RIGHT -> Point(
                left = anchorPoint.left - boxBound.right,
                top = anchorPoint.top.offsetToRange(boxBound.top, boxBound.bottom)
            )

            Around.BOTTOM -> Point(
                left = anchorPoint.left.offsetToRange(boxBound.left, boxBound.right),
                top = anchorPoint.top - boxBound.bottom
            )
        }

    fun LineConnector.getPointInNewBound(
        direction: DirectedPoint.Direction,
        boxBound: Rect
    ): DirectedPoint =
        DirectedPoint(direction, getLeftInNewBound(boxBound), getTopInNewBound(boxBound))

    private fun LineConnector.getLeftInNewBound(boxBound: Rect): Int =
        (boxBound.left + (boxBound.width - 1) * ratio.left + offset.left).toInt()

    private fun LineConnector.getTopInNewBound(boxBound: Rect): Int =
        (boxBound.top + (boxBound.height - 1) * ratio.top + offset.top).toInt()

    private fun Int.offsetToRange(lower: Int, upper: Int): Int = when {
        this < lower -> this - lower
        this > upper -> this - upper
        else -> 0
    }

    private fun Int.adjustSizeValue(): Int = (this - 1).coerceAtLeast(1)

    enum class Around {
        LEFT, TOP, RIGHT, BOTTOM
    }
}
