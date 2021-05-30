package mono.html.canvas.canvas

import mono.graphics.board.MonoBoard
import mono.graphics.board.Pixel
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.HTMLCanvasElement

internal class BoardCanvasViewController(
    lifecycleOwner: LifecycleOwner,
    canvas: HTMLCanvasElement,
    private val board: MonoBoard,
    drawingInfoLiveData: LiveData<DrawingInfoController.DrawingInfo>
) : BaseCanvasViewController(canvas) {

    init {
        drawingInfoLiveData.observe(lifecycleOwner, listener = ::setDrawingInfo)
    }

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
            context.font = drawingInfo.font
            drawText(pixel.char.toString(), row, column)
        }
    }
}
