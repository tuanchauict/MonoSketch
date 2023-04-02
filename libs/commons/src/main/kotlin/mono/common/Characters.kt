/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.common

object Characters {
    // Transparent in both rendering and selection
    const val TRANSPARENT_CHAR: Char = 0.toChar()

    // Transparent in rendering but visible for selection
    const val HALF_TRANSPARENT_CHAR: Char = 1.toChar()

    const val NBSP: Char = 0x00A0.toChar()

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

    val Char.isTransparent: Boolean
        get() = this == TRANSPARENT_CHAR

    val Char.isHalfTransparent: Boolean
        get() = this == HALF_TRANSPARENT_CHAR
}
