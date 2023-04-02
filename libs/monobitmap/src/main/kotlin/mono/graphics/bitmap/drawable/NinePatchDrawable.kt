/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.bitmap.drawable

import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.MonoBitmap
import kotlin.math.max
import kotlin.math.min

/**
 * A simple 9-patch image which scales image based on repeating points in
 * [horizontalRepeatableRange] and [horizontalRepeatableRange].
 */
class NinePatchDrawable(
    private val pattern: Pattern,
    private val horizontalRepeatableRange: RepeatableRange =
        RepeatableRange.Scale(0, pattern.width - 1),
    private val verticalRepeatableRange: RepeatableRange =
        RepeatableRange.Scale(0, pattern.height - 1)
) : Drawable {
    override fun toBitmap(width: Int, height: Int): MonoBitmap {
        val builder = MonoBitmap.Builder(width, height)
        val rowIndexes = verticalRepeatableRange.toIndexes(height, pattern.height)
        val colIndexes = horizontalRepeatableRange.toIndexes(width, pattern.width)
        for (row in 0 until height) {
            for (col in 0 until width) {
                builder.put(row, col, pattern.getChar(rowIndexes[row], colIndexes[col]))
            }
        }
        return builder.toBitmap()
    }

    /**
     * A data class which provides a basic render characters for 9-patch image.
     */
    data class Pattern(val width: Int, val height: Int, private val chars: List<Char>) {
        init {
            if (chars.size < width * height) {
                throw IllegalArgumentException("Mismatch between size and number of chars provided")
            }
        }

        internal fun getChar(row: Int, column: Int): Char {
            val index = row * width + column
            return chars[index]
        }

        companion object {
            fun fromText(
                text: String,
                delimiter: Char = '\n',
                transparentChar: Char = ' '
            ): Pattern {
                val array = text.split(delimiter)
                if (array.isEmpty()) {
                    return Pattern(0, 0, emptyList())
                }
                val width = array.first().length
                val height = array.size
                val chars = array.joinToString("")
                    .map { if (it == transparentChar) TRANSPARENT_CHAR else it }
                return Pattern(width, height, chars)
            }
        }
    }

    /**
     * The algorithm for repeating the repeated range.
     */
    sealed class RepeatableRange(
        start: Int,
        endInclusive: Int
    ) {
        private val start: Int = max(0, min(start, endInclusive))
        private val endInclusive: Int = max(start, endInclusive)

        /**
         * Creates a list with size [size] of indexes whole value in range [0, [patternSize]).
         * If [patternSize] < [endInclusive], [endInclusive] will be used for the index value range.
         */
        fun toIndexes(size: Int, patternSize: Int): List<Int> {
            val adjustedEndInclusive = min(patternSize - 1, endInclusive)
            val rangeSize = adjustedEndInclusive - start + 1
            val repeatingLeft = start
            val repeatingRight = size - (patternSize - adjustedEndInclusive)

            return List(size) { index ->
                when {
                    index < repeatingLeft -> index
                    index > repeatingRight -> adjustedEndInclusive + index - repeatingRight
                    else -> scaleIndex(index, repeatingLeft, repeatingRight, rangeSize)
                }
            }
        }

        protected abstract fun scaleIndex(
            index: Int,
            minRepeatingIndex: Int,
            maxRepeatingIndex: Int,
            rangeSize: Int
        ): Int

        /**
         * The algorithm for repeating the repeated range: `01` -> `00001111`
         */
        class Scale(start: Int, endInclusive: Int) : RepeatableRange(start, endInclusive) {

            override fun scaleIndex(
                index: Int,
                minRepeatingIndex: Int,
                maxRepeatingIndex: Int,
                rangeSize: Int
            ): Int {
                val repeatingSize = maxRepeatingIndex - minRepeatingIndex + 1
                return minRepeatingIndex + (index - minRepeatingIndex) * rangeSize / repeatingSize
            }
        }

        /**
         * The algorithm for repeating the repeated range: `01` -> `01010101`
         */
        class Repeat(start: Int, endInclusive: Int) : RepeatableRange(start, endInclusive) {

            override fun scaleIndex(
                index: Int,
                minRepeatingIndex: Int,
                maxRepeatingIndex: Int,
                rangeSize: Int
            ): Int = minRepeatingIndex + (index - minRepeatingIndex) % rangeSize
        }
    }
}
