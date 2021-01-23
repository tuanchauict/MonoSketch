package mono.html.canvas.canvas

import mono.graphics.board.MonoBoard
import org.w3c.dom.HTMLCanvasElement

internal class BoardCanvasViewController(
    canvas: HTMLCanvasElement,
    private val board: MonoBoard
) : BaseCanvasViewController(canvas) {

    override fun drawInternal() {
        val columnRange = this.boardColumnRange
        for (row in boardRowRange) {
            for (col in columnRange) {
                val pixel = board.get(col, row)
                if (!pixel.isTransparent) {
                    context.fillStyle = pixel.highlight.paintColor
                    context.fillText("${pixel.char}", toXPx(col), toYPx(row))
                }
            }
        }
    }
}
