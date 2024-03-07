/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.board

import mono.environment.Build

/**
 * An objects that defines resources for crossing.
 */
internal object CrossingResources {
    private val SINGLE_PAIRS = sequenceOf(
        "─━═",
        "│┃║",
        "┐┓╗",
        "┌┏╔",
        "┘┛╝",
        "└┗╚",
        "┬┳╦",
        "┴┻╩",
        "├┣╠",
        "┤┫╣",
        "┼╋╬"
    )
        .map { it.first() to it }
        .toMap()

    private val STANDARDIZED_CHARS = mapOf(
        '-' to '─',
        '|' to '│',
        '+' to '┼',
        '╮' to '┐',
        '╭' to '┌',
        '╯' to '┘',
        '╰' to '└'
    )

    private val CONNECTABLE_CHARS = "─│┌└┐┘┬┴├┤┼".extendChars().flatMap { it.toList() }.toSet()

    // TODO: Extend the list with complex combination chars.
    private val LEFT_IN_CHARS: Set<Char> = "─┌└┬┴├┼".extendChars().flatMap { it.toList() }.toSet()
    private val RIGHT_IN_CHARS: Set<Char> = "─┐┘┬┴┤┼".extendChars().flatMap { it.toList() }.toSet()
    private val TOP_IN_CHARS: Set<Char> = "│┌┐┬├┤┼".extendChars().flatMap { it.toList() }.toSet()
    private val BOTTOM_IN_CHARS: Set<Char> = "│└┘┴├┤┼".extendChars().flatMap { it.toList() }.toSet()

    private val SINGLE_CONNECTOR_CHAR_MAP = sequenceOf(
        "─│" to mapOf(
            inDirectionMark(hasRight = true, hasVertical = true) to '├',
            inDirectionMark(hasLeft = true, hasVertical = true) to '┤',
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼',
            inDirectionMark(hasHorizontal = true, hasTop = true) to '┴',
            inDirectionMark(hasHorizontal = true, hasBottom = true) to '┬'
        ),

        "─┌" to mapOf(
            inDirectionMark(hasRight = true, hasBottom = true) to '┌',
            inDirectionMark(hasHorizontal = true, hasBottom = true) to '┬'
        ),

        "─└" to mapOf(
            inDirectionMark(hasRight = true, hasTop = true) to '└',
            inDirectionMark(hasHorizontal = true, hasTop = true) to '┴'
        ),

        "─┐" to mapOf(
            inDirectionMark(hasLeft = true, hasBottom = true) to '┐',
            inDirectionMark(hasHorizontal = true, hasBottom = true) to '┬'
        ),

        "─┘" to mapOf(
            inDirectionMark(hasLeft = true, hasTop = true) to '┘',
            inDirectionMark(hasHorizontal = true, hasTop = true) to '┴'
        ),

        "─├" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼',
            inDirectionMark(hasVertical = true, hasRight = true) to '├'
        ),

        "─┤" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "─┴" to mapOf(
            inDirectionMark(hasHorizontal = true, hasTop = true) to '┴'
        ),

        "─┬" to mapOf(
            inDirectionMark(hasHorizontal = true, hasBottom = true) to '┬'
        ),

