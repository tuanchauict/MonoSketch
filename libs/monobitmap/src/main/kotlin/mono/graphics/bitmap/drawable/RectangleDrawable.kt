package mono.graphics.bitmap.drawable

import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.MonoBitmap
import mono.shape.shape.Rectangle

object RectangleDrawable {
    private val REPEATABLE_RANGE = NinePatchDrawable.RepeatableRange.Repeat(1, 1)
    private val PATTERN_TEXT_0 = """
        +-+
        | |
        +-+
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

    // TODO: Add more kinds of rectangle
    fun toBitmap(rectangle: Rectangle): MonoBitmap =
        NINE_PATCH_0_BLANK.toBitmap(rectangle.bound.width, rectangle.bound.height)
}
