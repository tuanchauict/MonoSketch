package mono.graphics.bitmap.drawable

import mono.graphics.bitmap.MonoBitmap
import mono.shape.shape.Rectangle

object RectangleDrawable {
    private val REPEATABLE_RANGE = NinePatchDrawable.RepeatableRange.Repeat(1, 1)
    private val NINE_PATCH_0 = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText("+-+\n| |\n+-+"),
        REPEATABLE_RANGE,
        REPEATABLE_RANGE
    )

    // TODO: Add more kinds of rectangle
    fun toBitmap(rectangle: Rectangle): MonoBitmap =
        NINE_PATCH_0.toBitmap(rectangle.bound.width, rectangle.bound.height)
}
