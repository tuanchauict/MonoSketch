/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.board

/**
 * An objects that defines resources for crossing.
 */
internal object CrossingResources {
    private val CONNECTABLE_CHARS = "─│┌└┐┘┬┴├┤┼".toSet()

    private val LEFT_IN_CHARS = "─┌└┬┴├┼".toSet()
    private val RIGHT_IN_CHARS = "─┐┘┬┴┤┼".toSet()
    private val TOP_IN_CHARS = "│┌┐┬├┤┼".toSet()
    private val BOTTOM_IN_CHARS = "│└┘┴├┤┼".toSet()

    private val STANDARDIZED_CHARS = mapOf(
        '-' to '─',
        '|' to '│',
        '+' to '┼'
    )

    private val CONNECTOR_CHAR_MAP = mapOf(
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

        return CONNECTOR_CHAR_MAP["$standardizedChar1$standardizedChar2"]
            ?: CONNECTOR_CHAR_MAP["$standardizedChar2$standardizedChar1"]
    }

    private fun standardize(char: Char): Char = STANDARDIZED_CHARS[char] ?: char
}
