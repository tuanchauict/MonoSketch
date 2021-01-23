package mono.html.canvas.canvas

import mono.graphics.geo.Point
import mono.graphics.geo.Size
import mono.graphics.geo.SizeF
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.CanvasTextBaseline
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.LEFT
import org.w3c.dom.TOP
import kotlin.math.ceil

internal abstract class BaseCanvasViewController(private val canvas: HTMLCanvasElement) {
    protected val context: CanvasRenderingContext2D =
        canvas.getContext("2d") as CanvasRenderingContext2D

    private var font: String = ""
    private var fontSize: Int = 0

    protected var cellSizePx: SizeF = SizeF(1.0, 1.0)
    protected var canvasSizePx: Size = Size(canvas.width, canvas.height)
        set(value) {
            field = value
            canvas.width = value.width
            canvas.height = value.height
        }
    protected var offset: Point = Point(0, 0)
        private set

    private val rowCount: Int get() = ceil(canvasSizePx.height / cellSizePx.height).toInt()
    private val columnCount: Int get() = ceil(canvasSizePx.width / cellSizePx.width).toInt()
    protected val rowRange: IntRange get() = 0..rowCount
    protected val columnRange: IntRange get() = 0..columnCount

    init {
        setFont(15)
        context.imageSmoothingEnabled = true
    }

    private fun setFont(fontSize: Int) {
        this.fontSize = fontSize
        this.font = "normal normal ${fontSize}px monospace"

        cellSizePx = context.getCellSizePx()
    }

    fun setSizeAndRedraw(widthPx: Int, heightPx: Int) {
        canvasSizePx = Size(widthPx, heightPx)
        draw()
    }

    fun toXPx(column: Int): Double = offset.left + cellSizePx.width * column
    fun toYPx(row: Int): Double = offset.top + cellSizePx.height * row

    private fun CanvasRenderingContext2D.getCellSizePx(): SizeF {
        context.font = font
        context.textAlign = CanvasTextAlign.LEFT
        context.textBaseline = CanvasTextBaseline.TOP
        val metrics = measureText("█")
        val cWidth = metrics.width
        val cHeight = fontSize.toDouble()
        return SizeF(cWidth, cHeight)
    }

    fun draw() {
        context.font = font
        context.textAlign = CanvasTextAlign.LEFT
        context.textBaseline = CanvasTextBaseline.TOP
        drawInternal()
    }

    protected abstract fun drawInternal()
}
