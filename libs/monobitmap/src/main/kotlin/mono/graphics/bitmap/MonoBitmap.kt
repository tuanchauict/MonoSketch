/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.bitmap

import mono.common.Characters.TRANSPARENT_CHAR
import mono.common.Characters.isHalfTransparent
import mono.common.Characters.isTransparent
import mono.graphics.geo.Rect
import mono.graphics.geo.Size

/**
 * A model class to hold the look of a shape after drawing.
 * Create new object via [Builder].
 */
class MonoBitmap private constructor(val matrix: List<Row>) {
    val size: Size = Size(
        width = matrix.firstOrNull()?.size ?: 0,
        height = matrix.size
    )

    fun isEmpty(): Boolean = size == Size.ZERO

    fun get(row: Int, column: Int): Char = matrix.getOrNull(row)?.get(column) ?: TRANSPARENT_CHAR

    override fun toString(): String =
        matrix.joinToString("\n")

    class Builder(private val width: Int, private val height: Int) {
        private val bound: Rect = Rect.byLTWH(0, 0, width, height)
        private val matrix: List<MutableList<Char>> = List(height) {
            MutableList(width) { TRANSPARENT_CHAR }
        }

        fun put(row: Int, column: Int, char: Char) {
            if (row in 0 until height && column in 0 until width) {
                matrix[row][column] = char
            }
        }

        fun fill(char: Char) {
            for (row in 0 until height) {
                for (col in 0 until width) {
                    matrix[row][col] = char
                }
            }
        }

        fun fill(row: Int, column: Int, bitmap: MonoBitmap) {
            if (bitmap.isEmpty()) {
                return
            }
            val inMatrix = bitmap.matrix

            val inMatrixBound = Rect.byLTWH(row, column, bitmap.size.width, bitmap.size.height)

            val overlap = bound.getOverlappedRect(inMatrixBound) ?: return
            val (startCol, startRow) = overlap.position - bound.position
            val (inStartCol, inStartRow) = overlap.position - inMatrixBound.position

            for (r in 0 until overlap.height) {
                val src = inMatrix[inStartRow + r]
                val dest = matrix[startRow + r]

                src.forEachIndex(inStartCol, inStartCol + overlap.width) { index, char ->
                    val destIndex = startCol + index
                    // char from source is always not transparent (0) due to the optimisation of Row
                    val isApplicable =
                        dest[destIndex].isTransparent && char.isHalfTransparent ||
                            !char.isHalfTransparent
                    if (isApplicable) {
                        dest[startCol + index] = char
                    }
                }
            }
        }

        fun toBitmap(): MonoBitmap = MonoBitmap(matrix.map { Row(it) })
    }

    class Row(chars: List<Char>) {
        internal val size: Int = chars.size
        private val sortedCells: List<Cell> = chars.mapIndexedNotNull { index, char ->
            if (!char.isTransparent) Cell(index, char) else null
        }

        fun forEachIndex(
            fromIndex: Int = 0,
            toExclusiveIndex: Int = size,
            action: (Int, Char) -> Unit
        ) {
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
