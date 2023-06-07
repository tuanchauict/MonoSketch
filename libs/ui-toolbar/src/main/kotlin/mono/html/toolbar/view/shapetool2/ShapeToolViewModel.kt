/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.State
import mono.actionmanager.ActionManager
import mono.actionmanager.RetainableActionType
import mono.graphics.geo.Rect
import mono.html.toolbar.view.shapetool2.viewdata.AppearanceDataController
import mono.html.toolbar.view.shapetool2.viewdata.AppearanceOptionItem
import mono.html.toolbar.view.shapetool2.viewdata.CloudItemSelectionState
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.extra.style.StraightStrokeDashPattern
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
        combineLiveData(
            actionManager.retainableActionLiveData,
            ShapeExtraManager.defaultExtraStateUpdateLiveData
        ) { action, _ -> action }
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
        singleShapeLiveData.map { it is Rectangle || it is Text }.toState(lifecycleOwner)

    val shapeFillTypeState: State<CloudItemSelectionState?> =
        appearanceDataController.fillToolStateLiveData.toState(lifecycleOwner)

    val shapeBorderTypeState: State<CloudItemSelectionState?> =
        appearanceDataController.borderToolStateLiveData.toState(lifecycleOwner)

    val shapeBorderDashTypeState: State<StraightStrokeDashPattern?> =
        appearanceDataController.borderDashPatternLiveData.toState(lifecycleOwner)

    val shapeBorderRoundedCornerState: State<Boolean?> =
        appearanceDataController.borderRoundedCornerLiveData.toState(lifecycleOwner)

    val lineStrokeTypeState: State<CloudItemSelectionState?> =
        appearanceDataController.lineStrokeToolStateLiveData.toState(lifecycleOwner)

    val lineStrokeDashTypeState: State<StraightStrokeDashPattern?> =
        appearanceDataController.lineStrokeDashPatternLiveData.toState(lifecycleOwner)

    val lineStrokeRoundedCornerState: State<Boolean?> =
        appearanceDataController.lineStrokeRoundedCornerLiveData.toState(lifecycleOwner)

    val lineStartHeadState: State<CloudItemSelectionState?> =
        appearanceDataController.lineStartHeadToolStateLiveData.toState(lifecycleOwner)

    val lineEndHeadState: State<CloudItemSelectionState?> =
        appearanceDataController.lineEndHeadToolStateLiveData.toState(lifecycleOwner)

    val appearanceVisibilityState: State<Boolean> =
        appearanceDataController.hasAnyVisibleToolLiveData.toState(lifecycleOwner)

    private val textAlignLiveData: LiveData<TextAlign?> =
        createTextAlignLiveData(singleShapeLiveData, retainableActionLiveData)
    val textAlignState: State<TextAlign?> = textAlignLiveData.toState(lifecycleOwner)

    val hasAnyToolState: State<Boolean> = combineLiveData(
        singleShapeLiveData.map { it != null },
        appearanceDataController.hasAnyVisibleToolLiveData,
        textAlignLiveData.map { it != null }
    ) { states -> states.any { it == true } }
        .toState(lifecycleOwner)

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
