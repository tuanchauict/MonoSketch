package mono.graphics.bitmap.drawable

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Size
import mono.shape.shape.Text

object TextDrawable {
    fun toBitmap(boundSize: Size, extra: Text.Extra): MonoBitmap {
        val boundExtra = extra.boundExtra
        val bgBitmap = if (boundExtra != null) {
            RectangleDrawable.toBitmap(boundSize, boundExtra)
        } else {
            null
        }
        val bitmapBuilder = MonoBitmap.Builder(boundSize.width, boundSize.height)
        if (bgBitmap != null) {
            bitmapBuilder.fill(0, 0, bgBitmap)
        }
        // TODO: Fill text
        return bitmapBuilder.toBitmap()
    }
}
