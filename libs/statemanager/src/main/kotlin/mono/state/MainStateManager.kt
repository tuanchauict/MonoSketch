package mono.state

import mono.graphics.bitmap.MonoBitmapManager
import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import mono.lifecycle.LifecycleOwner
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Rectangle

/**
 * A class which is connect components in the app.
 */
class MainStateManager(
    lifecycleOwner: LifecycleOwner,
    private val mainBoard: MonoBoard,
    private val shapeManager: ShapeManager,
    private val bitmapManager: MonoBitmapManager,
    private val canvasManager: CanvasViewController
) {

    init {
        shapeManager.versionLiveData.distinctUntilChange().observe(lifecycleOwner, 2) {
            mainBoard.redraw()
        }
        mainBoard.onBoardStateChangeLiveData.observe(lifecycleOwner, 2) {
            canvasManager.drawBoard()
        }

        shapeManager.add(Rectangle(Rect.byLTWH(10, 10, 10, 10)))
        shapeManager.add(Rectangle(Rect.byLTWH(15, 15, 10, 10)))
    }

    private fun MonoBoard.redraw() {
        // TODO: Reset board
        for (shape in shapeManager.shapes) {
            drawShape(shape)
        }
    }

    private fun MonoBoard.drawShape(shape: AbstractShape) {
        if (shape is Group) {
            for (child in shape.items) {
                drawShape(shape)
            }
            return
        }
        val bitmap = bitmapManager.getBitmap(shape) ?: return
        fill(shape.bound.position, bitmap, Highlight.NO)
    }
}
