package mono.html.canvas.canvas

import mono.graphics.board.MonoBoard
import mono.graphics.board.Pixel
import org.w3c.dom.HTMLCanvasElement

internal class BoardCanvasViewController(
    canvas: HTMLCanvasElement,
    private val board: MonoBoard
) : BaseCanvasViewController(canvas) {

    override fun drawInternal() {
        for (row in drawingInfo.boardRowRange) {
            val rowYPx = drawingInfo.toYPx(row.toDouble())
            for (col in drawingInfo.boardColumnRange) {
                val xPx = drawingInfo.toXPx(col + 0.07)
                drawPixel(board.get(col, row), xPx, rowYPx)
            }
        }
    }

    private fun drawPixel(pixel: Pixel, xPx: Double, yPx: Double) {
        if (!pixel.isTransparent) {
            context.fillStyle = pixel.highlight.paintColor
            context.fillText("${pixel.char}", xPx, yPx)
        }
    }
}
