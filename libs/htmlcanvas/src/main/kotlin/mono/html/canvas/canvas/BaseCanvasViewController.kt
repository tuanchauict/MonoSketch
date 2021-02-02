package mono.html.canvas.canvas

import kotlinx.browser.window
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import mono.graphics.geo.SizeF
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.CanvasTextBaseline
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.LEFT
import org.w3c.dom.MIDDLE
import org.w3c.dom.TOP
import kotlin.math.ceil
import kotlin.math.max

internal abstract class BaseCanvasViewController(private val canvas: HTMLCanvasElement) {
    protected val context: CanvasRenderingContext2D =
        canvas.getContext("2d") as CanvasRenderingContext2D

    private var font: String = ""
    private var fontSize: Int = 0

    internal var drawingInfo: DrawingInfo

    init {
        drawingInfo = DrawingInfo(canvasSizePx = Size(canvas.width, canvas.height))
        setFont(15)
    }

    internal fun setFont(fontSize: Int) {
        this.fontSize = fontSize
        this.font = "normal normal ${fontSize}px monospace"

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
        context.imageSmoothingEnabled = false
        drawInternal()
    }

    protected abstract fun drawInternal()

    internal data class DrawingInfo(
        val offsetPx: Point = Point.ZERO,
        val cellSizePx: SizeF = SizeF(1.0, 1.0),
        val canvasSizePx: Size = Size(1, 1)
    ) {
        val boundPx: Rect = Rect(offsetPx, canvasSizePx)

        private val boardOffsetRow: Int = (-offsetPx.top / cellSizePx.height).toInt()
        private val boardOffsetColumn: Int = (-offsetPx.left / cellSizePx.width).toInt()
        private val rowCount: Int = ceil(canvasSizePx.height / cellSizePx.height).toInt()
        private val columnCount: Int = ceil(canvasSizePx.width / cellSizePx.width).toInt()

        val boardBound: Rect = Rect.byLTWH(boardOffsetColumn, boardOffsetRow, columnCount, rowCount)

        internal val boardRowRange: IntRange = boardOffsetRow..(boardOffsetRow + rowCount)
        internal val boardColumnRange: IntRange =
            boardOffsetColumn..(boardOffsetColumn + columnCount)

        fun toXPx(column: Int): Double = offsetPx.left + cellSizePx.width * column
        fun toYPx(row: Int): Double = offsetPx.top + cellSizePx.height * row
        fun toBoardRow(yPx: Int): Int = ((yPx - offsetPx.top) / cellSizePx.height).toInt()
        fun toBoardColumn(xPx: Int): Int = ((xPx - offsetPx.left) / cellSizePx.width).toInt()
    }
}
