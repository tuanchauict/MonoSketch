/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.export

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.geo.Rect
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group

/**
 * A helper class for exporting selected shapes.
 */
class ExportShapesHelper(
    private val getBitmap: (AbstractShape) -> MonoBitmap?,
    private val getShadowBitmap: (AbstractShape) -> MonoBitmap?,
    private val setClipboardText: (String) -> Unit
) {

    fun exportText(shapes: List<AbstractShape>, isModalRequired: Boolean) {
        if (shapes.isEmpty()) {
            return
        }

        val left = shapes.minOf { it.bound.left }
        val right = shapes.maxOf { it.bound.right } + 1
        val top = shapes.minOf { it.bound.top }
        val bottom = shapes.maxOf { it.bound.bottom } + 1
        val window = Rect.byLTRB(left, top, right, bottom)

        val exportingBoard = MonoBoard().apply { clearAndSetWindow(window) }
        drawShapesOntoExportingBoard(exportingBoard, shapes)

        val text = exportingBoard.toStringInBound(window)
        if (isModalRequired) {
            ExportShapesModal().show(text)
        } else {
            setClipboardText(text)
        }
    }

    private fun drawShapesOntoExportingBoard(board: MonoBoard, shapes: Collection<AbstractShape>) {
        for (shape in shapes) {
            if (shape is Group) {
                drawShapesOntoExportingBoard(board, shape.items)
                continue
            }
            val bitmap = getBitmap(shape) ?: continue
            val shadowBitmap = getShadowBitmap(shape)
            if (shadowBitmap != null) {
                board.fill(shape.bound.position, shadowBitmap, Highlight.NO)
            }
            board.fill(shape.bound.position, bitmap, Highlight.NO)
        }
    }
}
