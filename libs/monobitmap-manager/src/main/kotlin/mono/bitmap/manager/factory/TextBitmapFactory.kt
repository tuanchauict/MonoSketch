package mono.bitmap.manager.factory

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Size
import mono.shape.shape.extra.TextExtra

object TextBitmapFactory {
    fun toBitmap(boundSize: Size, renderableText: List<String>, extra: TextExtra): MonoBitmap {
        val bgBitmap =
            RectangleBitmapFactory.toBitmap(boundSize, extra.boundExtra)
        val bitmapBuilder = MonoBitmap.Builder(boundSize.width, boundSize.height)
        bitmapBuilder.fill(0, 0, bgBitmap)

        val hasBorder = extra.hasBorder()
        val rowOffset = if (hasBorder) 1 else 0
        val colOffset = if (hasBorder) 1 else 0
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
