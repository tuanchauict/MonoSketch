package mono.html.canvas.canvas

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D
import kotlin.math.abs

internal class GridCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {
    override fun drawInternal() {
        context.strokeStyle = "#d9d9d9"
        context.lineWidth = 0.25
        context.stroke(createGridPath())

        if (DEBUG) {
            drawAxis()
        }
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
        val zeroX = drawingInfo.toXPx(drawingInfo.boardColumnRange.first)
        val zeroY = drawingInfo.toYPx(drawingInfo.boardRowRange.first)
        for (row in drawingInfo.boardRowRange) {
            context.fillStyle = getAxisFillStyle(row)
            context.fillText("${abs(row % 10)}", zeroX, drawingInfo.toYPx(row))
        }
        for (col in drawingInfo.boardColumnRange) {
            context.fillStyle = getAxisFillStyle(col)
            context.fillText("${abs(col % 10)}", drawingInfo.toXPx(col), zeroY)
        }

        context.fillStyle = "#ff0000"
        context.fillText("█", drawingInfo.toXPx(0), drawingInfo.toYPx(0))
        context.fillStyle = "#ffffff"
        context.fillText("+", drawingInfo.toXPx(0), drawingInfo.toYPx(0))
    }

    private fun getAxisFillStyle(index: Int): String {
        val styleIndex = (index / 10) % AXIS_STYLES.size
        return AXIS_STYLES[styleIndex]
    }

    companion object {
        private val AXIS_STYLES =
            listOf("#86b3fc", "#ff878d")
        private const val DEBUG = true
    }
}
