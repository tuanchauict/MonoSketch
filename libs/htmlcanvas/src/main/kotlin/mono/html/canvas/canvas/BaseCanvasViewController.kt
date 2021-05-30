package mono.html.canvas.canvas

import kotlinx.browser.window
import mono.graphics.geo.Size
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.CanvasTextBaseline
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.LEFT
import org.w3c.dom.TOP
import kotlin.math.max

// TODO: Pass drawing info livedata into this class and let it observes the change.
internal abstract class BaseCanvasViewController(private val canvas: HTMLCanvasElement) {
    protected val context: CanvasRenderingContext2D =
        canvas.getContext("2d") as CanvasRenderingContext2D

    internal var drawingInfo: DrawingInfoController.DrawingInfo

    init {
        drawingInfo =
            DrawingInfoController.DrawingInfo(canvasSizePx = Size(canvas.width, canvas.height))
    }

    protected fun setDrawingInfo(drawingInfo: DrawingInfoController.DrawingInfo) {
        this.drawingInfo = drawingInfo
        val canvasSizePx = drawingInfo.canvasSizePx

        val dpr = max(window.devicePixelRatio, 2.0)

        canvas.width = (canvasSizePx.width * dpr).toInt()
        canvas.height = (canvasSizePx.height * dpr).toInt()
        canvas.style.width = "${canvasSizePx.width}px"
        canvas.style.height = "${canvasSizePx.height}px"
        context.scale(dpr, dpr)

        draw()
    }

    internal fun draw() {
        context.clearRect(
            x = 0.0,
            y = 0.0,
            w = drawingInfo.canvasSizePx.width.toDouble(),
            h = drawingInfo.canvasSizePx.height.toDouble()
        )

        context.font = drawingInfo.font
        context.textAlign = CanvasTextAlign.LEFT
        context.textBaseline = CanvasTextBaseline.TOP
        context.imageSmoothingEnabled = true
        drawInternal()
    }

    protected abstract fun drawInternal()

    protected fun drawText(text: String, row: Int, column: Int) {
        val rowYPx = drawingInfo.toYPx(row.toDouble())
        val xPx = drawingInfo.toXPx(column + 0.05)
        context.fillText(text, xPx, rowYPx)
    }
}
