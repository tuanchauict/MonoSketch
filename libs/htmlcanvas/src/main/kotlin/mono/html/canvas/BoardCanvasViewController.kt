package mono.html.canvas

import mono.graphics.board.MonoBoard
import org.w3c.dom.HTMLCanvasElement

internal class BoardCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {

    init {
        setFont(DEFAULT_FONT)
    }

    fun draw(board: MonoBoard) {
        TODO()
    }
}
