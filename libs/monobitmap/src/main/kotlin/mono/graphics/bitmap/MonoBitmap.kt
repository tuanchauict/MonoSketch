/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.bitmap

import mono.common.Characters.TRANSPARENT_CHAR
import mono.common.Characters.isHalfTransparent
import mono.common.Characters.isTransparent
import mono.graphics.bitmap.MonoBitmap.Builder
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

    /**
     * A builder of the [MonoBitmap].
     */
    class Builder(private val width: Int, private val height: Int) {
        private val bound: Rect = Rect.byLTWH(0, 0, width, height)

        /**
         * A matrix for storing visual characters.
         * Except for crossing points, which is identified after combining some information from
         * the mono board, most of the visual characters will be displayed on the canvas.
         */
        private val visualMatrix: List<MutableList<Char>> = List(height) {
            MutableList(width) { TRANSPARENT_CHAR }
        }

        /**
         * A matrix for storing direction characters.
         * The direction characters are used for identifying the crossing points when painting on
         * the mono board.
         */
        private val directionMatrix: List<MutableList<Char>> = List(height) {
            MutableList(width) { TRANSPARENT_CHAR }
        }

        fun put(row: Int, column: Int, visualChar: Char, directionChar: Char) {
            if (row in 0 until height && column in 0 until width) {
                visualMatrix[row][column] = visualChar

                // The direction char is only override with non-transparent chars.
                if (directionChar != TRANSPARENT_CHAR) {
                    directionMatrix[row][column] = directionChar
                }
            }
        }

        fun fill(char: Char) {
            for (row in 0 until height) {
                for (col in 0 until width) {
                    visualMatrix[row][col] = char
                    // TODO: Delegate direction char
                    directionMatrix[row][col] = char
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
                val destVisual = visualMatrix[startRow + r]
                val destDirection = directionMatrix[startRow + r]

                src.forEachIndex(inStartCol, inStartCol + overlap.width) { index, char ->
                    val destIndex = startCol + index
                    // char from source is always not transparent (0) due to the optimisation of Row
                    val isVisualApplicable =
                        destVisual[destIndex].isTransparent && char.isHalfTransparent ||
                            !char.isHalfTransparent
                    if (isVisualApplicable) {
                        destVisual[startCol + index] = char
                    }

                    // TODO: Double check this condition
                    val isDirectionApplicable =
                        destDirection[destIndex].isTransparent && char.isHalfTransparent ||
                            !char.isHalfTransparent
                    if (isDirectionApplicable) {
                        destDirection[startCol + index] = char
                    }
                }
            }
        }

        fun toBitmap(): MonoBitmap {
            val rows = visualMatrix.mapIndexed { index: Int, chars ->
                Row(chars, directionMatrix[index])
            }
            return MonoBitmap(rows)
        }
    }

    /**
     * A lightweight data structure to represent a row of the matrix.
     * Only cells that having visible characters will be kept to save the memory. For example, with
     * inputs:
     * ```
     * ab      c
     * ```
     * only `[a, b, c]` is kept.
     */
    class Row internal constructor(chars: List<Char>, directionChars: List<Char>) {
        internal val size: Int = chars.size

        /**
         * A list of cells sorted by its [Cell.index].
         */
        private val sortedCells: List<Cell> = chars.mapIndexedNotNull { index, char ->
            if (!char.isTransparent) Cell(index, char, directionChars[index]) else null
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

    /**
     * A data class for a cell on the [Row].
     * Each cell contains:
     * - [index]: The index of the cell on the row. This is used for searching.
     * - [char]: The visual character of the cell which will be painted on the canvas.
     */
    private data class Cell(val index: Int, val char: Char, val directionChar: Char)
}
