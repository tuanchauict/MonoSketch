/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.State
import mono.actionmanager.ActionManager
import mono.graphics.geo.Rect
import mono.html.toolbar.view.shapetool.AppearanceDataController
import mono.html.toolbar.view.shapetool.AppearanceOptionItem
import mono.html.toolbar.view.shapetool.AppearanceVisibility
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
    actionManager: ActionManager
) {
    private val appearanceDataController = AppearanceDataController(
        selectedShapesLiveData,
        shapeManagerVersionLiveData,
        actionManager
    )

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

    val shapeFillTypeState: State<AppearanceVisibility> =
        appearanceDataController.fillToolStateLiveData.toState(lifecycleOwner)

    val shapeBorderTypeState: State<AppearanceVisibility> =
        appearanceDataController.borderToolStateLiveData.toState(lifecycleOwner)

    val shapeBorderDashTypeState: State<AppearanceVisibility> =
        appearanceDataController.borderDashPatternLiveData.toState(lifecycleOwner)

    val lineStrokeTypeState: State<AppearanceVisibility> =
        appearanceDataController.lineStrokeToolStateLiveData.toState(lifecycleOwner)

    val lineStrokeDashTypeState: State<AppearanceVisibility> =
        appearanceDataController.lineStrokeDashPatternLiveData.toState(lifecycleOwner)

    val lineStartHeadState: State<AppearanceVisibility> =
        appearanceDataController.lineStartHeadToolStateLiveData.toState(lifecycleOwner)

    val lineEndHeadState: State<AppearanceVisibility> =
        appearanceDataController.lineEndHeadToolStateLiveData.toState(lifecycleOwner)

    val appearanceVisibilityState: State<Boolean> =
        appearanceDataController.hasAnyVisibleToolLiveData.toState(lifecycleOwner)

    val hasAnyToolState: State<Boolean> =
        singleShapeLiveData.map { it != null }.toState(lifecycleOwner)

    val fillOptions: List<AppearanceOptionItem> = appearanceDataController.fillOptions

    val strokeOptions: List<AppearanceOptionItem> = appearanceDataController.strokeOptions

    val headOptions: List<AppearanceOptionItem> = appearanceDataController.headOptions
}
