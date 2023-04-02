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
    char: Char = TRANSPARENT_CHAR,
    highlight: Highlight = Highlight.NO
) {
    var char: Char = char
        private set
    var highlight: Highlight = highlight
        private set

    val isTransparent: Boolean
        get() = char.isTransparent || char.isHalfTransparent

    fun set(char: Char, highlight: Highlight) {
        this.char = char
        this.highlight = highlight
    }

    fun reset() {
        char = TRANSPARENT_CHAR
        highlight = Highlight.NO
    }

    override fun toString(): String = if (isTransparent) " " else char.toString()

    override fun equals(other: Any?): Boolean {
        return other is Pixel && char == other.char && highlight == other.highlight
    }

    override fun hashCode(): Int {
        var result = char.hashCode()
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
