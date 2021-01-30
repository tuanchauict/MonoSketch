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
            val rowYPx = drawingInfo.toYPx(row)
            for (col in drawingInfo.boardColumnRange) {
                drawPixel(board.get(col, row), drawingInfo.toXPx(col), rowYPx)
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
