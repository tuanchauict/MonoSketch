package mono.graphics.bitmap.drawable

import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.MonoBitmap
import mono.shape.shape.Rectangle

object RectangleDrawable {
    private val REPEATABLE_RANGE = NinePatchDrawable.RepeatableRange.Repeat(1, 1)
    private val PATTERN_TEXT_0 = """
        ┌─┐
        | |
        └─┘
    """.trimIndent()

    private val NINE_PATCH_0_BLANK = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText(PATTERN_TEXT_0),
        REPEATABLE_RANGE,
        REPEATABLE_RANGE
    )
    private val NINE_PATCH_0_FILL = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText(PATTERN_TEXT_0, transparentChar = TRANSPARENT_CHAR),
        REPEATABLE_RANGE,
        REPEATABLE_RANGE
    )

    private val NINE_PATCH_DOT = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText("•")
    )

    private val NINE_PATCH_HORIZONTAL_LINE = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText("─"),
        horizontalRepeatableRange = NinePatchDrawable.RepeatableRange.Repeat(0, 0),
    )

    private val NINE_PATCH_VERTICAL_LINE = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText("|"),
        verticalRepeatableRange = NinePatchDrawable.RepeatableRange.Repeat(0, 0)
    )

    // TODO: Add more kinds of rectangle
    fun toBitmap(rectangle: Rectangle): MonoBitmap {
        val size = rectangle.bound.size
        val ninepatch = when {
            size.width == 1 && size.height == 1 -> NINE_PATCH_DOT
            size.width == 1 -> NINE_PATCH_VERTICAL_LINE
            size.height == 1 -> NINE_PATCH_HORIZONTAL_LINE
            else -> NINE_PATCH_0_BLANK
        }
        return ninepatch.toBitmap(rectangle.bound.width, rectangle.bound.height)
    }
}
