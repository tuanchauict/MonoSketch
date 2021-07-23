package mono.shape.extra.manager.predefined

import mono.common.Characters
import mono.graphics.bitmap.drawable.NinePatchDrawable
import mono.shape.extra.manager.model.RectangleBorderStyle

/**
 * An object for listing all predefined rectangle border styles.
 */
internal object PredefinedRectangleBorderStyle {
    private val PATTERN_TEXT_NO_BORDER = """
                +++
                + +
                +++
            """.trimIndent()
        .replace('+', Characters.HALF_TRANSPARENT_CHAR)

    private val PATTERN_TEXT_0 = """
                ┌─┐
                │ │
                └─┘
            """.trimIndent()
    private val PATTERN_TEXT_1 = """
                ┏━┓
                ┃ ┃
                ┗━┛
            """.trimIndent()
    private val PATTERN_TEXT_2 = """
                ╔═╗
                ║ ║
                ╚═╝
            """.trimIndent()

    private val REPEATABLE_RANGE_0 = NinePatchDrawable.RepeatableRange.Repeat(1, 1)

    val NO_BORDER = RectangleBorderStyle(
        id = "B0",
        displayName = "No border",
        NinePatchDrawable(
            NinePatchDrawable.Pattern.fromText(PATTERN_TEXT_NO_BORDER),
            REPEATABLE_RANGE_0,
            REPEATABLE_RANGE_0
        )
    )

    val PREDEFINED_STYLES = listOf(
        RectangleBorderStyle(
            id = "B1",
            displayName = "─",
            NinePatchDrawable(
                NinePatchDrawable.Pattern.fromText(PATTERN_TEXT_0),
                REPEATABLE_RANGE_0,
                REPEATABLE_RANGE_0
            )
        ),
        RectangleBorderStyle(
            id = "B2",
            displayName = "━",
            NinePatchDrawable(
                NinePatchDrawable.Pattern.fromText(PATTERN_TEXT_1),
                REPEATABLE_RANGE_0,
                REPEATABLE_RANGE_0
            )
        ),
        RectangleBorderStyle(
            id = "B3",
            displayName = "═",
            NinePatchDrawable(
                NinePatchDrawable.Pattern.fromText(PATTERN_TEXT_2),
                REPEATABLE_RANGE_0,
                REPEATABLE_RANGE_0
            )
        )
    )

    val PREDEFINED_STYLE_MAP = PREDEFINED_STYLES.associateBy { it.id }
}
