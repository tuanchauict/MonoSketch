package mono.html.canvas.canvas

import mono.graphics.board.MonoBoard
import org.w3c.dom.HTMLCanvasElement

internal class BoardCanvasViewController(
    canvas: HTMLCanvasElement,
    private val board: MonoBoard
) : BaseCanvasViewController(canvas) {

    override fun drawInternal() {
        for (row in rowRange) {
            for (col in columnRange) {
                val left = col // TODO: correct with board offset
                val top = row // TODO: correct with board offset
                val pixel = board.get(left, top)
                if (!pixel.isTransparent) {
                    context.fillStyle = pixel.highlight.paintColor
                    context.fillText("${pixel.char}", toXPx(col), toYPx(row))
                }
            }
        }
        context.fillText("This is a sample █", 100.0, 100.0)
    }
}
