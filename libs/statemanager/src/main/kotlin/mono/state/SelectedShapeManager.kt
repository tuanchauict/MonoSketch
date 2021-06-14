package mono.state

import mono.common.exhaustive
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.shape.ShapeManager
import mono.shape.command.ChangeBound
import mono.shape.remove
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import mono.shapebound.LineInteractionBound
import mono.shapebound.ScalableInteractionBound
import mono.state.command.text.EditTextShapeHelper

/**
 * A model class to manage selected shapes and render the selection bound.
 */
class SelectedShapeManager(
    private val shapeManager: ShapeManager,
    private val canvasManager: CanvasViewController,
    private val requestRedraw: () -> Unit
) {
    var selectedShapes: Set<AbstractShape> = emptySet()
        private set

    private val selectedShapesMutableLiveData: MutableLiveData<Set<AbstractShape>> =
        MutableLiveData(emptySet())
    val selectedShapesLiveData: LiveData<Set<AbstractShape>> = selectedShapesMutableLiveData

    fun setSelectedShapes(vararg shapes: AbstractShape?) {
        selectedShapes = shapes.filterNotNull().toSet()
        selectedShapesMutableLiveData.value = selectedShapes
        updateInteractionBound()
    }

    fun toggleSelection(shape: AbstractShape) {
        selectedShapes = if (shape in selectedShapes) {
            selectedShapes - shape
        } else {
            selectedShapes + shape
        }
        selectedShapesMutableLiveData.value = selectedShapes
        updateInteractionBound()
    }

    fun deleteSelectedShapes() {
        for (shape in selectedShapes) {
            shapeManager.remove(shape)
        }
        selectedShapes = emptySet()
        selectedShapesMutableLiveData.value = selectedShapes
        updateInteractionBound()
    }

    fun moveSelectedShape(offsetRow: Int, offsetCol: Int) {
        for (shape in selectedShapes) {
            val bound = shape.bound
            val newPosition = Point(bound.left + offsetCol, bound.top + offsetRow)
            val newBound = shape.bound.copy(position = newPosition)
            shapeManager.execute(ChangeBound(shape, newBound))
        }
        selectedShapesMutableLiveData.value = selectedShapes
        updateInteractionBound()
    }

    fun editSelectedShapes() {
        val singleShape = selectedShapes.singleOrNull() ?: return
        when (singleShape) {
            is Text -> EditTextShapeHelper.showEditTextDialog(shapeManager, singleShape)
            is Line,
            is Rectangle,
            is MockShape,
            is Group -> Unit
        }.exhaustive
    }

    fun updateInteractionBound() {
        val bounds = selectedShapes.mapNotNull {
            when (it) {
                is Rectangle,
                is Text -> ScalableInteractionBound(it.id, it.bound)
                is Line -> LineInteractionBound(it.id, it.edges)
                is Group -> null // TODO: Add new Interaction bound type for Group
                is MockShape -> null
            }
        }
        canvasManager.drawInteractionBounds(bounds)
        requestRedraw()
    }

    fun setSelectionBound(bound: Rect?) {
        canvasManager.drawSelectionBound(bound)
    }

    fun isInSelectionBounds(point: Point): Boolean = selectedShapes.any { it.contains(point) }
}
