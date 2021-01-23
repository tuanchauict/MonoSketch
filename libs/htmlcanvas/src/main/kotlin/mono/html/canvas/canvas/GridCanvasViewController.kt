package mono.html.canvas.canvas

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D

internal class GridCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {
    override fun drawInternal() {
        context.strokeStyle = "#ff0000"
        context.lineWidth = 0.1
        context.stroke(createGridPath())
    }

    private fun createGridPath(): Path2D = Path2D().apply {
        val widthPx = canvasSizePx.width.toDouble()
        val heightPx = canvasSizePx.height.toDouble()
        val zeroX = offset.left.toDouble()
        val zeroY = offset.top.toDouble()

        for (row in rowRange) {
            val y = toYPx(row)
            moveTo(zeroX, y)
            lineTo(widthPx, y)
        }

        for (col in columnRange) {
            val x = toXPx(col)
            moveTo(x, zeroY)
            lineTo(x, heightPx)
        }
    }
}
