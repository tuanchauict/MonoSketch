package mono.html.canvas

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

internal abstract class BaseCanvasViewController(canvas: HTMLCanvasElement) {
    protected val context: CanvasRenderingContext2D =
        canvas.getContext("2d") as CanvasRenderingContext2D

    protected var font: String = ""
    protected var cellWidthPx: Double = 0.0
    protected var cellHeightPx: Double = 0.0

    init {
        setFont(DEFAULT_FONT)
    }

    fun setFont(font: String) {
        this.font = font
        context.font = font
        val (cellWidthPx, cellHeightPx) = context.getCellSizePx()
        this.cellWidthPx = cellWidthPx
        this.cellHeightPx = cellHeightPx
    }

    private fun CanvasRenderingContext2D.getCellSizePx(): Pair<Double, Double> {
        val metrics = measureText("M")
        val cWidth = metrics.width
        val cHeight = metrics.fontBoundingBoxAscent + metrics.fontBoundingBoxDescent
        return cWidth to cHeight
    }

    companion object {
        internal const val DEFAULT_FONT = "15px monospace"
    }
}
