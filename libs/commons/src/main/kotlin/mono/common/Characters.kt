package mono.common

object Characters {
    const val TRANSPARENT_CHAR: Char = 0.toChar()

    /**
     * Copies [length] characters from [src] from [srcOffset] into [dest] from [destOffset].
     * If the character is [TRANSPARENT_CHAR], ignore.
     */
    fun copyChars(
        src: List<Char>,
        srcOffset: Int,
        dest: MutableList<Char>,
        destOffset: Int,
        length: Int
    ) {
        src.subList(srcOffset, srcOffset + length).forEachIndexed { index, char ->
            if (char != TRANSPARENT_CHAR) {
                dest[destOffset + index] = char
            }
        }
    }
}
