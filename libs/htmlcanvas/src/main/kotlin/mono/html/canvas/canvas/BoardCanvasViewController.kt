package mono.html.canvas.canvas

import mono.common.Characters
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
                val char = board.get(left, top)
                if (char != Characters.TRANSPARENT_CHAR) {
                    context.fillText("$char", toXPx(col), toYPx(row))
                }
            }
        }
        context.fillText("This is a sample █", 100.0, 100.0)
    }
}
