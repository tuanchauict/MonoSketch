package mono.state

import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.shape.ShapeManager
import mono.shape.remove
import mono.shape.shape.AbstractShape

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
}