        "─┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "│┌" to mapOf(
            inDirectionMark(hasBottom = true, hasRight = true) to '┌',
            inDirectionMark(hasVertical = true, hasRight = true) to '├'
        ),

        "│└" to mapOf(
            inDirectionMark(hasTop = true, hasRight = true) to '└',
            inDirectionMark(hasVertical = true, hasRight = true) to '├'
        ),

        "│┐" to mapOf(
            inDirectionMark(hasBottom = true, hasLeft = true) to '┐',
            inDirectionMark(hasVertical = true, hasLeft = true) to '┤'
        ),

        "│┘" to mapOf(
            inDirectionMark(hasTop = true, hasLeft = true) to '┘',
            inDirectionMark(hasVertical = true, hasLeft = true) to '┤'
        ),

        "│├" to mapOf(
            inDirectionMark(hasVertical = true, hasRight = true) to '├'
        ),

        "│┤" to mapOf(
            inDirectionMark(hasVertical = true, hasLeft = true) to '┤'
        ),

        "│┴" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "│┬" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "│┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┌└" to mapOf(
            inDirectionMark(hasVertical = true, hasRight = true) to '├'
        ),

        "┌┘" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┌┐" to mapOf(
            inDirectionMark(hasHorizontal = true, hasBottom = true) to '┬'
        ),

        "┌├" to mapOf(
            inDirectionMark(hasVertical = true, hasRight = true) to '├'
        ),

        "┌┤" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┌┴" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┌┬" to mapOf(
            inDirectionMark(hasHorizontal = true, hasBottom = true) to '┬'
        ),

        "┌┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "└┐" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "└┘" to mapOf(
            inDirectionMark(hasHorizontal = true, hasTop = true) to '┴'
        ),

        "└├" to mapOf(
            inDirectionMark(hasVertical = true, hasRight = true) to '├'
        ),

        "└┤" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "└┴" to mapOf(
            inDirectionMark(hasHorizontal = true, hasTop = true) to '┴'
        ),

        "└┬" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "└┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┘┐" to mapOf(
            inDirectionMark(hasVertical = true, hasLeft = true) to '┤'
        ),

        "┘├" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┘┤" to mapOf(
            inDirectionMark(hasVertical = true, hasLeft = true) to '┤'
        ),

        "┘┴" to mapOf(
            inDirectionMark(hasHorizontal = true, hasTop = true) to '┴'
        ),

        "┘┬" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┘┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┐├" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┐┤" to mapOf(
            inDirectionMark(hasVertical = true, hasLeft = true) to '┤'
        ),

        "┐┴" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┐┬" to mapOf(
            inDirectionMark(hasHorizontal = true, hasBottom = true) to '┬'
        ),

        "┐┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "├┤" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "├┴" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "├┬" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "├┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┤┴" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┤┬" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┤┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┴┬" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┴┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        ),

        "┬┼" to mapOf(
            inDirectionMark(hasHorizontal = true, hasVertical = true) to '┼'
        )
    )
        .flatMap { (key, mark) ->
            key.extendChars().mapIndexed { index: Int, xKey: String ->
                xKey to mark.mapValues { (_, char) -> SINGLE_PAIRS[char]!![index] }
            }
        }
        .toMap()

    val Char.isConnectable: Boolean
        get() = standardize(this) in CONNECTABLE_CHARS

    val Char.hasLeft: Boolean
        get() = standardize(this) in LEFT_IN_CHARS

    val Char.hasRight: Boolean
        get() = standardize(this) in RIGHT_IN_CHARS

    val Char.hasTop: Boolean
        get() = standardize(this) in TOP_IN_CHARS

    val Char.hasBottom: Boolean
        get() = standardize(this) in BOTTOM_IN_CHARS

    private fun String.extendChars(): Sequence<String> =
        (0..2).asSequence()
            .map { getSingleKey(this, it) }

    private fun getSingleKey(key: String, index: Int): String =
        key.map { SINGLE_PAIRS[it]!![index] }
            .joinToString(separator = "")

    /**
     * A utility method for creating a mark vector for in-directions.
     */
    fun inDirectionMark(
        hasLeft: Boolean = false,
        hasRight: Boolean = false,
        hasTop: Boolean = false,
        hasBottom: Boolean = false,
        hasHorizontal: Boolean = false,
        hasVertical: Boolean = false
    ): Int {
        val leftMark = if (hasLeft || hasHorizontal) 0b1 else 0
        val rightMark = if (hasRight || hasHorizontal) 0b10 else 0
        val topMark = if (hasTop || hasVertical) 0b100 else 0
        val bottomMark = if (hasBottom || hasVertical) 0b1000 else 0
        return leftMark or topMark or rightMark or bottomMark
    }

    fun getDirectionMap(char1: Char, char2: Char): Map<Int, Char>? {
        val standardizedChar1 = standardize(char1)
        val standardizedChar2 = standardize(char2)

        return SINGLE_CONNECTOR_CHAR_MAP["$standardizedChar1$standardizedChar2"]
            ?: SINGLE_CONNECTOR_CHAR_MAP["$standardizedChar2$standardizedChar1"]
    }

    private const val MASK_SINGLE_LEFT = 0b0001
    private const val MASK_SINGLE_RIGHT = 0b0010
    private const val MASK_SINGLE_TOP = 0b0100
    private const val MASK_SINGLE_BOTTOM = 0b1000
    private const val MASK_SINGLE_HORIZONTAL = MASK_SINGLE_LEFT or MASK_SINGLE_RIGHT
    private const val MASK_SINGLE_VERTICAL = MASK_SINGLE_TOP or MASK_SINGLE_BOTTOM
    private const val MASK_SINGLE_CROSS = MASK_SINGLE_HORIZONTAL or MASK_SINGLE_VERTICAL

    private const val MASK_BOLD_LEFT = MASK_SINGLE_LEFT shl 4
    private const val MASK_BOLD_RIGHT = MASK_SINGLE_RIGHT shl 4
    private const val MASK_BOLD_TOP = MASK_SINGLE_TOP shl 4
    private const val MASK_BOLD_BOTTOM = MASK_SINGLE_BOTTOM shl 4
    private const val MASK_BOLD_HORIZONTAL = MASK_SINGLE_HORIZONTAL shl 4
    private const val MASK_BOLD_VERTICAL = MASK_SINGLE_VERTICAL shl 4
    private const val MASK_BOLD_CROSS = MASK_SINGLE_CROSS shl 4

    private const val MASK_DOUBLE_LEFT = MASK_SINGLE_LEFT shl 8
    private const val MASK_DOUBLE_RIGHT = MASK_SINGLE_RIGHT shl 8
    private const val MASK_DOUBLE_TOP = MASK_SINGLE_TOP shl 8
    private const val MASK_DOUBLE_BOTTOM = MASK_SINGLE_BOTTOM shl 8
    private const val MASK_DOUBLE_HORIZONTAL = MASK_SINGLE_HORIZONTAL shl 8
    private const val MASK_DOUBLE_VERTICAL = MASK_SINGLE_VERTICAL shl 8
    private const val MASK_DOUBLE_CROSS = MASK_SINGLE_CROSS shl 8

    private const val MASK_LEFT = MASK_SINGLE_LEFT or MASK_BOLD_LEFT or MASK_DOUBLE_LEFT
    private const val MASK_RIGHT = MASK_SINGLE_RIGHT or MASK_BOLD_RIGHT or MASK_DOUBLE_RIGHT
    private const val MASK_TOP = MASK_SINGLE_TOP or MASK_BOLD_TOP or MASK_DOUBLE_TOP
    private const val MASK_BOTTOM = MASK_SINGLE_BOTTOM or MASK_BOLD_BOTTOM or MASK_DOUBLE_BOTTOM
    private const val MASK_CROSS = MASK_SINGLE_CROSS or MASK_BOLD_CROSS or MASK_DOUBLE_CROSS

    private val CHAR_TO_MASK_MAP = mapOf(
        '─' to MASK_SINGLE_HORIZONTAL,
        '│' to MASK_SINGLE_VERTICAL,
        '┘' to (MASK_SINGLE_LEFT or MASK_SINGLE_TOP),
        '┐' to (MASK_SINGLE_LEFT or MASK_SINGLE_BOTTOM),
        '┤' to (MASK_SINGLE_LEFT or MASK_SINGLE_VERTICAL),
        '└' to (MASK_SINGLE_RIGHT or MASK_SINGLE_TOP),
        '┌' to (MASK_SINGLE_RIGHT or MASK_SINGLE_BOTTOM),
        '├' to (MASK_SINGLE_RIGHT or MASK_SINGLE_VERTICAL),
        '┴' to (MASK_SINGLE_HORIZONTAL or MASK_SINGLE_TOP),
        '┬' to (MASK_SINGLE_HORIZONTAL or MASK_SINGLE_BOTTOM),
        '┼' to (MASK_SINGLE_HORIZONTAL or MASK_SINGLE_VERTICAL),

        '━' to MASK_BOLD_HORIZONTAL,
        '┃' to MASK_BOLD_VERTICAL,
        '┛' to (MASK_BOLD_LEFT or MASK_BOLD_TOP),
        '┓' to (MASK_BOLD_LEFT or MASK_BOLD_BOTTOM),
        '┫' to (MASK_BOLD_LEFT or MASK_BOLD_VERTICAL),
        '┗' to (MASK_BOLD_RIGHT or MASK_BOLD_TOP),
        '┏' to (MASK_BOLD_RIGHT or MASK_BOLD_BOTTOM),
        '┣' to (MASK_BOLD_RIGHT or MASK_BOLD_VERTICAL),
        '┻' to (MASK_BOLD_HORIZONTAL or MASK_BOLD_TOP),
        '┳' to (MASK_BOLD_HORIZONTAL or MASK_BOLD_BOTTOM),
        '╋' to (MASK_BOLD_HORIZONTAL or MASK_BOLD_VERTICAL),

        '═' to MASK_DOUBLE_HORIZONTAL,
        '║' to MASK_DOUBLE_VERTICAL,
        '╝' to (MASK_DOUBLE_LEFT or MASK_DOUBLE_TOP),
        '╗' to (MASK_DOUBLE_LEFT or MASK_DOUBLE_BOTTOM),
        '╣' to (MASK_DOUBLE_LEFT or MASK_DOUBLE_VERTICAL),
        '╚' to (MASK_DOUBLE_RIGHT or MASK_DOUBLE_TOP),
        '╔' to (MASK_DOUBLE_RIGHT or MASK_DOUBLE_BOTTOM),
        '╠' to (MASK_DOUBLE_RIGHT or MASK_DOUBLE_VERTICAL),
        '╩' to (MASK_DOUBLE_HORIZONTAL or MASK_DOUBLE_TOP),
        '╦' to (MASK_DOUBLE_HORIZONTAL or MASK_DOUBLE_BOTTOM),
        '╬' to (MASK_DOUBLE_HORIZONTAL or MASK_DOUBLE_VERTICAL),

        // Complex (SINGLE, BOLD) combinations
        '╼' to (MASK_SINGLE_LEFT or MASK_BOLD_RIGHT),
        '╾' to (MASK_BOLD_LEFT or MASK_SINGLE_RIGHT),

        '╽' to (MASK_SINGLE_TOP or MASK_BOLD_BOTTOM),
        '╿' to (MASK_BOLD_TOP or MASK_SINGLE_BOTTOM),

        '┚' to (MASK_SINGLE_LEFT or MASK_BOLD_TOP),
        '┙' to (MASK_BOLD_LEFT or MASK_SINGLE_TOP),

        '┒' to (MASK_SINGLE_LEFT or MASK_BOLD_BOTTOM),
        '┑' to (MASK_BOLD_LEFT or MASK_SINGLE_BOTTOM),

        '┨' to (MASK_SINGLE_LEFT or MASK_BOLD_TOP or MASK_BOLD_BOTTOM),
        '┦' to (MASK_SINGLE_LEFT or MASK_BOLD_TOP or MASK_SINGLE_BOTTOM),
        '┧' to (MASK_SINGLE_LEFT or MASK_SINGLE_TOP or MASK_BOLD_BOTTOM),

        '┥' to (MASK_BOLD_LEFT or MASK_SINGLE_TOP or MASK_SINGLE_BOTTOM),
        '┩' to (MASK_BOLD_LEFT or MASK_BOLD_TOP or MASK_SINGLE_BOTTOM),
        '┪' to (MASK_BOLD_LEFT or MASK_SINGLE_TOP or MASK_BOLD_BOTTOM),

        '┖' to (MASK_SINGLE_RIGHT or MASK_BOLD_TOP),
        '┕' to (MASK_BOLD_RIGHT or MASK_SINGLE_TOP),

        '┎' to (MASK_SINGLE_RIGHT or MASK_BOLD_BOTTOM),
        '┍' to (MASK_BOLD_RIGHT or MASK_SINGLE_BOTTOM),

        '┠' to (MASK_SINGLE_RIGHT or MASK_BOLD_TOP or MASK_BOLD_BOTTOM),
        '┞' to (MASK_SINGLE_RIGHT or MASK_BOLD_TOP or MASK_SINGLE_BOTTOM),
        '┟' to (MASK_SINGLE_RIGHT or MASK_SINGLE_TOP or MASK_BOLD_BOTTOM),

        '┝' to (MASK_BOLD_RIGHT or MASK_SINGLE_TOP or MASK_SINGLE_BOTTOM),
        '┡' to (MASK_BOLD_RIGHT or MASK_BOLD_TOP or MASK_SINGLE_BOTTOM),
        '┢' to (MASK_BOLD_RIGHT or MASK_SINGLE_TOP or MASK_BOLD_BOTTOM),

        '┷' to (MASK_BOLD_LEFT or MASK_BOLD_RIGHT or MASK_SINGLE_TOP),
        '┶' to (MASK_SINGLE_LEFT or MASK_BOLD_RIGHT or MASK_SINGLE_TOP),
        '┵' to (MASK_BOLD_LEFT or MASK_SINGLE_RIGHT or MASK_SINGLE_TOP),

        '┸' to (MASK_SINGLE_LEFT or MASK_SINGLE_RIGHT or MASK_BOLD_TOP),
        '┹' to (MASK_BOLD_LEFT or MASK_SINGLE_RIGHT or MASK_BOLD_TOP),
        '┺' to (MASK_SINGLE_LEFT or MASK_BOLD_RIGHT or MASK_BOLD_TOP),

        '┯' to (MASK_BOLD_LEFT or MASK_BOLD_RIGHT or MASK_SINGLE_BOTTOM),
        '┭' to (MASK_BOLD_LEFT or MASK_SINGLE_RIGHT or MASK_SINGLE_BOTTOM),
        '┮' to (MASK_SINGLE_LEFT or MASK_BOLD_RIGHT or MASK_SINGLE_BOTTOM),

        '┰' to (MASK_SINGLE_LEFT or MASK_SINGLE_RIGHT or MASK_BOLD_BOTTOM),
        '┱' to (MASK_BOLD_LEFT or MASK_SINGLE_RIGHT or MASK_BOLD_BOTTOM),
        '┲' to (MASK_SINGLE_LEFT or MASK_BOLD_RIGHT or MASK_BOLD_BOTTOM),

        '┽' to (MASK_BOLD_LEFT or MASK_SINGLE_RIGHT or MASK_SINGLE_TOP or MASK_SINGLE_BOTTOM),
        '┾' to (MASK_SINGLE_LEFT or MASK_BOLD_RIGHT or MASK_SINGLE_TOP or MASK_SINGLE_BOTTOM),
        '╀' to (MASK_SINGLE_LEFT or MASK_SINGLE_RIGHT or MASK_BOLD_TOP or MASK_SINGLE_BOTTOM),
        '╁' to (MASK_SINGLE_LEFT or MASK_SINGLE_RIGHT or MASK_SINGLE_TOP or MASK_BOLD_BOTTOM),

        '╂' to (MASK_SINGLE_LEFT or MASK_SINGLE_RIGHT or MASK_BOLD_TOP or MASK_BOLD_BOTTOM),
        '┿' to (MASK_BOLD_LEFT or MASK_BOLD_RIGHT or MASK_SINGLE_TOP or MASK_SINGLE_BOTTOM),
        '╃' to (MASK_BOLD_LEFT or MASK_SINGLE_RIGHT or MASK_BOLD_TOP or MASK_SINGLE_BOTTOM),
        '╄' to (MASK_SINGLE_LEFT or MASK_BOLD_RIGHT or MASK_BOLD_TOP or MASK_SINGLE_BOTTOM),
        '╅' to (MASK_BOLD_LEFT or MASK_SINGLE_RIGHT or MASK_SINGLE_TOP or MASK_BOLD_BOTTOM),
        '╆' to (MASK_SINGLE_LEFT or MASK_BOLD_RIGHT or MASK_SINGLE_TOP or MASK_BOLD_BOTTOM),

        '╇' to (MASK_BOLD_LEFT or MASK_BOLD_RIGHT or MASK_BOLD_TOP or MASK_SINGLE_BOTTOM),
        '╈' to (MASK_BOLD_LEFT or MASK_BOLD_RIGHT or MASK_SINGLE_TOP or MASK_BOLD_BOTTOM),
        '╉' to (MASK_BOLD_LEFT or MASK_SINGLE_RIGHT or MASK_BOLD_TOP or MASK_BOLD_BOTTOM),
        '╊' to (MASK_SINGLE_LEFT or MASK_BOLD_RIGHT or MASK_BOLD_TOP or MASK_BOLD_BOTTOM),

        // Complex (SINGLE, DOUBLE) combinations
        '╒' to (MASK_DOUBLE_RIGHT or MASK_SINGLE_BOTTOM),
        '╓' to (MASK_SINGLE_RIGHT or MASK_DOUBLE_BOTTOM),

        '╕' to (MASK_DOUBLE_LEFT or MASK_SINGLE_BOTTOM),
        '╖' to (MASK_SINGLE_LEFT or MASK_DOUBLE_BOTTOM),

        '╘' to (MASK_DOUBLE_RIGHT or MASK_SINGLE_TOP),
        '╙' to (MASK_SINGLE_RIGHT or MASK_DOUBLE_TOP),

        '╛' to (MASK_SINGLE_LEFT or MASK_SINGLE_TOP),
        '╜' to (MASK_SINGLE_LEFT or MASK_DOUBLE_TOP),

        '╞' to (MASK_DOUBLE_RIGHT or MASK_SINGLE_TOP or MASK_SINGLE_BOTTOM),
        '╟' to (MASK_SINGLE_RIGHT or MASK_DOUBLE_TOP or MASK_DOUBLE_BOTTOM),

        '╡' to (MASK_DOUBLE_LEFT or MASK_SINGLE_TOP or MASK_SINGLE_BOTTOM),
        '╢' to (MASK_SINGLE_LEFT or MASK_DOUBLE_TOP or MASK_DOUBLE_BOTTOM),

        '╤' to (MASK_DOUBLE_LEFT or MASK_DOUBLE_RIGHT or MASK_SINGLE_BOTTOM),
        '╥' to (MASK_SINGLE_LEFT or MASK_SINGLE_RIGHT or MASK_DOUBLE_BOTTOM),

        '╧' to (MASK_DOUBLE_LEFT or MASK_DOUBLE_RIGHT or MASK_SINGLE_TOP),
        '╨' to (MASK_SINGLE_LEFT or MASK_SINGLE_RIGHT or MASK_DOUBLE_TOP),

        '╪' to (MASK_DOUBLE_LEFT or MASK_DOUBLE_RIGHT or MASK_SINGLE_TOP or MASK_SINGLE_BOTTOM),
        '╫' to (MASK_SINGLE_LEFT or MASK_SINGLE_RIGHT or MASK_DOUBLE_TOP or MASK_DOUBLE_BOTTOM)
    )

    private val MASK_TO_CHAR_MAP =
        CHAR_TO_MASK_MAP.entries.associate { (key, value) -> value to key }

    init {
        if (Build.DEBUG) {
            for ((key, value) in MASK_TO_CHAR_MAP) {
                console.log(value.toString(), maskToString(key))
            }
        }
    }

    fun getCrossingChar(
        char1: Char,
        surroundingLeft1: Char,
        surroundingRight1: Char,
        surroundingTop1: Char,
        surroundingBottom1: Char,
        char2: Char,
        surroundingLeft2: Char,
        surroundingRight2: Char,
        surroundingTop2: Char,
        surroundingBottom2: Char,
    ): Char? {
        val mask1 = getCharMask(char1, MASK_CROSS)
        val mask2 = getCharMask(char2, MASK_CROSS)

        val maskLeft =
            if (surroundingLeft1 in LEFT_IN_CHARS || surroundingLeft2 in LEFT_IN_CHARS) MASK_LEFT else 0
        val maskRight =
            if (surroundingRight1 in RIGHT_IN_CHARS || surroundingRight2 in RIGHT_IN_CHARS) MASK_RIGHT else 0
        val maskTop =
            if (surroundingTop1 in TOP_IN_CHARS || surroundingTop2 in TOP_IN_CHARS) MASK_TOP else 0
        val maskBottom =
            if (surroundingBottom1 in BOTTOM_IN_CHARS || surroundingBottom2 in BOTTOM_IN_CHARS) MASK_BOTTOM else 0

        val innerMask = mask1 or mask2
        val outerMask = maskLeft or maskRight or maskTop or maskBottom
        val mask = innerMask and outerMask

        if (Build.DEBUG) {
            console.log(
                listOf(
                    "$char1:${maskToString(mask1)}",
                    "$char2:${maskToString(mask2)}",
                    "-> ${maskToString(innerMask)}"
                ).toString(),
                listOf(
                    "$surroundingLeft1:$surroundingLeft2:${maskToString(maskLeft)}",
                    "$surroundingRight1:$surroundingRight2:${maskToString(maskRight)}",
                    "$surroundingTop1:$surroundingTop2:${maskToString(maskTop)}",
                    "$surroundingBottom1:$surroundingBottom2:${maskToString(maskBottom)}",
                    "-> ${maskToString(outerMask)}",
                ).toString(),
                maskToString(outerMask),
                "->",
                maskToString(mask),
                MASK_TO_CHAR_MAP[mask].toString()
            )
        }
        return MASK_TO_CHAR_MAP[mask]
    }

    private fun getCharMask(char: Char, mask: Int): Int {
        val charMask = CHAR_TO_MASK_MAP[char] ?: 0
        return charMask and mask
    }

    private fun standardize(char: Char): Char = STANDARDIZED_CHARS[char] ?: char

    private fun maskToString(mask: Int): String = mask.toString(2).padStart(12, '0')
}
