package mono.state

import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import mono.html.canvas.CanvasViewController.BoundType
import mono.shape.shape.AbstractShape

/**
 * A model class to manage selected shapes and render the selection bound.
 */
class SelectedShapeManager(private val canvasManager: CanvasViewController) {
    var selectedShapes: Set<AbstractShape> = emptySet()
        private set

    fun set(shapes: Set<AbstractShape>) {
        selectedShapes = shapes
        val bound = if (shapes.isNotEmpty()) {
            Rect.byLTRB(
                shapes.minOf { it.bound.left },
                shapes.minOf { it.bound.top },
                shapes.maxOf { it.bound.right },
                shapes.maxOf { it.bound.bottom }
            )
        } else {
            null
        }
        canvasManager.drawInteractionBound(bound, BoundType.NINE_DOTS)
    }
}
