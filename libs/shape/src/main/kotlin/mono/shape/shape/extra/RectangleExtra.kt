package mono.shape.shape.extra

import mono.common.Characters.HALF_TRANSPARENT_CHAR
import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.drawable.CharDrawable
import mono.graphics.bitmap.drawable.Drawable
import mono.graphics.bitmap.drawable.NinePatchDrawable
import mono.graphics.bitmap.drawable.NinePatchDrawable.Pattern
import mono.shape.serialization.SerializableRectangle
import mono.shape.shape.extra.RectangleExtra.BorderStyle.Companion.NO_ID
import mono.shape.shape.extra.RectangleExtra.FillStyle.Companion.NO_ID

/**
 * A [ShapeExtra] for [mono.shape.shape.Rectangle]
 */
data class RectangleExtra(
    val isFillEnabled: Boolean = false,
    val userSelectedFillStyle: FillStyle,
    val isBorderEnabled: Boolean,
    val userSelectedBorderStyle: BorderStyle
) : ShapeExtra() {
    val fillStyle: FillStyle
        get() = if (isFillEnabled) userSelectedFillStyle else FillStyle.NOFILLED_STYLE

    val borderStyle: BorderStyle
        get() = if (isBorderEnabled) userSelectedBorderStyle else BorderStyle.NO_BORDER

    constructor(serializableExtra: SerializableRectangle.SerializableExtra) : this(
        serializableExtra.isFillEnabled,
        FillStyle.PREDEFINED_STYLES.first { it.id == serializableExtra.userSelectedFillStyleId },
        serializableExtra.isBorderEnabled,
        BorderStyle.PREDEFINED_STYLES.first { it.id == serializableExtra.userSelectedBorderStyleId }
    )

    fun toSerializableExtra(): SerializableRectangle.SerializableExtra =
        SerializableRectangle.SerializableExtra(
            isFillEnabled = isFillEnabled,
            userSelectedFillStyleId = userSelectedFillStyle.id,
            isBorderEnabled = isBorderEnabled,
            userSelectedBorderStyleId = userSelectedBorderStyle.id
        )

    /**
     * A class for defining a fill style for rectangle.
     *
     * @param id is the key for retrieving predefined [FillStyle] when serialization. If id is not
     * defined ([NO_ID]), serializer will use the other information for marshal and unmarshal. [id]
     * cannot be set from outside.
     *
     * @param displayName is the text visible on the UI tool for selection.
     */
    class FillStyle private constructor(
        val id: String,
        val displayName: String,
        val drawable: Drawable
    ) {

        companion object {
            private const val NO_ID = ""

            internal val NOFILLED_STYLE = FillStyle(
                id = "F0",
                displayName = "No Fill",
                CharDrawable(TRANSPARENT_CHAR)
            )

            val PREDEFINED_STYLES = listOf(
                FillStyle(
                    id = "F1",
                    displayName = " ",
                    CharDrawable(' ')
                ),
                FillStyle(
                    id = "F2",
                    displayName = "█",
                    CharDrawable('█')
                ),
                FillStyle(
                    id = "F3",
                    displayName = "▒",
                    CharDrawable('▒')
                ),
                FillStyle(
                    id = "F4",
                    displayName = "░",
                    CharDrawable('░')
                ),
                FillStyle(
                    id = "F5",
                    displayName = "▚",
                    CharDrawable('▚')
                )
            )
        }
    }

    /**
     * A class for defining a fill style for rectangle.
     *
     * @param id is the key for retrieving predefined [FillStyle] when serialization. If id is not
     * defined ([NO_ID]), serializer will use the other information for marshal and unmarshal. [id]
     * cannot be set from outside.
     *
     * @param displayName is the text visible on the UI tool for selection.
     */
    class BorderStyle private constructor(
        val id: String,
        val displayName: String,
        val drawable: Drawable
    ) {
        companion object {
            private const val NO_ID = ""

            private val PATTERN_TEXT_NO_BORDER = """
                +++
                + +
                +++
            """.trimIndent()
                .replace('+', HALF_TRANSPARENT_CHAR)

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

            internal val NO_BORDER = BorderStyle(
                id = "B0",
                displayName = "No border",
                NinePatchDrawable(
                    Pattern.fromText(PATTERN_TEXT_NO_BORDER),
                    REPEATABLE_RANGE_0,
                    REPEATABLE_RANGE_0
                )
            )

            val PREDEFINED_STYLES = listOf(
                BorderStyle(
                    id = "B1",
                    displayName = "─",
                    NinePatchDrawable(
                        Pattern.fromText(PATTERN_TEXT_0),
                        REPEATABLE_RANGE_0,
                        REPEATABLE_RANGE_0
                    )
                ),
                BorderStyle(
                    id = "B2",
                    displayName = "━",
                    NinePatchDrawable(
                        Pattern.fromText(PATTERN_TEXT_1),
                        REPEATABLE_RANGE_0,
                        REPEATABLE_RANGE_0
                    )
                ),
                BorderStyle(
                    id = "B3",
                    displayName = "═",
                    NinePatchDrawable(
                        Pattern.fromText(PATTERN_TEXT_2),
                        REPEATABLE_RANGE_0,
                        REPEATABLE_RANGE_0
                    )
                )
            )
        }
    }

    companion object {
        val DEFAULT = RectangleExtra(
            isFillEnabled = false,
            FillStyle.PREDEFINED_STYLES[0],
            isBorderEnabled = true,
            BorderStyle.PREDEFINED_STYLES[0]
        )
    }
}
