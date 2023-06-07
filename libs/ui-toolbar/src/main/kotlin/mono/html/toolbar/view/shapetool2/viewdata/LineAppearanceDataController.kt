/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2.viewdata

import mono.actionmanager.RetainableActionType
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.extra.LineExtra
import mono.shape.extra.style.StraightStrokeDashPattern
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text

/**
 * Data controller class of Rectangle appearance
 */
internal class LineAppearanceDataController(
    shapesLiveData: LiveData<Set<AbstractShape>>,
    retainableActionLiveData: LiveData<RetainableActionType>
) {
    private val singleLineExtraLiveData: LiveData<LineExtra?> = shapesLiveData.map {
        when (val line = it.singleOrNull()) {
            is Line -> line.extra
            is Group,
            is MockShape,
            is Rectangle,
            is Text,
            null -> null
        }
    }

    private val defaultLineExtraLiveData: LiveData<LineExtra?> =
        retainableActionLiveData.map {
            when (it) {
                RetainableActionType.ADD_LINE -> ShapeExtraManager.defaultLineExtra
                RetainableActionType.IDLE,
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT -> null
            }
        }

    val strokeToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createLineStrokeAppearanceVisibilityLiveData()
    val strokeDashPatternLiveData: LiveData<StraightStrokeDashPattern?> =
        createLineStrokeDashPatternLiveData()
    val strokeRoundedCornerLiveData: LiveData<Boolean?> =
        createLineStrokeRoundedCornerLiveData(shapesLiveData)
    val startHeadToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createStartHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val endHeadToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createEndHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)

    val hasAnyVisibleTollLiveData: LiveData<Boolean> = combineLiveData(
        strokeToolStateLiveData,
        strokeDashPatternLiveData,
        strokeRoundedCornerLiveData,
        startHeadToolStateLiveData,
        endHeadToolStateLiveData
    ) { list -> list.any { it != null } }

    private val defaultLineExtra: LineExtra
        get() = ShapeExtraManager.defaultLineExtra

    private fun createLineStrokeAppearanceVisibilityLiveData(): LiveData<CloudItemSelectionState?> {
        val selectedVisibilityLiveData =
            singleLineExtraLiveData.map { it?.toStrokeVisibilityState() }

        val defaultVisibilityLiveData =
            defaultLineExtraLiveData.map { it?.toStrokeVisibilityState() }

        return selectedOrDefault(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createLineStrokeDashPatternLiveData(): LiveData<StraightStrokeDashPattern?> {
        val selectedDashPatternLiveData: LiveData<StraightStrokeDashPattern?> =
            singleLineExtraLiveData
                .map { it?.takeIf { it.isStrokeEnabled } }
                .map { it?.dashPattern }
        val defaultDashPatternLiveData: LiveData<StraightStrokeDashPattern?> =
            defaultLineExtraLiveData
                .map { it?.takeIf { it.isStrokeEnabled } }
                .map { it?.dashPattern }
        return selectedOrDefault(
            selectedDashPatternLiveData,
            defaultDashPatternLiveData
        )
    }

    private fun createLineStrokeRoundedCornerLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>
    ): LiveData<Boolean?> {
        val selectedCornerPatternLiveData = selectedShapesLiveData.map {
            when (val shape = it.singleOrNull()) {
                is Line -> shape.extra.isRoundedCorner
                is Rectangle,
                is Text,
                is Group,
                is MockShape,
                null -> null
            }
        }
        val defaultCornerPatternLiveData = defaultLineExtraLiveData.map { it?.isRoundedCorner }
        return combineLiveData(
            selectedCornerPatternLiveData,
            defaultCornerPatternLiveData
        ) { selected, default -> selected ?: default }
    }


    private fun createStartHeadAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<CloudItemSelectionState?> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when (val shape = it.singleOrNull()) {
                is Line -> shape.toStartHeadAppearanceVisibilityState()
                null,
                is Rectangle,
                is Text,
                is Group,
                is MockShape -> null
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map { type ->
            val defaultState = when (type) {
                RetainableActionType.ADD_LINE ->
                    defaultLineExtra.userSelectedStartAnchor

                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            defaultState?.let {
                CloudItemSelectionState(defaultLineExtra.isStartAnchorEnabled, it.id)
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createEndHeadAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<CloudItemSelectionState?> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when (val shape = it.singleOrNull()) {
                is Line -> shape.toEndHeadAppearanceVisibilityState()
                null,
                is Rectangle,
                is Text,
                is Group,
                is MockShape -> null
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map { type ->
            val defaultState = when (type) {
                RetainableActionType.ADD_LINE ->
                    defaultLineExtra.userSelectedEndAnchor

                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            defaultState?.let {
                CloudItemSelectionState(defaultLineExtra.isEndAnchorEnabled, it.id)
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun LineExtra.toStrokeVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(isStrokeEnabled, userSelectedStrokeStyle.id)

    private fun Line.toStartHeadAppearanceVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(extra.isStartAnchorEnabled, extra.userSelectedStartAnchor.id)

    private fun Line.toEndHeadAppearanceVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(extra.isEndAnchorEnabled, extra.userSelectedEndAnchor.id)

    private fun createAppearanceVisibilityLiveData(
        selectedShapeVisibilityLiveData: LiveData<CloudItemSelectionState?>,
        defaultVisibilityLiveData: LiveData<CloudItemSelectionState?>
    ): LiveData<CloudItemSelectionState?> = combineLiveData(
        selectedShapeVisibilityLiveData,
        defaultVisibilityLiveData
    ) { selected, default -> selected ?: default }

    private fun <T> selectedOrDefault(
        selectedLiveData: LiveData<T?>,
        defaultLiveData: LiveData<T?>
    ): LiveData<T?> = combineLiveData(selectedLiveData, defaultLiveData) { s, d -> s ?: d }
}
