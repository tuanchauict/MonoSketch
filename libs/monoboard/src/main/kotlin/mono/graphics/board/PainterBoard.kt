package mono.graphics.board

import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size

/**
 * A model class to manage drawn pixel.
 * This is where a pixel is represented with its absolute position.
 */
internal class PainterBoard(internal val bound: Rect) {
    private val validColumnRange = 0 until bound.width
    private val validRowRange = 0 until bound.height

    private val matrix: List<List<Pixel>> = List(bound.height) {
        List(bound.width) { Pixel() }
    }

    fun clear() {
        for (row in matrix) {
            for (cell in row) {
                cell.reset()
            }
        }
    }

    /**
     * Fills with another [PainterBoard].
     * If a pixel in input [PainterBoard] is transparent, the value in the current board at that
     * position won't be overwritten.
     */
    fun fill(board: PainterBoard) {
        val position = board.bound.position
        val inMatrix = board.matrix

        if (matrix.isEmpty() || matrix.first().isEmpty()) {
            return
        }
        val inMatrixBound = Rect(position, Size(inMatrix.first().size, inMatrix.size))

        val overlap = bound.getOverlappedRect(inMatrixBound) ?: return
        val (startCol, startRow) = overlap.position - bound.position
        val (inStartCol, inStartRow) = overlap.position - position

        for (r in 0 until overlap.height) {
            val src = inMatrix[inStartRow + r]
            val dest = matrix[startRow + r]

            src.subList(inStartCol, inStartCol + overlap.width).forEachIndexed { index, pixel ->
                if (!pixel.isTransparent) {
                    dest[startCol + index].set(pixel.char, pixel.highlight)
                }
            }
        }
    }

    /**
     * Fills with a bitmap and the highlight state of that bitmap from [position].
     * If a pixel in input [bitmap] is transparent, the value in the current board at that
     * position won't be overwritten.
     */
    fun fill(position: Point, bitmap: MonoBitmap, highlight: Highlight) {
        if (bitmap.isEmpty()) {
            return
        }
        val inMatrix = bitmap.matrix

        val inMatrixBound = Rect(position, bitmap.size)

        val overlap = bound.getOverlappedRect(inMatrixBound) ?: return
        val (startCol, startRow) = overlap.position - bound.position
        val (inStartCol, inStartRow) = overlap.position - position

        for (r in 0 until overlap.height) {
            val src = inMatrix[inStartRow + r]
            val dest = matrix[startRow + r]

            src.forEachIndex(inStartCol, inStartCol + overlap.width) { index, char ->
                dest[startCol + index].set(char, highlight)
            }
        }
    }

    /**
     * Force values overlap with [rect] to be [char] regardless they are [TRANSPARENT_CHAR].
     *
     * Note: This method is for testing only
     */
    fun fill(rect: Rect, char: Char, highlight: Highlight) {
        val overlap = bound.getOverlappedRect(rect) ?: return
        val (startCol, startRow) = overlap.position - bound.position

        for (r in 0 until overlap.height) {
            val row = matrix[r + startRow]
            for (c in 0 until overlap.width) {
                row[c + startCol].set(char, highlight)
            }
        }
    }

    /**
     * Force value at [position] to be [char] with [highlight].
     *
     * Note: This method is for testing only
     */
    fun set(position: Point, char: Char, highlight: Highlight) =
        set(position.left, position.top, char, highlight)

    // This method is for testing only
    fun set(left: Int, top: Int, char: Char, highlight: Highlight) {
        val columnIndex = left - bound.left
        val rowIndex = top - bound.top
        if (columnIndex !in validColumnRange || rowIndex !in validRowRange) {
            return
        }
        matrix[rowIndex][columnIndex].set(char, highlight)
    }

    operator fun get(position: Point): Pixel? = get(position.left, position.top)

    fun get(left: Int, top: Int): Pixel? {
        val columnIndex = left - bound.left
        val rowIndex = top - bound.top
        return matrix.getOrNull(rowIndex)?.getOrNull(columnIndex)
    }

    override fun toString(): String =
        matrix.joinToString("\n", transform = ::toRowString)

    private fun toRowString(chars: List<Pixel>): String =
        chars.joinToString("") { it.toString() }
}
