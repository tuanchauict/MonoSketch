/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.canvas.canvas

import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.board.Pixel
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.ui.theme.ThemeColor
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
        context.font = drawingInfo.font
        for (row in drawingInfo.boardRowRange) {
            for (col in drawingInfo.boardColumnRange) {
                drawPixel(board.get(col, row), row, col)
            }
        }
    }

    private fun drawPixel(pixel: Pixel, row: Int, column: Int) {
        if (!pixel.isTransparent) {
            val color = when (pixel.highlight) {
                Highlight.NO -> ThemeColor.Shape
                Highlight.SELECTED -> ThemeColor.ShapeSelected
                Highlight.TEXT_EDITING -> ThemeColor.ShapeTextEditing
            }
            context.fillStyle = color.colorCode
            drawText(pixel.char.toString(), row, column)
        }
    }
}
