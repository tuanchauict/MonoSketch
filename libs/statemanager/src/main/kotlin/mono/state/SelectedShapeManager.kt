package mono.state

import mono.common.exhaustive
import mono.graphics.geo.Point
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
import mono.state.command.text.EditTextShapeHelper

/**
 * A model class to manage selected shapes and render the selection bound.
 */
class SelectedShapeManager(private val shapeManager: ShapeManager) {

    private val selectedShapesMutableLiveData: MutableLiveData<Set<AbstractShape>> =
        MutableLiveData(emptySet())
    val selectedShapesLiveData: LiveData<Set<AbstractShape>> = selectedShapesMutableLiveData

    val selectedShapes: Set<AbstractShape>
        get() = selectedShapesLiveData.value

    fun setSelectedShapes(vararg shapes: AbstractShape?) {
        val selectedShapes = shapes.filterNotNull().toSet()
        selectedShapesMutableLiveData.value = selectedShapes
    }

    fun toggleSelection(shape: AbstractShape) {
        val selectedShapes = if (shape in selectedShapes) {
            selectedShapes - shape
        } else {
            selectedShapes + shape
        }
        selectedShapesMutableLiveData.value = selectedShapes
    }

    fun deleteSelectedShapes() {
        for (shape in selectedShapes) {
            shapeManager.remove(shape)
        }
        selectedShapesMutableLiveData.value = emptySet()
    }

    fun moveSelectedShape(offsetRow: Int, offsetCol: Int) {
        for (shape in selectedShapes) {
            val bound = shape.bound
            val newPosition = Point(bound.left + offsetCol, bound.top + offsetRow)
            val newBound = shape.bound.copy(position = newPosition)
            shapeManager.execute(ChangeBound(shape, newBound))
        }
        selectedShapesMutableLiveData.value = selectedShapes
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

    fun isInSelectionBounds(point: Point): Boolean = selectedShapes.any { it.contains(point) }
}
