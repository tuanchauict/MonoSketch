/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.extra.style

/**
 * A class for defining a stroke style of straight line.
 */
data class StraightStrokeStyle(
    val id: String,
    val displayName: String,
    val horizontal: Char,
    val vertical: Char,
    val downLeft: Char,
    val upRight: Char,
    val upLeft: Char,
    val downRight: Char
)
