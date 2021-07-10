package mono.bitmap.manager.factory

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.bitmap.drawable.NinePatchDrawable
import mono.graphics.geo.Size
import mono.shape.shape.extra.RectangleExtra

object RectangleBitmapFactory {

    private val NINE_PATCH_DOT = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText("•")
    )

    private val NINE_PATCH_HORIZONTAL_LINE = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText("─"),
        horizontalRepeatableRange = NinePatchDrawable.RepeatableRange.Repeat(0, 0),
    )

    private val NINE_PATCH_VERTICAL_LINE = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText("│"),
        verticalRepeatableRange = NinePatchDrawable.RepeatableRange.Repeat(0, 0)
    )

    fun toBitmap(size: Size, extra: RectangleExtra): MonoBitmap {
        val fillDrawable = when {
            size.width == 1 && size.height == 1 -> null
            size.width == 1 -> null
            size.height == 1 -> null
            else -> extra.fillStyle.drawable
        }
        val borderDrawable = when {
            size.width == 1 && size.height == 1 -> NINE_PATCH_DOT
            size.width == 1 -> NINE_PATCH_VERTICAL_LINE
            size.height == 1 -> NINE_PATCH_HORIZONTAL_LINE
            else -> extra.borderStyle.drawable
        }

        val bitmapBuilder = MonoBitmap.Builder(size.width, size.height)
        if (fillDrawable != null) {
            bitmapBuilder.fill(0, 0, fillDrawable.toBitmap(size.width, size.height))
        }
        bitmapBuilder.fill(0, 0, borderDrawable.toBitmap(size.width, size.height))

        return bitmapBuilder.toBitmap()
    }
}
