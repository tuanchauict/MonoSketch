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

    private var cellSizePx: SizeF = SizeF(1.0, 1.0)
    private var canvasSizePx: Size = Size(canvas.width, canvas.height)
        set(value) {
            field = value
            canvas.width = value.width
            canvas.height = value.height
        }
    private var offsetPx: Point = Point(0, 0)
    private val boardOffsetRow: Int
        get() = (-offsetPx.top / cellSizePx.height).toInt()
    private val boardOffsetColumn: Int
        get() = (-offsetPx.left / cellSizePx.width).toInt()

    protected val boardRowRange: IntRange
        get() {
            val rowCount = ceil(canvasSizePx.height / cellSizePx.height).toInt()
            return boardOffsetRow..(boardOffsetRow + rowCount)
        }
    protected val boardColumnRange: IntRange
        get() {
            val colCount = ceil(canvasSizePx.width / cellSizePx.width).toInt()
            return boardOffsetColumn..(boardOffsetColumn + colCount)
        }

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

    fun toXPx(column: Int): Double = offsetPx.left + cellSizePx.width * (column + boardOffsetColumn)
    fun toYPx(row: Int): Double = offsetPx.top + cellSizePx.height * (row + boardOffsetRow)
    fun toBoardRow(yPx: Int): Int = ((yPx - offsetPx.top) / cellSizePx.height).toInt()
    fun toBoardColumn(xPx: Int): Int = ((xPx - offsetPx.left) / cellSizePx.width).toInt()

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
