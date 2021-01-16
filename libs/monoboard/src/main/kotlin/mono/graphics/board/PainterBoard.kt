package mono.graphics.board

import mono.common.Characters.TRANSPARENT_CHAR
import mono.common.Characters.copyChars
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size


internal class PainterBoard(private val bound: Rect) {
    private val validColumnRange = 0 until bound.width
    private val validRowRange = 0 until bound.height

    private val matrix: List<MutableList<Char>> = List(bound.height) {
        MutableList(bound.width) { TRANSPARENT_CHAR }
    }

    /**
     * Force values overlap with [rect] to be [char] regardless they are [TRANSPARENT_CHAR]
     */
    fun fill(rect: Rect, char: Char) {
        val overlap = bound.getOverlappedRect(rect) ?: return
        val (startCol, startRow) = overlap.position - bound.position

        for (r in 0 until overlap.height) {
            val row = matrix[r + startRow]
            for (c in 0 until overlap.width) {
                row[c + startCol] = char
            }
        }
    }

    /**
     * Fills with another [PainterBoard].
     * If a value in input [PainterBoard] is [TRANSPARENT_CHAR], the value in the current board at that
     * position won't be overlapped.
     */
    fun fill(board: PainterBoard) = fillWithMatrix(board.bound.position, board.matrix)

    fun fill(position: Point, bitmap: MonoBitmap) = fillWithMatrix(position, bitmap.matrix)

    private fun fillWithMatrix(position: Point, inMatrix: List<List<Char>>) {
        if (matrix.isEmpty() || matrix.first().isEmpty()) {
            return
        }
        val inMatrixBound = Rect(position, Size(inMatrix.first().size, inMatrix.size))

        val overlap = bound.getOverlappedRect(inMatrixBound) ?: return
        val (startCol, startRow) = overlap.position - bound.position
        val (inStartCol, inStartRow) = overlap.position - position
        for (r in 0 until overlap.height) {
            copyChars(
                src = inMatrix[inStartRow + r],
                srcOffset = inStartCol,
                dest = matrix[startRow + r],
                destOffset = startCol,
                length = overlap.width
            )
        }
    }

    /**
     * Force value at [position] to be [char]
     */
    operator fun set(position: Point, char: Char) = set(position.left, position.top, char)

    fun set(left: Int, top: Int, char: Char) {
        val columnIndex = left - bound.left
        val rowIndex = top - bound.top
        if (columnIndex !in validColumnRange || rowIndex !in validRowRange) {
            return
        }
        matrix[rowIndex][columnIndex] = char
    }

    operator fun get(position: Point): Char? = get(position.left, position.top)

    fun get(left: Int, top: Int): Char? {
        val columnIndex = left - bound.left
        val rowIndex = top - bound.top
        return matrix.getOrNull(rowIndex)?.getOrNull(columnIndex)
    }

    override fun toString(): String =
        matrix.joinToString("\n", transform = ::toRowString)

    private fun toRowString(chars: List<Char>): String =
        chars.joinToString("") { if (it == TRANSPARENT_CHAR) " " else it.toString() }
}
