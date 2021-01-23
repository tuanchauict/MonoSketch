package mono.html.canvas.canvas

import mono.graphics.geo.Point
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.CanvasTextBaseline
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.LEFT
import org.w3c.dom.TOP

internal abstract class BaseCanvasViewController(private val canvas: HTMLCanvasElement) {
    protected val context: CanvasRenderingContext2D =
        canvas.getContext("2d") as CanvasRenderingContext2D

    private var font: String = ""
    private var fontSize: Int = 0
    private var cellWidthPx: Double = 0.0
    private var cellHeightPx: Double = 0.0

    protected val widthPx: Int
        get() = canvas.width
    protected val heightPx: Int
        get() = canvas.height
    protected var offset: Point = Point(0, 0)
        private set

    private val rowCount: Int get() = (heightPx / cellHeightPx).toInt()
    private val columnCount: Int get() = (widthPx / cellWidthPx).toInt()
    protected val rowRange: IntRange get() = 0..rowCount
    protected val columnRange: IntRange get() = 0..columnCount

    init {
        setFont(15)
        context.imageSmoothingEnabled = true
    }

    private fun setFont(fontSize: Int) {
        this.fontSize = fontSize
        this.font = "normal normal ${fontSize}px monospace"

        val (cellWidthPx, cellHeightPx) = context.getCellSizePx()
        this.cellWidthPx = cellWidthPx
        this.cellHeightPx = cellHeightPx
    }

    fun setSize(widthPx: Int, heightPx: Int) {
        canvas.width = widthPx
        canvas.height = heightPx
    }

    fun toXPx(column: Int): Double = offset.left + cellWidthPx * column
    fun toYPx(row: Int): Double = offset.top + cellHeightPx * row

    private fun CanvasRenderingContext2D.getCellSizePx(): Pair<Double, Double> {
        context.font = font
        context.textAlign = CanvasTextAlign.LEFT
        context.textBaseline = CanvasTextBaseline.TOP
        val metrics = measureText("█")
        val cWidth = metrics.width
        val cHeight = fontSize.toDouble()
        return cWidth to cHeight
    }

    fun draw() {
        context.font = font
        context.textAlign = CanvasTextAlign.LEFT
        context.textBaseline = CanvasTextBaseline.TOP
        drawInternal()
    }

    protected abstract fun drawInternal()
}
