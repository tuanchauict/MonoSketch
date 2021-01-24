package mono.html.canvas.canvas

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D
import kotlin.math.abs

internal class GridCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {
    override fun drawInternal() {
        context.strokeStyle = "#ff0000"
        context.lineWidth = 0.1
        context.stroke(createGridPath())
        // TODO: This is for testing
        drawAxis()
    }

    private fun createGridPath(): Path2D = Path2D().apply {
        val zeroX = drawingInfo.toXPx(drawingInfo.boardColumnRange.first)
        val maxX = drawingInfo.toXPx(drawingInfo.boardColumnRange.last)
        val zeroY = drawingInfo.toYPx(drawingInfo.boardRowRange.first)
        val maxY = drawingInfo.toYPx(drawingInfo.boardRowRange.last)

        for (row in drawingInfo.boardRowRange) {
            val y = drawingInfo.toYPx(row)
            moveTo(zeroX, y)
            lineTo(maxX, y)
        }

        for (col in drawingInfo.boardColumnRange) {
            val x = drawingInfo.toXPx(col)
            moveTo(x, zeroY)
            lineTo(x, maxY)
        }
    }

    private fun drawAxis() {
        context.fillStyle = "#aaaaaa"
        val zeroX = drawingInfo.toXPx(drawingInfo.boardColumnRange.first)
        val zeroY = drawingInfo.toYPx(drawingInfo.boardRowRange.first)
        for (row in drawingInfo.boardRowRange) {
            context.fillText("${abs(row % 10)}", zeroX, drawingInfo.toYPx(row))
        }
        for (col in drawingInfo.boardColumnRange) {
            context.fillText("${abs(col % 10)}", drawingInfo.toXPx(col), zeroY)
        }

        context.fillStyle = "#ff0000"
        context.fillText("█", drawingInfo.toXPx(0), drawingInfo.toYPx(0))
        context.fillStyle = "#ffffff"
        context.fillText("+", drawingInfo.toXPx(0), drawingInfo.toYPx(0))
    }
}
