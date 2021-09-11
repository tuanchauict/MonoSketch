package mono.html.canvas.canvas

import kotlinx.browser.window
import mono.graphics.geo.Size
import mono.html.canvas.canvas.DrawingInfoController.Companion.DEFAULT_FONT
import mono.html.ext.px
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.LEFT
import org.w3c.dom.Path2D
import org.w3c.dom.RIGHT
import kotlin.math.max

internal class AxisCanvasViewController(
    lifecycleOwner: LifecycleOwner,
    private val canvas: HTMLCanvasElement,
    drawingInfoLiveData: LiveData<DrawingInfoController.DrawingInfo>
) : BaseCanvasViewController(canvas) {
    init {
        drawingInfoLiveData.observe(lifecycleOwner, listener = ::updateCanvasSize)
        drawingInfoLiveData.observe(lifecycleOwner, 0) { draw() }
    }

    private fun updateCanvasSize(drawingInfo: DrawingInfoController.DrawingInfo) {
        val canvasSizePx = Size(
            drawingInfo.canvasSizePx.width + AXIS_Y_WIDTH.toInt(),
            drawingInfo.canvasSizePx.height + AXIS_X_HEIGHT.toInt()
        )

        // Update canvas information causes canvas clearRect() which requires redraw
        val isSizeChange = this.drawingInfo.canvasSizePx != canvasSizePx
        if (isSizeChange) {
            val dpr = max(window.devicePixelRatio, 2.0)
            canvas.width = (canvasSizePx.width * dpr).toInt()
            canvas.height = ((canvasSizePx.height) * dpr).toInt()
            canvas.style.width = canvasSizePx.width.px
            canvas.style.height = canvasSizePx.height.px
            context.scale(dpr, dpr)
        }

        this.drawingInfo = drawingInfo.copy(canvasSizePx = canvasSizePx)

        if (isSizeChange) {
            draw()
        }
    }

    override fun drawInternal() {
        context.font = "normal normal normal 10.5px $DEFAULT_FONT"
        drawAxis()
    }

    private fun drawAxis() {
        val cellSizePx = drawingInfo.cellSizePx
        val canvasSizePx = drawingInfo.canvasSizePx

        val xAxisHeight = AXIS_X_HEIGHT
        val yAxisWidth = AXIS_Y_WIDTH

        val path = Path2D()
        context.lineWidth = 1.0
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

        for (row in drawingInfo.boardRowRange) {
            val text = row.toString()
            val yPx = xAxisHeight + drawingInfo.toYPx(row.toDouble())
            val xPx = yAxisWidth - 0.5 * cellSizePx.width
            context.fillText(text, xPx, yPx + 3)
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

        for (col in drawingInfo.boardColumnRange.filter { it % 20 == 0 }) {
            val xPx = drawingInfo.toXPx(col.toDouble()) + AXIS_Y_WIDTH
            context.fillText(col.toString(), xPx + 2, 7.0)

            path.addVLine(xPx, xAxisHeight - AXIS_RULER_SIZE, AXIS_RULER_SIZE)
        }

        context.strokeStyle = AXIS_RULER_COLOR
        context.stroke(path)
    }

    companion object {
        private const val AXIS_BG_COLOR = "#EEEEEE"
        private const val AXIS_TEXT_COLOR = "#666666"
        private const val AXIS_RULER_COLOR = "#444444"
        private const val AXIS_RULER_SIZE = 12.0

        private const val AXIS_Y_WIDTH = 33.0
        private const val AXIS_X_HEIGHT = 18.0
    }
}
