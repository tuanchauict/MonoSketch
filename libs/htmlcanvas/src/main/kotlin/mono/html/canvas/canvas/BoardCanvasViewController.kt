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
            for (col in drawingInfo.boardColumnRange) {
                drawPixel(board.get(col, row), row, col)
            }
        }
    }

    private fun drawPixel(pixel: Pixel, row: Int, column: Int) {
        if (!pixel.isTransparent) {
            context.fillStyle = pixel.highlight.paintColor
            context.font = if (pixel.char == '|') drawingInfo.boldFont else drawingInfo.font
            drawText(pixel.char.toString(), row, column)
        }
    }
}
