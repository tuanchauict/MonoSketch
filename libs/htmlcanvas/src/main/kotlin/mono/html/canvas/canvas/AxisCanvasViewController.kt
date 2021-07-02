package mono.html.canvas.canvas

import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.LEFT
import org.w3c.dom.Path2D
import org.w3c.dom.RIGHT

internal class AxisCanvasViewController(
    lifecycleOwner: LifecycleOwner,
    canvas: HTMLCanvasElement,
    drawingInfoLiveData: LiveData<DrawingInfoController.DrawingInfo>
) : BaseCanvasViewController(canvas) {
    init {
        drawingInfoLiveData.observe(lifecycleOwner, listener = ::setDrawingInfo)
        drawingInfoLiveData.observe(lifecycleOwner, 0) { draw() }
    }

    override fun drawInternal() {
        drawAxis()
    }

    private fun drawAxis() {
        context.font = "normal normal normal ${11}px 'JetBrains Mono'"
        val cellSizePx = drawingInfo.cellSizePx
        val canvasSizePx = drawingInfo.canvasSizePx

        val xAxisHeight = cellSizePx.height * AXIS_X_HEIGHT
        val yAxisWidth = cellSizePx.width * AXIS_Y_WIDTH

        val path = Path2D()

        context.fillStyle = AXIS_BG_COLOR
        context.fillRect(
            x = 0.0,
            y = 0.0,
            w = yAxisWidth,
            h = canvasSizePx.height.toDouble()
        )
        context.fillStyle = AXIS_TEXT_COLOR
        context.textAlign = CanvasTextAlign.RIGHT

        path.addHLine(0.0, xAxisHeight, canvasSizePx.width.toDouble())

        for (row in drawingInfo.boardRowRange.filter { it % 10 == 0 }) {
            val text = row.toString()
            val yPx = xAxisHeight + drawingInfo.toYPx(row.toDouble())
            val xPx = yAxisWidth - 0.5 * cellSizePx.width
            context.fillText(text, xPx, yPx + 5)

            if (yPx > xAxisHeight) {
                path.addHLine(yAxisWidth - AXIS_RULER_SIZE, yPx, AXIS_RULER_SIZE)
            }
        }

        context.fillStyle = AXIS_BG_COLOR
        context.fillRect(
            x = 0.0,
            y = 0.0,
            w = canvasSizePx.width.toDouble(),
            h = xAxisHeight
        )
        context.fillStyle = AXIS_TEXT_COLOR
        context.textAlign = CanvasTextAlign.LEFT

        path.addVLine(yAxisWidth, xAxisHeight, canvasSizePx.height.toDouble())

        for (col in drawingInfo.boardColumnRange.filter { it % 15 == 0 }) {
            val xPx = drawingInfo.toXPx(col.toDouble() + AXIS_Y_WIDTH)
            context.fillText(col.toString(), xPx + 3, 1 * cellSizePx.height)

            path.addVLine(xPx, xAxisHeight - AXIS_RULER_SIZE, AXIS_RULER_SIZE)
        }

        context.strokeStyle = AXIS_RULER_COLOR
        context.stroke(path)
    }

    companion object {
        private const val AXIS_BG_COLOR = "#EEEEEE"
        private const val AXIS_TEXT_COLOR = "#000000"
        private const val AXIS_RULER_COLOR = "#444444"
        private const val AXIS_RULER_SIZE = 15.0

        const val AXIS_Y_WIDTH = 4.0
        const val AXIS_X_HEIGHT = 2.0
    }
}
