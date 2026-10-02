/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.extra.style

/**
 * A class for defining a shadow style for rectangle.
 *
 * @param id is the key for retrieving predefined [ShadowStyle] when serialization.
 * @param displayName is the text visible on the UI tool for selection.
 * @param char is the character used to render the shadow.
 */
class ShadowStyle(
    val id: String,
    val displayName: String,
    val char: Char
)
