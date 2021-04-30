package mono.graphics.bitmap.drawable

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Point
import kotlin.math.max
import kotlin.math.min

/**
 * A drawable to draw Line shape to bitmap.
 */
object LineDrawable {
    fun toBitmap(jointPoints: List<Point>): MonoBitmap {
        val bitmapBuilder = BitmapBuilderDecoration.getInstance(jointPoints)

        for (i in 0 until jointPoints.lastIndex) {
            val point0 = jointPoints[i]
            val point1 = jointPoints[i + 1]
            
            val char = if (isHorizontal(point0, point1)) '-' else '|'
            bitmapBuilder.fill(point0, point1, char)
        }

        return bitmapBuilder.toBitmap()
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

                return BitmapBuilderDecoration(boundLeft, boundTop, boundWidth, boundHeight)
            }
        }
    }
}
