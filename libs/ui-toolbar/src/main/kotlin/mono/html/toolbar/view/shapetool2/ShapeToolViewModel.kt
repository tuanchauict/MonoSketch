/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.State
import mono.actionmanager.ActionManager
import mono.actionmanager.RetainableActionType
import mono.graphics.geo.Rect
import mono.html.toolbar.view.shapetool.AppearanceDataController
import mono.html.toolbar.view.shapetool.AppearanceOptionItem
import mono.html.toolbar.view.shapetool.AppearanceVisibility
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.extra.style.TextAlign
import mono.shape.shape.AbstractShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
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

    private val retainableActionLiveData: LiveData<RetainableActionType> = combineLiveData(
        actionManager.retainableActionLiveData,
        ShapeExtraManager.defaultExtraStateUpdateLiveData
    ) { action, _ -> action }

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

    val textAlignState: State<TextAlign?> =
        createTextAlignLiveData(singleShapeLiveData, retainableActionLiveData)
            .toState(lifecycleOwner)

    val hasAnyToolState: State<Boolean> =
        singleShapeLiveData.map { it != null }.toState(lifecycleOwner)

    val fillOptions: List<AppearanceOptionItem> = appearanceDataController.fillOptions

    val strokeOptions: List<AppearanceOptionItem> = appearanceDataController.strokeOptions

    val headOptions: List<AppearanceOptionItem> = appearanceDataController.headOptions

    private fun createTextAlignLiveData(
        selectedShapeLiveData: LiveData<AbstractShape?>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<TextAlign?> {
        val selectedTextAlignLiveData: LiveData<TextAlign?> = selectedShapeLiveData.map {
            val text = it as? Text
            val editableText = text?.takeIf(Text::isTextEditable)
            editableText?.extra?.textAlign
        }
        val defaultTextAlignLiveData: LiveData<TextAlign?> = retainableActionTypeLiveData.map {
            if (it == RetainableActionType.ADD_TEXT) {
                ShapeExtraManager.defaultTextAlign
            } else {
                null
            }
        }
        return combineLiveData(
            selectedTextAlignLiveData,
            defaultTextAlignLiveData
        ) { selected, default -> selected ?: default }
    }
}
