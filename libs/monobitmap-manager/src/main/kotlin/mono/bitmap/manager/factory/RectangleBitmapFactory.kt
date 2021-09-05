package mono.bitmap.manager.factory

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Size
import mono.shape.extra.style.StraightStrokeStyle
import mono.shape.extra.RectangleExtra
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle
import mono.shape.extra.style.StraightStrokeDashPattern

object RectangleBitmapFactory {

    fun toBitmap(size: Size, extra: RectangleExtra): MonoBitmap {
        val bitmapBuilder = MonoBitmap.Builder(size.width, size.height)

        val fillDrawable = extra.fillStyle?.drawable
        val strokeStyle = extra.strokeStyle

        if (fillDrawable == null && strokeStyle == null) {
            bitmapBuilder.drawBorder(
                size,
                PredefinedStraightStrokeStyle.NO_STROKE,
                extra.dashPattern
            )
            return bitmapBuilder.toBitmap()
        }

        if (fillDrawable != null) {
            bitmapBuilder.fill(0, 0, fillDrawable.toBitmap(size.width, size.height))
        }
        if (strokeStyle != null) {
            bitmapBuilder.drawBorder(size, strokeStyle, extra.dashPattern)
        }

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
            PointChar.horizontalLine(left, right, top, strokeStyle.horizontal),
            PointChar.point(right, top, strokeStyle.downLeft),
            PointChar.verticalLine(right, top, bottom, strokeStyle.vertical),
            PointChar.point(right, bottom, strokeStyle.upLeft),
            PointChar.horizontalLine(right, left, bottom, strokeStyle.horizontal),
            PointChar.point(left, bottom, strokeStyle.downRight),
            PointChar.verticalLine(left, bottom, top, strokeStyle.vertical)
        )
            .flatMap { it }
            .forEachIndexed { index, pointChar ->
                val char = if (dashPattern.isGap(index)) ' ' else pointChar.char
                put(pointChar.top, pointChar.left, char)
            }
    }
}
