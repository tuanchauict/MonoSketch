package mono.bitmap.manager.factory

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Point
import mono.shape.extra.manager.model.AnchorChar
import mono.shape.shape.extra.LineExtra
import kotlin.math.max
import kotlin.math.min

/**
 * A drawable to draw Line shape to bitmap.
 */
object LineBitmapFactory {
    fun toBitmap(
        jointPoints: List<Point>,
        lineExtra: LineExtra,
    ): MonoBitmap {
        val bitmapBuilder = BitmapBuilderDecoration.getInstance(jointPoints)
        if (jointPoints.size < 2) {
            return bitmapBuilder.toBitmap()
        }

        for (i in 0 until jointPoints.lastIndex) {
            val point0 = jointPoints[i]
            val point1 = jointPoints[i + 1]

            val char = if (isHorizontal(point0, point1)) '─' else '│'
            bitmapBuilder.fill(point0, point1, char)
        }

        for (i in 1 until jointPoints.lastIndex) {
            val point0 = jointPoints[i - 1]
            val point1 = jointPoints[i]
            val point2 = jointPoints[i + 1]

            val rightAngle = getRightAngle(point0, point1, point2) ?: continue
            bitmapBuilder.put(point1.row, point1.column, rightAngle.char)
        }

        val startAnchor = lineExtra.startAnchor
        if (startAnchor != null) {
            bitmapBuilder.putAnchorPoint(
                jointPoints[0],
                jointPoints[1],
                startAnchor
            )
        }
        val endAnchor = lineExtra.endAnchor
        if (endAnchor != null) {
            bitmapBuilder.putAnchorPoint(
                jointPoints[jointPoints.lastIndex],
                jointPoints[jointPoints.lastIndex - 1],
                endAnchor
            )
        }

        return bitmapBuilder.toBitmap()
    }

    private fun getRightAngle(point0: Point, point1: Point, point2: Point): RightAngle? {
        if (isHorizontal(point0, point1) == isHorizontal(point1, point2)) {
            // Same line, ignore
            return null
        }

        val isLeft = point0.left < point1.left || point2.left < point1.left
        val isUpper = point0.top < point1.top || point2.top < point1.top

        return if (isLeft) {
            if (isUpper) RightAngle.UPPER_LEFT else RightAngle.LOWER_LEFT
        } else {
            if (isUpper) RightAngle.UPPER_RIGHT else RightAngle.LOWER_RIGHT
        }
    }

    private fun BitmapBuilderDecoration.putAnchorPoint(
        anchor: Point,
        previousPoint: Point,
        anchorChar: AnchorChar
    ) {
        val char = if (isHorizontal(anchor, previousPoint)) {
            if (anchor.left < previousPoint.left) anchorChar.left else anchorChar.right
        } else {
            if (anchor.top < previousPoint.top) anchorChar.top else anchorChar.bottom
        }
        put(anchor.row, anchor.column, char)
    }

    private fun isHorizontal(point1: Point, point2: Point): Boolean = point1.top == point2.top

    private class BitmapBuilderDecoration(
        private val row0: Int,
        private val column0: Int,
        width: Int,
        height: Int
    ) {
        private val builder = MonoBitmap.Builder(width, height)

        fun put(row: Int, column: Int, char: Char) =
            builder.put(row - row0, column - column0, char)

        /**
         * Fills the bound created by [point1] and [point2] including the edge with [char].
         */
        fun fill(point1: Point, point2: Point, char: Char) {
            val left = min(point1.left, point2.left)
            val right = max(point1.left, point2.left)
            val top = min(point1.top, point2.top)
            val bottom = max(point1.top, point2.top)

            for (row in top..bottom) {
                for (column in left..right) {
                    put(row, column, char)
                }
            }
        }

        fun toBitmap(): MonoBitmap = builder.toBitmap()

        companion object {
            fun getInstance(jointPoints: List<Point>): BitmapBuilderDecoration {
                val boundLeft = jointPoints.minOf { it.left }
                val boundRight = jointPoints.maxOf { it.left }
                val boundTop = jointPoints.minOf { it.top }
                val boundBottom = jointPoints.maxOf { it.top }

                val boundWidth = boundRight - boundLeft + 1
                val boundHeight = boundBottom - boundTop + 1

                return BitmapBuilderDecoration(boundTop, boundLeft, boundWidth, boundHeight)
            }
        }
    }

    private enum class RightAngle(val char: Char) {
        UPPER_LEFT('┘'),
        LOWER_LEFT('┐'),
        UPPER_RIGHT('└'),
        LOWER_RIGHT('┌');
    }
}
