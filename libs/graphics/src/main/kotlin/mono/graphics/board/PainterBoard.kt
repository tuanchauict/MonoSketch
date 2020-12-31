package mono.graphics.board

import mono.graphics.geo.Point
import mono.graphics.geo.Rect


internal class PainterBoard(private val bound: Rect) {
    private val validColumnRange = 0 until bound.width
    private val validRowRange = 0 until bound.height

    private val matrix: List<MutableList<Char>> = List(bound.height) {
        MutableList(bound.width) { EMPTY_CHAR }
    }

    fun fill(rect: Rect, char: Char) {
        val overlap = bound.getOverlappedRect(rect)
        val (startCol, startRow) = overlap.position - bound.position

        for (r in 0 until overlap.height) {
            val row = matrix[r + startRow]
            for (c in 0 until overlap.width) {
                row[c + startCol] = char
            }
        }
    }

    fun fill(board: PainterBoard) {
        val overlap = bound.getOverlappedRect(board.bound)
        val (startCol, startRow) = overlap.position - bound.position
        val (inStartCol, inStartRow) = overlap.position - board.bound.position

        for (r in 0 until overlap.height) {
            val rowIndex = startRow + r
            val inRowIndex = inStartRow + r
            val row = matrix[rowIndex]
            val inRow = board.matrix[inRowIndex]
            for (c in 0 until overlap.width) {
                val colIndex = startCol + c
                val inColIndex = inStartCol + c
                row[colIndex] = inRow[inColIndex]
            }
        }
    }

    operator fun set(position: Point, char: Char) {
        val (columnIndex, rowIndex) = position - bound.position
        if (columnIndex !in validColumnRange || rowIndex !in validRowRange) {
            return
        }
        matrix[rowIndex][columnIndex] = char
    }

    operator fun get(position: Point): Char? {
        val (columnIndex, rowIndex) = position - bound.position
        if (rowIndex !in validRowRange || columnIndex !in validColumnRange) {
            return null
        }
        return matrix[rowIndex][columnIndex]
    }

    override fun toString(): String =
        matrix.joinToString("\n", transform = ::toRowString) + "\n"

    private fun toRowString(chars: List<Char>): String =
        chars.joinToString("") { if (it == EMPTY_CHAR) " " else it.toString() }

    companion object {
        const val EMPTY_CHAR = 0.toChar()
    }
}
