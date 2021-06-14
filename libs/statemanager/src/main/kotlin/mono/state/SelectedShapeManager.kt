package mono.state

import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.shape.shape.AbstractShape

/**
 * A model class to manage selected shapes and render the selection bound.
 */
class SelectedShapeManager {

    private val selectedShapesMutableLiveData: MutableLiveData<Set<AbstractShape>> =
        MutableLiveData(emptySet())
    val selectedShapesLiveData: LiveData<Set<AbstractShape>> = selectedShapesMutableLiveData

    val selectedShapes: Set<AbstractShape>
        get() = selectedShapesLiveData.value

    fun addSelectedShape(shape: AbstractShape?) {
        if (shape != null) {
            selectedShapesMutableLiveData.value += shape
        }
    }

    fun toggleSelection(shape: AbstractShape) {
        val selectedShapes = if (shape in selectedShapes) {
            selectedShapes - shape
        } else {
            selectedShapes + shape
        }
        selectedShapesMutableLiveData.value = selectedShapes
    }
    
    fun clearSelectedShapes() {
        selectedShapesMutableLiveData.value = emptySet()
    }
}
