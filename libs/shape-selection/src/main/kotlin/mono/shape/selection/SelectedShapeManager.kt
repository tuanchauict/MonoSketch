/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.selection

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

    private val focusingShapeMutableLiveData: MutableLiveData<FocusingShape?> =
        MutableLiveData(null)
    val focusingShapeLiveData: LiveData<FocusingShape?> = focusingShapeMutableLiveData

    fun addSelectedShape(shape: AbstractShape) {
        selectedShapesMutableLiveData.value += shape
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

    fun setFocusingShape(shape: AbstractShape?, focusType: ShapeFocusType) {
        focusingShapeMutableLiveData.value =
            if (shape == null) null else FocusingShape(shape, focusType)
    }

    fun getFocusingType(shape: AbstractShape?): ShapeFocusType? =
        if (shape == focusingShapeLiveData.value?.shape) {
            focusingShapeLiveData.value?.focusType
        } else {
            null
        }

    /**
     * An enum class defines the type of focus.
     */
    enum class ShapeFocusType {
        LINE_CONNECTING,
        SELECT_MODE_HOVER
    }

    /**
     * A model class to store the focusing shape and its focusing type.
     */
    data class FocusingShape(val shape: AbstractShape, val focusType: ShapeFocusType)
}
