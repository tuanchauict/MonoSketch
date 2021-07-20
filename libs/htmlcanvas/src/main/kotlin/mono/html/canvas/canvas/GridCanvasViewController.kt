package mono.html.canvas.canvas

import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D

internal class GridCanvasViewController(
    lifecycleOwner: LifecycleOwner,
    canvas: HTMLCanvasElement,
    drawingInfoLiveData: LiveData<DrawingInfoController.DrawingInfo>
) : BaseCanvasViewController(canvas) {

    init {
        drawingInfoLiveData.observe(lifecycleOwner, listener = ::setDrawingInfo)
        drawingInfoLiveData.observe(lifecycleOwner, 0) { draw() }
    }

    override fun drawInternal() {
        context.strokeStyle = "#d9d9d9"
        context.lineWidth = 0.25
        context.stroke(createGridPath())

        context.strokeStyle = "#BBBBBB"
        context.lineWidth = 1.0
        val zeroLines = Path2D().apply {
            addHLine(
                0.0,
                drawingInfo.toYPx(0.0),
                drawingInfo.canvasSizePx.width.toDouble()
            )
            addVLine(
                drawingInfo.toXPx(0.0),
                0.0,
                drawingInfo.canvasSizePx.height.toDouble()
            )
        }
        context.stroke(zeroLines)
    }

    private fun createGridPath(): Path2D = Path2D().apply {
        val zeroX = drawingInfo.toXPx(drawingInfo.boardColumnRange.first.toDouble() - 1.0)
        val maxX = drawingInfo.toXPx(drawingInfo.boardColumnRange.last.toDouble() + 1.0)
        val zeroY = drawingInfo.toYPx(drawingInfo.boardRowRange.first.toDouble() - 1.0)
        val maxY = drawingInfo.toYPx(drawingInfo.boardRowRange.last.toDouble() + 1.0)

        for (row in drawingInfo.boardRowRange) {
            val y = drawingInfo.toYPx(row.toDouble())
            addHLine(zeroX, y, maxX - zeroX)
        }

        for (col in drawingInfo.boardColumnRange) {
            val x = drawingInfo.toXPx(col.toDouble())
            addVLine(x, zeroY, maxY - zeroY)
        }
    }
}
