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

    fun set(char: Char, highlight: Highlight) {
        this.char = char
        this.highlight = highlight
    }
}

/**
 * An enum to describe all kinds of highlight
 */
enum class Highlight(val paintColor: String) {
    NO("#000000"),
    SELECTED("#0000FF")
}
