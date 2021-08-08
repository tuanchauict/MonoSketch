package mono.bitmap.manager.factory

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.bitmap.drawable.NinePatchDrawable
import mono.graphics.geo.Size
import mono.shape.shape.extra.RectangleExtra

object RectangleBitmapFactory {

    private val NINE_PATCH_DOT = NinePatchDrawable(
        NinePatchDrawable.Pattern.fromText("▫")
    )

    fun toBitmap(size: Size, extra: RectangleExtra): MonoBitmap {
        val fillDrawable = extra.fillStyle.drawable
        val borderDrawable = when {
            size.width > 1 && size.height > 1 -> extra.borderStyle.drawable
            size.width == 1 && size.height == 1 -> NINE_PATCH_DOT
            else -> null
        }

        val bitmapBuilder = MonoBitmap.Builder(size.width, size.height)
        bitmapBuilder.fill(0, 0, fillDrawable.toBitmap(size.width, size.height))
        if (borderDrawable != null) {
            bitmapBuilder.fill(0, 0, borderDrawable.toBitmap(size.width, size.height))
        }

        return bitmapBuilder.toBitmap()
    }
}
