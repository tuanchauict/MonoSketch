package mono.html.canvas.canvas

import mono.graphics.board.MonoBoard
import org.w3c.dom.HTMLCanvasElement

internal class BoardCanvasViewController(
    canvas: HTMLCanvasElement,
    private val board: MonoBoard
) : BaseCanvasViewController(canvas) {

    override fun drawInternal() {
        for (row in drawingInfo.boardRowRange) {
            val rowYPx = drawingInfo.toYPx(row)
            for (col in drawingInfo.boardColumnRange) {
                val pixel = board.get(col, row)
                if (!pixel.isTransparent) {
                    context.fillStyle = pixel.highlight.paintColor
                    context.fillText(
                        "${pixel.char}",
                        drawingInfo.toXPx(col),
                        rowYPx
                    )
                }
            }
        }
    }
}
