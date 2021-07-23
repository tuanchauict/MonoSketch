package mono.bitmap.manager.factory

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Size
import mono.shape.extra.manager.model.TextAlign
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

        val maxTextWidth = boundSize.width - colOffset * 2
        val maxTextHeight = boundSize.height - rowOffset * 2

        val row0 = when (extra.textAlign.verticalAlign) {
            TextAlign.VerticalAlign.TOP -> rowOffset
            TextAlign.VerticalAlign.MIDDLE ->
                if (maxTextHeight < renderableText.size) {
                    rowOffset
                } else {
                    (maxTextHeight - renderableText.size) / 2 + rowOffset
                }
            TextAlign.VerticalAlign.BOTTOM ->
                if (maxTextHeight < renderableText.size) {
                    rowOffset
                } else {
                    maxTextHeight - renderableText.size + rowOffset
                }
        }

        val horizontalAlign = extra.textAlign.horizontalAlign
        for (rowIndex in renderableText.indices) {
            val row = renderableText[rowIndex]
            val col0 = when (horizontalAlign) {
                TextAlign.HorizontalAlign.LEFT -> colOffset
                TextAlign.HorizontalAlign.MIDDLE -> (maxTextWidth - row.length) / 2 + colOffset
                TextAlign.HorizontalAlign.RIGHT -> maxTextWidth - row.length + colOffset
            }
            for (colIndex in row.indices) {
                val char = row[colIndex]
                if (char != ' ') {
                    bitmapBuilder.put(row0 + rowIndex, col0 + colIndex, char)
                }
            }
        }
        return bitmapBuilder.toBitmap()
    }
}
