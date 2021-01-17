package mono.graphics.board

import mono.common.Characters.TRANSPARENT_CHAR

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
        get() = char == TRANSPARENT_CHAR

    fun set(char: Char, highlight: Highlight) {
        this.char = char
        this.highlight = highlight
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
enum class Highlight(val paintColor: String) {
    NO("#000000"),
    SELECTED("#0000FF")
}
