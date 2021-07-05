package mono.graphics.bitmap.drawable

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Size
import mono.shape.shape.extra.TextExtra

object TextDrawable {
    fun toBitmap(boundSize: Size, renderableText: List<String>, extra: TextExtra): MonoBitmap {
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
        val rowOffset = if (extra.boundExtra != null) 1 else 0
        val colOffset = if (extra.boundExtra != null) 1 else 0
        for (rowIndex in renderableText.indices) {
            val row = renderableText[rowIndex]
            for (colIndex in row.indices) {
                val char = row[colIndex]
                if (char != ' ') {
                    bitmapBuilder.put(rowOffset + rowIndex, colOffset + colIndex, char)
                }
            }
        }
        return bitmapBuilder.toBitmap()
    }
}
