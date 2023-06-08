/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2.viewdata

import mono.actionmanager.RetainableActionType
import mono.html.toolbar.view.shapetool2.CloudItemSelectionState
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
 * Data controller class of Line appearance
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

    private val defaultLineExtraLiveData: LiveData<LineExtra?> = retainableActionLiveData.map {
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
        createLineStrokeRoundedCornerLiveData()
    val startHeadToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createStartHeadAppearanceVisibilityLiveData()
    val endHeadToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createEndHeadAppearanceVisibilityLiveData()

    val hasAnyVisibleTollLiveData: LiveData<Boolean> = combineLiveData(
        strokeToolStateLiveData,
        strokeDashPatternLiveData,
        strokeRoundedCornerLiveData,
        startHeadToolStateLiveData,
        endHeadToolStateLiveData
    ) { list -> list.any { it != null } }

    private fun createLineStrokeAppearanceVisibilityLiveData(): LiveData<CloudItemSelectionState?> =
        selectedOrDefault(
            selectedLiveData = singleLineExtraLiveData.map { it?.toStrokeVisibilityState() },
            defaultLiveData = defaultLineExtraLiveData.map { it?.toStrokeVisibilityState() }
        )

    private fun createLineStrokeDashPatternLiveData(): LiveData<StraightStrokeDashPattern?> =
        selectedOrDefault(
            selectedLiveData = singleLineExtraLiveData.map { it?.dashPattern },
            defaultLiveData = defaultLineExtraLiveData.map { it?.dashPattern }
        )

    private fun createLineStrokeRoundedCornerLiveData(): LiveData<Boolean?> {
        val selectedCornerPatternLiveData = singleLineExtraLiveData
            .map { it?.takeIf { it.isStrokeEnabled } }
            .map { it?.isRoundedCorner }
        val defaultCornerPatternLiveData = defaultLineExtraLiveData
            .map { it?.takeIf { it.isStrokeEnabled } }
            .map { it?.isRoundedCorner }
        return selectedOrDefault(selectedCornerPatternLiveData, defaultCornerPatternLiveData)
    }

    private fun createStartHeadAppearanceVisibilityLiveData(): LiveData<CloudItemSelectionState?> =
        selectedOrDefault(
            selectedLiveData = singleLineExtraLiveData.map { it?.toStartHeadState() },
            defaultLiveData = defaultLineExtraLiveData.map { it?.toStartHeadState() }
        )

    private fun createEndHeadAppearanceVisibilityLiveData(): LiveData<CloudItemSelectionState?> =
        selectedOrDefault(
            selectedLiveData = singleLineExtraLiveData.map { it?.toEndHeadState() },
            defaultLiveData = defaultLineExtraLiveData.map { it?.toEndHeadState() }
        )

    private fun LineExtra.toStrokeVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(isStrokeEnabled, userSelectedStrokeStyle.id)

    private fun LineExtra.toStartHeadState(): CloudItemSelectionState =
        CloudItemSelectionState(isEndAnchorEnabled, userSelectedEndAnchor.id)

    private fun LineExtra.toEndHeadState(): CloudItemSelectionState =
        CloudItemSelectionState(isEndAnchorEnabled, userSelectedEndAnchor.id)

    private fun <T> selectedOrDefault(
        selectedLiveData: LiveData<T?>,
        defaultLiveData: LiveData<T?>
    ): LiveData<T?> = combineLiveData(selectedLiveData, defaultLiveData) { s, d -> s ?: d }
}
