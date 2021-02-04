package mono.graphics.bitmap

import mono.common.Characters.TRANSPARENT_CHAR
import mono.common.Characters.isTransparent

/**
 * A model class to hold the look of a shape after drawing.
 * Create new object via [Builder].
 */
class MonoBitmap private constructor(val matrix: List<Row>) {
    val width: Int = matrix.firstOrNull()?.size ?: 0
    val height: Int = matrix.size

    fun get(row: Int, column: Int): Char = matrix.getOrNull(row)?.get(column) ?: TRANSPARENT_CHAR

    override fun toString(): String =
        matrix.joinToString("\n")

    class Builder(private val width: Int, private val height: Int) {
        private val matrix: List<MutableList<Char>> = List(height) {
            MutableList(width) { TRANSPARENT_CHAR }
        }

        fun put(row: Int, column: Int, char: Char) {
            if (row in 0 until height && column in 0 until width) {
                matrix[row][column] = char
            }
        }

        fun toBitmap(): MonoBitmap = MonoBitmap(matrix.map { Row(it) })
    }

    class Row(chars: List<Char>) {
        val size: Int = chars.size
        private val sortedCells: List<Cell> = chars.mapIndexedNotNull { index, char ->
            if (!char.isTransparent) Cell(index, char) else null
        }

        fun forEachIndex(fromIndex: Int, toExclusiveIndex: Int, action: (Int, Char) -> Unit) {
            val foundLow = sortedCells.binarySearch { it.index.compareTo(fromIndex) }
            val low = if (foundLow < 0) -foundLow - 1 else foundLow
            for (index in low until sortedCells.size) {
                val cell = sortedCells[index]
                if (cell.index >= toExclusiveIndex) {
                    break
                }
                action(cell.index - fromIndex, cell.char)
            }
        }

        internal fun get(column: Int): Char {
            val index = sortedCells.binarySearch { it.index.compareTo(column) }
            return if (index >= 0) sortedCells[index].char else TRANSPARENT_CHAR
        }

        override fun toString(): String {
            val list = MutableList(size) { ' ' }
            for (cell in sortedCells) {
                list[cell.index] = cell.char
            }
            return list.joinToString("")
        }
    }

    private data class Cell(val index: Int, val char: Char)
}
