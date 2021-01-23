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
        val zeroX = toXPx(boardColumnRange.first)
        val maxX = toXPx(boardColumnRange.last)
        val zeroY = toYPx(boardRowRange.first)
        val maxY = toYPx(boardRowRange.last)

        for (row in boardRowRange) {
            val y = toYPx(row)
            moveTo(zeroX, y)
            lineTo(maxX, y)
        }

        for (col in boardColumnRange) {
            val x = toXPx(col)
            moveTo(x, zeroY)
            lineTo(x, maxY)
        }
    }

    private fun drawAxis() {
        context.fillStyle = "#aaaaaa"
        val zeroX = toXPx(boardColumnRange.first)
        val zeroY = toYPx(boardRowRange.first)
        for (row in boardRowRange) {
            context.fillText("${abs(row % 10)}", zeroX, toYPx(row))
        }
        for (col in boardColumnRange) {
            context.fillText("${abs(col % 10)}", toXPx(col), zeroY)
        }

    }
}
