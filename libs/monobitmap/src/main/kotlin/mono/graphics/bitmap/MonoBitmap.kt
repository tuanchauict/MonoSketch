package mono.graphics.bitmap

import mono.common.Characters
import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.geo.Rect

/**
 * A model class to hold the look of a shape after drawing.
 * Create new object via [Builder].
 */
class MonoBitmap private constructor(val matrix: List<List<Char>>) {
    val width: Int = matrix.firstOrNull()?.size ?: 0
    val height: Int = matrix.size

    override fun toString(): String =
        matrix.joinToString("\n", transform = ::toRowString)

    private fun toRowString(chars: List<Char>): String =
        chars.joinToString("") { if (it == TRANSPARENT_CHAR) " " else it.toString() }

    class Builder(private val width: Int, private val height: Int) {
        private val matrix: List<MutableList<Char>> = List(height) {
            MutableList(width) { TRANSPARENT_CHAR }
        }
        private val bound: Rect = Rect.byLTWH(0, 0, width, height)

        fun put(row: Int, column: Int, char: Char) {
            if (row in 0 until height && column in 0 until width) {
                matrix[row][column] = char
            }
        }

        fun fill(rowOffset: Int, columnOffset: Int, bitmap: MonoBitmap) {
            val bitmapBound = Rect.byLTWH(columnOffset, rowOffset, bitmap.width, bitmap.height)
            val overlap = bound.getOverlappedRect(bitmapBound) ?: return
            val (startCol, startRow) = overlap.position - bound.position
            val (inStartCol, inStartRow) = overlap.position - bitmapBound.position
            for (r in 0 until overlap.height) {
                Characters.copyChars(
                    src = bitmap.matrix[inStartRow + r],
                    srcOffset = inStartCol,
                    dest = matrix[startRow + r],
                    destOffset = startCol,
                    length = overlap.width
                )
            }
        }

        fun toBitmap(): MonoBitmap = MonoBitmap(matrix)
    }
}
