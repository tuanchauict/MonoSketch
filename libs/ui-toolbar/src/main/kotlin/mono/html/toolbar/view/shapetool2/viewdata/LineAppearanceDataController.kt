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

    private val defaultLineExtra: LineExtra
        get() = ShapeExtraManager.defaultLineExtra

    private fun createLineStrokeAppearanceVisibilityLiveData(): LiveData<CloudItemSelectionState?> {
        val selectedVisibilityLiveData =
            singleLineExtraLiveData.map { it?.toStrokeVisibilityState() }

        val defaultVisibilityLiveData =
            defaultLineExtraLiveData.map { it?.toStrokeVisibilityState() }

        return selectedOrDefault(selectedVisibilityLiveData, defaultVisibilityLiveData)
    }

    private fun createLineStrokeDashPatternLiveData(): LiveData<StraightStrokeDashPattern?> {
        val selectedDashPatternLiveData: LiveData<StraightStrokeDashPattern?> =
            singleLineExtraLiveData.map { it?.dashPattern }
        val defaultDashPatternLiveData: LiveData<StraightStrokeDashPattern?> =
            defaultLineExtraLiveData.map { it?.dashPattern }
        return selectedOrDefault(selectedDashPatternLiveData, defaultDashPatternLiveData)
    }

    private fun createLineStrokeRoundedCornerLiveData(): LiveData<Boolean?> {
        val selectedCornerPatternLiveData = singleLineExtraLiveData
            .map { it?.takeIf { it.isStrokeEnabled } }
            .map { it?.isRoundedCorner }
        val defaultCornerPatternLiveData = defaultLineExtraLiveData
            .map { it?.takeIf { it.isStrokeEnabled } }
            .map { it?.isRoundedCorner }
        return selectedOrDefault(selectedCornerPatternLiveData, defaultCornerPatternLiveData)
    }

    private fun createStartHeadAppearanceVisibilityLiveData(): LiveData<CloudItemSelectionState?> {
        val selectedLiveData = singleLineExtraLiveData.map {
            it?.let { extra ->
                CloudItemSelectionState(
                    extra.isStartAnchorEnabled,
                    extra.userSelectedStartAnchor.id
                )
            }
        }
        val defaultLiveData = defaultLineExtraLiveData.map {
            it?.let { extra ->
                CloudItemSelectionState(
                    extra.isStartAnchorEnabled,
                    extra.userSelectedStartAnchor.id
                )
            }
        }

        return selectedOrDefault(selectedLiveData, defaultLiveData)
    }

    private fun createEndHeadAppearanceVisibilityLiveData(): LiveData<CloudItemSelectionState?> {
        val selectedLiveData = singleLineExtraLiveData.map {
            it?.let { extra ->
                CloudItemSelectionState(
                    extra.isEndAnchorEnabled,
                    extra.userSelectedEndAnchor.id
                )
            }
        }
        val defaultLiveData = defaultLineExtraLiveData.map {
            it?.let { extra ->
                CloudItemSelectionState(
                    extra.isEndAnchorEnabled,
                    extra.userSelectedEndAnchor.id
                )
            }
        }

        return selectedOrDefault(selectedLiveData, defaultLiveData)
    }

    private fun LineExtra.toStrokeVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(isStrokeEnabled, userSelectedStrokeStyle.id)

    private fun <T> selectedOrDefault(
        selectedLiveData: LiveData<T?>,
        defaultLiveData: LiveData<T?>
    ): LiveData<T?> = combineLiveData(selectedLiveData, defaultLiveData) { s, d -> s ?: d }
}
