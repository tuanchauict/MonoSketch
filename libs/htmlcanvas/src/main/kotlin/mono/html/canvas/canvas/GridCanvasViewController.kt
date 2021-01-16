package mono.html.canvas.canvas

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D

internal class GridCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {
    fun draw() {
        context.strokeStyle = "#444444"
        context.lineWidth = 0.1
        context.stroke(createGridPath())
    }

    private fun createGridPath(): Path2D = Path2D().apply {
        val widthPx = widthPx.toDouble()
        val heightPx = heightPx.toDouble()
        val zeroX = offset.left.toDouble()
        val zeroY = offset.top.toDouble()

        val rowCount = (heightPx / cellHeightPx).toInt()
        for (row in 0..rowCount) {
            val y = zeroY + cellHeightPx * row
            moveTo(zeroX, y)
            lineTo(widthPx, y)
        }

        val colCount = (widthPx / cellWidthPx).toInt()
        for (col in 0..colCount) {
            val x = zeroX + cellWidthPx * col
            moveTo(x, zeroY)
            lineTo(x, heightPx)
        }
    }
}
