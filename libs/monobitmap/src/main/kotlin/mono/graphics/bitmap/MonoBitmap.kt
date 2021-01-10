package mono.graphics.bitmap

import mono.common.SpecialCharacters.EMPTY_CHAR

/**
 * A model class for drawing characters.
 */
class MonoBitmap(private val width: Int, private val height: Int) {
    private val matrixInternal: List<MutableList<Char>> = List(height) {
        MutableList(width) { EMPTY_CHAR }
    }

    val matrix: List<List<Char>> = matrixInternal

    fun put(row: Int, column: Int, char: Char) {
        if (row in 0 until height && column in 0 until width) {
            matrixInternal[row][column] = char
        }
    }

    override fun toString(): String =
        matrix.joinToString("\n", transform = ::toRowString)

    private fun toRowString(chars: List<Char>): String =
        chars.joinToString("") { if (it == EMPTY_CHAR) " " else it.toString() }
}
