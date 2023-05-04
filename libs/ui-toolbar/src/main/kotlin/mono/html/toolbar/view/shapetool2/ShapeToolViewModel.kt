/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.State
import mono.graphics.geo.Rect
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.shape.AbstractShape
import mono.shape.shape.Rectangle
import mono.ui.compose.ext.toState

internal class ShapeToolViewModel(
    lifecycleOwner: LifecycleOwner,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
    shapeManagerVersionLiveData: LiveData<Int>,
    shapeToolVisibilityLiveData: LiveData<Boolean>
) {
    private val singleShapeLiveData: LiveData<AbstractShape?> = combineLiveData(
        selectedShapesLiveData,
        shapeManagerVersionLiveData
    ) { selected, _ -> selected.singleOrNull() }

    private val reorderToolVisibilityLiveData: LiveData<Boolean> = singleShapeLiveData.map {
        it != null
    }
    val reorderToolVisibilityState: State<Boolean> =
        reorderToolVisibilityLiveData.toState(lifecycleOwner)

    val singleShapeBoundState: State<Rect?> =
        singleShapeLiveData.map { it?.bound }.toState(lifecycleOwner)

    val singleShapeResizeableState: State<Boolean> =
        singleShapeLiveData.map { it is Rectangle }.toState(lifecycleOwner)

    val hasAnyToolState: State<Boolean> =
        singleShapeLiveData.map { it != null }.toState(lifecycleOwner)
}
