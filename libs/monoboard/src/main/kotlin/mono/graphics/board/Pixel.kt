/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.board

import mono.common.Characters.TRANSPARENT_CHAR
import mono.common.Characters.isHalfTransparent
import mono.common.Characters.isTransparent

/**
 * A class represents each item on the board matrix.
 */
class Pixel(
    visualChar: Char = TRANSPARENT_CHAR,
    directionChar: Char = TRANSPARENT_CHAR,
    highlight: Highlight = Highlight.NO
) {
    var visualChar: Char = visualChar
        private set

    var directionChar: Char = directionChar
        private set

    var highlight: Highlight = highlight
        private set

    val isTransparent: Boolean
        get() = visualChar.isTransparent || visualChar.isHalfTransparent

    fun set(visualChar: Char, highlight: Highlight) {
        this.visualChar = visualChar
        // TODO: Correct this with a direction char
        this.directionChar = visualChar
        this.highlight = highlight
    }

    fun reset() {
        visualChar = TRANSPARENT_CHAR
        directionChar = TRANSPARENT_CHAR
        highlight = Highlight.NO
    }

    override fun toString(): String = if (isTransparent) " " else visualChar.toString()

    override fun equals(other: Any?): Boolean {
        return other is Pixel && visualChar == other.visualChar && highlight == other.highlight
    }

    override fun hashCode(): Int {
        var result = visualChar.hashCode()
        result = 31 * result + highlight.hashCode()
        return result
    }

    companion object {
        val TRANSPARENT_PIXEL = Pixel()
    }
}

/**
 * An enum to describe all kinds of highlight
 */
enum class Highlight {
    NO,
    SELECTED,
    TEXT_EDITING
}
