package mono.graphics.board

import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size


internal class PainterBoard(private val bound: Rect) {
    private val validColumnRange = 0 until bound.width
    private val validRowRange = 0 until bound.height

    private val matrix: List<List<Pixel>> = List(bound.height) {
        List(bound.width) { Pixel() }
    }

    /**
     * Force values overlap with [rect] to be [char] regardless they are [TRANSPARENT_CHAR]
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
     * Fills with another [PainterBoard].
     * If a value in input [PainterBoard] is [TRANSPARENT_CHAR], the value in the current board at that
     * position won't be overlapped.
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
                if (pixel.char != TRANSPARENT_CHAR) {
                    dest[startCol + index].set(pixel.char, pixel.highlight)
                }
            }
        }
    }

    fun fill(position: Point, bitmap: MonoBitmap, highlight: Highlight) {
        val inMatrix = bitmap.matrix

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

            src.subList(inStartCol, inStartCol + overlap.width).forEachIndexed { index, char ->
                if (char != TRANSPARENT_CHAR) {
                    dest[startCol + index].set(char, highlight)
                }
            }
        }
    }

    /**
     * Force value at [position] to be [char] with [highlight]
     */
    fun set(position: Point, char: Char, highlight: Highlight) =
        set(position.left, position.top, char, highlight)

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
