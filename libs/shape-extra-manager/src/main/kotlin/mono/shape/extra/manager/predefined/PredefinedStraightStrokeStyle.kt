package mono.shape.extra.manager.predefined

import mono.common.Characters
import mono.shape.extra.manager.model.StraightStrokeStyle

/**
 * An object for listing all predefined [mono.shape.extra.manager.model.StraightStrokeStyle]
 */
object PredefinedStraightStrokeStyle {
    val NO_STROKE = StraightStrokeStyle(
        id = "S0",
        displayName = "No Stroke",
        horizontalCharacter = Characters.HALF_TRANSPARENT_CHAR,
        verticalCharacter = Characters.HALF_TRANSPARENT_CHAR,
        upLeft = Characters.HALF_TRANSPARENT_CHAR,
        upRight = Characters.HALF_TRANSPARENT_CHAR,
        downLeft = Characters.HALF_TRANSPARENT_CHAR,
        downRight = Characters.HALF_TRANSPARENT_CHAR
    )

    val PREDEFINED_STYLES = listOf(
        StraightStrokeStyle(
            id = "S1",
            displayName = "─",
            horizontalCharacter = '─',
            verticalCharacter = '│',
            upLeft = '┐',
            upRight = '┌',
            downLeft = '┘',
            downRight = '└'
        ),
        StraightStrokeStyle(
            id = "S2",
            displayName = "━",
            horizontalCharacter = '━',
            verticalCharacter = '┃',
            upLeft = '┓',
            upRight = '┏',
            downLeft = '┛',
            downRight = '┗'
        ),
        StraightStrokeStyle(
            id = "S3",
            displayName = "═",
            horizontalCharacter = '═',
            verticalCharacter = '║',
            upLeft = '╗',
            upRight = '╔',
            downLeft = '╝',
            downRight = '╚'
        )
    )
}
