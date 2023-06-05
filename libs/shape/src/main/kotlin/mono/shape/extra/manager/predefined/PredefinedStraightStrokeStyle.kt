/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.extra.manager.predefined

import mono.common.Characters
import mono.shape.extra.style.StraightStrokeStyle

/**
 * An object for listing all predefined [mono.shape.extra.style.StraightStrokeStyle]
 */
object PredefinedStraightStrokeStyle {
    val NO_STROKE = StraightStrokeStyle(
        id = "S0",
        displayName = "No Stroke",
        horizontal = Characters.HALF_TRANSPARENT_CHAR,
        vertical = Characters.HALF_TRANSPARENT_CHAR,
        downLeft = Characters.HALF_TRANSPARENT_CHAR,
        upRight = Characters.HALF_TRANSPARENT_CHAR,
        upLeft = Characters.HALF_TRANSPARENT_CHAR,
        downRight = Characters.HALF_TRANSPARENT_CHAR
    )

    private val ALL_STYLES = listOf(
        NO_STROKE,
        StraightStrokeStyle(
            id = "S1",
            displayName = "─",
            horizontal = '─',
            vertical = '│',
            downLeft = '┐',
            upRight = '┌',
            upLeft = '┘',
            downRight = '└'
        ),
        StraightStrokeStyle(
            id = "S2",
            displayName = "━",
            horizontal = '━',
            vertical = '┃',
            downLeft = '┓',
            upRight = '┏',
            upLeft = '┛',
            downRight = '┗'
        ),
        StraightStrokeStyle(
            id = "S3",
            displayName = "═",
            horizontal = '═',
            vertical = '║',
            downLeft = '╗',
            upRight = '╔',
            upLeft = '╝',
            downRight = '╚'
        ),
        StraightStrokeStyle(
            id = "S4",
            displayName = "▢",
            horizontal = '─',
            vertical = '│',
            downLeft = '╮',
            upRight = '╭',
            upLeft = '╯',
            downRight = '╰'
        )
    )

    private val ID_TO_STYLE_MAP: Map<String, StraightStrokeStyle> = ALL_STYLES.associateBy { it.id }

    private val STYLE_TO_ROUNDED_CORNER_STYLE_MAP = mapOf("S1" to "S4")

    val PREDEFINED_STYLES = listOf("S1", "S2", "S3").map { ID_TO_STYLE_MAP[it]!! }

    fun getStyle(id: String?, isRounded: Boolean = false): StraightStrokeStyle? {
        val adjustedId =
            if (isRounded) {
                STYLE_TO_ROUNDED_CORNER_STYLE_MAP[id] ?: id
            } else {
                id
            }
        return ID_TO_STYLE_MAP[adjustedId]
    }

    fun isCornerRoundable(id: String?): Boolean = id in STYLE_TO_ROUNDED_CORNER_STYLE_MAP
}
