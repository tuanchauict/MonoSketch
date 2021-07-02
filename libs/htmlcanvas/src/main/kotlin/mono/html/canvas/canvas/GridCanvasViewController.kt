package mono.html.canvas.canvas

import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.floor

internal class GridCanvasViewController(
    lifecycleOwner: LifecycleOwner,
    canvas: HTMLCanvasElement,
    drawingInfoLiveData: LiveData<DrawingInfoController.DrawingInfo>
) : BaseCanvasViewController(canvas, redrawWhenDrawingInfoUpdated = true) {

    init {
        drawingInfoLiveData.observe(lifecycleOwner, listener = ::setDrawingInfo)
    }

    override fun drawInternal() {
        context.strokeStyle = "#d9d9d9"
        context.lineWidth = 0.25
        context.stroke(createGridPath())

        if (DEBUG) {
            drawAxis()
        }
    }

    private fun createGridPath(): Path2D = Path2D().apply {
        val zeroX = drawingInfo.toXPx(drawingInfo.boardColumnRange.first.toDouble())
        val maxX = drawingInfo.toXPx(drawingInfo.boardColumnRange.last.toDouble())
        val zeroY = drawingInfo.toYPx(drawingInfo.boardRowRange.first.toDouble())
        val maxY = drawingInfo.toYPx(drawingInfo.boardRowRange.last.toDouble())

        for (row in drawingInfo.boardRowRange) {
            val y = drawingInfo.toYPx(row.toDouble())
            moveTo(zeroX, y)
            lineTo(maxX, y)
        }

        for (col in drawingInfo.boardColumnRange) {
            val x = drawingInfo.toXPx(col.toDouble())
            moveTo(x, zeroY)
            lineTo(x, maxY)
        }
    }

    private fun drawAxis() {
        for (row in drawingInfo.boardRowRange) {
            context.fillStyle = getAxisFillStyle(row)
            drawText("${abs(row % 10)}", row, drawingInfo.boardBound.position.column)
        }
        for (col in drawingInfo.boardColumnRange) {
            context.fillStyle = getAxisFillStyle(col)
            drawText("${abs(col % 10)}", drawingInfo.boardBound.position.row, col)
        }

        context.fillStyle = "#ff0000"
        drawText("█", 0, 0)
        context.fillStyle = "#ffffff"
        drawText("+", 0, 0)
    }

    private fun getAxisFillStyle(index: Int): String {
        val styleIndex = floor(index / 10F) % AXIS_STYLES.size
        return AXIS_STYLES[styleIndex.toInt().absoluteValue]
    }

    companion object {
        private val AXIS_STYLES = listOf("#86b3fc", "#ff878d")
        private const val DEBUG = true
    }
}
