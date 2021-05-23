package mono.html.canvas.canvas

import kotlinx.html.dom.append
import kotlinx.html.js.canvas
import mono.common.firstOrNull
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import mono.graphics.geo.SizeF
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.CanvasTextBaseline
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.LEFT
import org.w3c.dom.MIDDLE
import kotlin.math.ceil

/**
 * A controller class to manage drawing info for the other canvas.
 */
internal class DrawingInfoController(container: HTMLDivElement) {
    private val context: CanvasRenderingContext2D

    private val drawingInfoMutableLiveData: MutableLiveData<DrawingInfo> =
        MutableLiveData(DrawingInfo())
    val drawingInfoLiveData: LiveData<DrawingInfo> = drawingInfoMutableLiveData

    init {
        container.append {
            canvas(CLASS_NAME) {}
        }
        val canvas = container.getElementsByClassName(CLASS_NAME).firstOrNull<HTMLCanvasElement>()!!
        context = canvas.getContext("2d") as CanvasRenderingContext2D

        setSize(canvas.width, canvas.height)
        setFont(15)
    }

    fun setFont(fontSize: Int) {
        drawingInfoMutableLiveData.value =
            drawingInfoMutableLiveData.value.copy(
                cellSizePx = context.getCellSizePx(fontSize),
                font = "normal normal normal ${fontSize}px Courier",
                boldFont = "normal normal 600 ${fontSize}px Courier",
                fontSize = fontSize
            )
    }

    fun setSize(widthPx: Int, heightPx: Int) {
        drawingInfoMutableLiveData.value =
            drawingInfoMutableLiveData.value.copy(canvasSizePx = Size(widthPx, heightPx))
    }

    private fun CanvasRenderingContext2D.getCellSizePx(fontSize: Int): SizeF {
        context.font = font
        context.textAlign = CanvasTextAlign.LEFT
        context.textBaseline = CanvasTextBaseline.MIDDLE
        val metrics = measureText("█")
        val cWidth = metrics.width
        val cHeight = fontSize.toDouble()
        return SizeF(cWidth, cHeight)
    }

    internal data class DrawingInfo(
        val offsetPx: Point = Point.ZERO,
        val cellSizePx: SizeF = SizeF(1.0, 1.0),
        val canvasSizePx: Size = Size(1, 1),
        val font: String = "",
        val boldFont: String = "",
        val fontSize: Int = 0
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

        fun toXPx(column: Double): Double = offsetPx.left + cellSizePx.width * column
        fun toYPx(row: Double): Double = offsetPx.top + cellSizePx.height * row
        fun toBoardRow(yPx: Int): Int = ((yPx - offsetPx.top) / cellSizePx.height).toInt()
        fun toBoardColumn(xPx: Int): Int = ((xPx - offsetPx.left) / cellSizePx.width).toInt()
    }

    companion object {
        private const val CLASS_NAME = "drawing-info"
    }
}
