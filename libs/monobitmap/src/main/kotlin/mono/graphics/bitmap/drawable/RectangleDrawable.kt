package mono.graphics.bitmap.drawable

import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Size
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

    fun toBitmap(size: Size, extra: Rectangle.Extra): MonoBitmap {
        val ninepatch = when {
            size.width == 1 && size.height == 1 -> NINE_PATCH_DOT
            size.width == 1 -> NINE_PATCH_VERTICAL_LINE
            size.height == 1 -> NINE_PATCH_HORIZONTAL_LINE
            else -> extra.fillStyle.toNinePatch()
        }
        return ninepatch.toBitmap(size.width, size.height)
    }

    private fun Rectangle.FillStyle.toNinePatch(): NinePatchDrawable = when (this) {
        Rectangle.FillStyle.STYLE_0_BORDER -> NINE_PATCH_0_BLANK
        Rectangle.FillStyle.STYLE_0_FILL -> NINE_PATCH_0_FILL
    }
}
