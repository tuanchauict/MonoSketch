package mono.bitmap.manager.factory

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Size
import mono.shape.extra.manager.model.StraightStrokeDashPattern
import mono.shape.extra.manager.model.StraightStrokeStyle
import mono.shape.shape.extra.RectangleExtra

object RectangleBitmapFactory {

    fun toBitmap(size: Size, extra: RectangleExtra): MonoBitmap {
        val fillDrawable = extra.fillStyle.drawable

        val bitmapBuilder = MonoBitmap.Builder(size.width, size.height)
        bitmapBuilder.fill(0, 0, fillDrawable.toBitmap(size.width, size.height))
        bitmapBuilder.drawBorder(size, extra.strokeStyle, extra.dashPattern)

        return bitmapBuilder.toBitmap()
    }

    private fun MonoBitmap.Builder.drawBorder(
        size: Size,
        strokeStyle: StraightStrokeStyle,
        dashPattern: StraightStrokeDashPattern
    ) {
        if (size.width == 1 && size.height == 1) {
            put(0, 0, '▫')
            return
        }
        if (size.width == 1 || size.height == 1) {
            return
        }

        val left = 0
        val top = 0
        val right = size.width - 1
        val bottom = size.height - 1

        sequenceOf(
            PointChar.point(left, top, strokeStyle.upRight),
            PointChar.horizontalLine(left + 1, right - 1, top, strokeStyle.horizontal),
            PointChar.point(right, top, strokeStyle.leftDown),
            PointChar.verticalLine(right, top + 1, bottom - 1, strokeStyle.vertical),
            PointChar.point(right, bottom, strokeStyle.leftUp),
            PointChar.horizontalLine(right - 1, left + 1, bottom, strokeStyle.horizontal),
            PointChar.point(left, bottom, strokeStyle.downRight),
            PointChar.verticalLine(left, bottom - 1, top + 1, strokeStyle.vertical)
        )
            .flatMap { it }
            .forEachIndexed { index, pointChar ->
                val char = if (dashPattern.isGap(index)) ' ' else pointChar.char
                put(pointChar.top, pointChar.left, char)
            }
    }
}
