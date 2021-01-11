package mono.common

object SpecialCharacters {
    const val EMPTY_CHAR: Char = 0.toChar()

    /**
     * Copies [length] characters from [src] from [srcOffset] into [dest] from [destOffset].
     * If the character is [EMPTY_CHAR], ignore.
     */
    fun copyChars(
        src: List<Char>,
        srcOffset: Int,
        dest: MutableList<Char>,
        destOffset: Int,
        length: Int
    ) {
        src.subList(srcOffset, srcOffset + length).forEachIndexed { index, char ->
            if (char != EMPTY_CHAR) {
                dest[destOffset + index] = char
            }
        }
    }
}
