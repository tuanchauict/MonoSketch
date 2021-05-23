package mono.html.canvas.canvas

import kotlinx.browser.window
import mono.graphics.geo.Size
import mono.graphics.geo.SizeF
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.CanvasTextBaseline
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.LEFT
import org.w3c.dom.MIDDLE
import org.w3c.dom.TOP
import kotlin.math.max

internal abstract class BaseCanvasViewController(private val canvas: HTMLCanvasElement) {
    protected val context: CanvasRenderingContext2D =
        canvas.getContext("2d") as CanvasRenderingContext2D

    protected var font: String = ""
    protected var boldFont: String = ""
    private var fontSize: Int = 0

    internal var drawingInfo: DrawingInfoController.DrawingInfo

    init {
        drawingInfo =
            DrawingInfoController.DrawingInfo(canvasSizePx = Size(canvas.width, canvas.height))
        setFont(15)
    }

    internal fun setFont(fontSize: Int) {
        this.fontSize = fontSize
        this.font = "normal normal normal ${fontSize}px Courier"
        this.boldFont = "normal normal 600 ${fontSize}px Courier"

        drawingInfo = drawingInfo.copy(cellSizePx = context.getCellSizePx())
    }

    internal fun setSizeAndRedraw(widthPx: Int, heightPx: Int) {
        val canvasSizePx = Size(widthPx, heightPx)
        val dpr = max(window.devicePixelRatio, 1.0)

        canvas.width = (canvasSizePx.width * dpr).toInt()
        canvas.height = (canvasSizePx.height * dpr).toInt()
        canvas.style.width = "${canvasSizePx.width}px"
        canvas.style.height = "${canvasSizePx.height}px"
        context.scale(dpr, dpr)
        drawingInfo = drawingInfo.copy(canvasSizePx = Size(widthPx, heightPx))
        draw()
    }

    private fun CanvasRenderingContext2D.getCellSizePx(): SizeF {
        context.font = font
        context.textAlign = CanvasTextAlign.LEFT
        context.textBaseline = CanvasTextBaseline.MIDDLE
        val metrics = measureText("█")
        val cWidth = metrics.width
        val cHeight = fontSize.toDouble()
        return SizeF(cWidth, cHeight)
    }

    internal fun draw() {
        context.clearRect(
            x = 0.0,
            y = 0.0,
            w = drawingInfo.canvasSizePx.width.toDouble(),
            h = drawingInfo.canvasSizePx.height.toDouble()
        )

        context.font = font
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
