package mono.graphics.bitmap.drawable

import mono.common.SpecialCharacters.EMPTY_CHAR
import mono.graphics.bitmap.MonoBitmap
import kotlin.math.max
import kotlin.math.min

/**
 * A simple 9-patch image which scales image based on repeating points in
 * [horizontalRepeatableRange] and [horizontalRepeatableRange].
 * [horizontalRepeatableRange] and [verticalRepeatableRange] must fit in the size of [pattern].
 */
class NinePatchDrawable(
    private val pattern: Pattern,
    private val horizontalRepeatableRange: RepeatableRange?,
    private val verticalRepeatableRange: RepeatableRange?
) {

    init {
        if (horizontalRepeatableRange?.verify(pattern.width) == false ||
            verticalRepeatableRange?.verify(pattern.height) == false
        ) {
            throw IllegalArgumentException(
                "Invalid repeat range(s). " +
                        "Horizontal range must in [0, ${pattern.width}); " +
                        "vertical range range must in [0, ${pattern.height})"
            )
        }
    }

    fun toBitmap(width: Int, height: Int): MonoBitmap {
        if (horizontalRepeatableRange == null && width != pattern.width) {
            throw IllegalArgumentException(
                "Horizontal side is not repeatable. " +
                        "Width must be ${pattern.width}"
            )
        }

        if (verticalRepeatableRange == null && height != pattern.height) {
            throw IllegalArgumentException(
                "Vertical side is not repeatable. " +
                        "Height must be ${pattern.height}"
            )
        }

        val builder = MonoBitmap.Builder(width, height)
        val rowIndexes = verticalRepeatableRange?.toIndexes(height, pattern.height)
            ?: (0 until pattern.height).toList()
        val colIndexes = horizontalRepeatableRange?.toIndexes(width, pattern.width)
            ?: (0 until pattern.width).toList()
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
            fun fromText(text: String, delimiters: String = "\n", emptyChar: Char = ' '): Pattern {
                val array = text.split(delimiters)
                if (array.isEmpty()) {
                    return Pattern(0, 0, emptyList())
                }
                val width = array.first().length
                val height = array.size
                val chars = array.joinToString("").map { if (it == emptyChar) EMPTY_CHAR else it }
                return Pattern(width, height, chars)
            }
        }
    }

    class RepeatableRange private constructor(
        start: Int,
        endInclusive: Int,
        private val strategy: Strategy
    ) {
        private val start: Int = min(start, endInclusive)
        private val endInclusive: Int = max(start, endInclusive)

        private val rangeSize: Int = endInclusive - start + 1

        internal fun verify(endExclusive: Int): Boolean = start >= 0 && endInclusive < endExclusive

        internal fun toIndexes(size: Int, patternSize: Int): List<Int> {
            val left = start
            val right = size - (patternSize - endInclusive)
            val repeatedSize = right - left + 1
            return List(size) { index ->
                when {
                    index < left -> index
                    index > right -> endInclusive + index - right
                    else -> when (strategy) {
                        Strategy.REPEAT -> left + (index - left) % rangeSize
                        Strategy.SCALE ->
                            left + ((index - left).toFloat() / repeatedSize * rangeSize).toInt()
                    }
                }
            }
        }

        companion object {
            fun repeat(start: Int, endInclusive: Int) =
                RepeatableRange(start, endInclusive, Strategy.REPEAT)

            fun scale(start: Int, endInclusive: Int) =
                RepeatableRange(start, endInclusive, Strategy.SCALE)
        }
    }

    /**
     * The algorithm for repeating the repeated range:
     * - [Strategy.REPEAT] `01` -> `01010101`
     * - [Strategy.SCALE] `01` -> `00001111`
     */
    private enum class Strategy {
        SCALE, REPEAT
    }
}
