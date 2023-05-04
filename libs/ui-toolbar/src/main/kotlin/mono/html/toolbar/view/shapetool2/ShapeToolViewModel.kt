/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.mutableStateOf
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.shape.AbstractShape

internal class ShapeToolViewModel(
    lifecycleOwner: LifecycleOwner,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
    shapeManagerVersionLiveData: LiveData<Int>,
    shapeToolVisibilityLiveData: LiveData<Boolean>
) {
    val hasAnyToolState = mutableStateOf(false)
}
