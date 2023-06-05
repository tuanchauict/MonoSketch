/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2

import mono.actionmanager.RetainableActionType
import mono.common.nullToFalse
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.extra.LineExtra
import mono.shape.extra.RectangleExtra
import mono.shape.extra.style.StraightStrokeDashPattern
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text

/**
 * Data controller class of appearance section
 */
internal class AppearanceDataController(
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
    shapeManagerVersionLiveData: LiveData<Int>,
    retainableActionLiveData: LiveData<RetainableActionType>
) {
    private val shapesLiveData: LiveData<Set<AbstractShape>> =
        combineLiveData(
            selectedShapesLiveData,
            shapeManagerVersionLiveData
        ) { selected, _ -> selected }

    val fillToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createFillAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val borderToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createBorderAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val borderDashPatternLiveData: LiveData<StraightStrokeDashPattern?> =
        createBorderDashPatternLiveData(shapesLiveData)
    val borderRoundedCornerLiveData: LiveData<Boolean> =
        createBorderRoundedCornerLiveData(shapesLiveData)

    val lineStrokeToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createLineStrokeAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val lineStrokeDashPatternLiveData: LiveData<StraightStrokeDashPattern?> =
        createLineStrokeDashPatternLiveData(shapesLiveData)
    val lineStrokeRoundedCornerLiveData: LiveData<Boolean> =
        createLineStrokeRoundedCornerLiveData(shapesLiveData)
    val lineStartHeadToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createStartHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val lineEndHeadToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createEndHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)

    val hasAnyVisibleToolLiveData: LiveData<Boolean> = combineLiveData(
        fillToolStateLiveData,
        borderToolStateLiveData,
        lineStrokeToolStateLiveData,
        lineStartHeadToolStateLiveData,
        lineEndHeadToolStateLiveData
    ) { list -> list.any { it != null } }

    val fillOptions: List<AppearanceOptionItem> =
        ShapeExtraManager.getAllPredefinedRectangleFillStyles()
            .map { AppearanceOptionItem(it.id, it.displayName) }

    val strokeOptions: List<AppearanceOptionItem> =
        ShapeExtraManager.getAllPredefinedStrokeStyles()
            .map { AppearanceOptionItem(it.id, it.displayName) }

    val headOptions: List<AppearanceOptionItem> =
        ShapeExtraManager.getAllPredefinedAnchorChars()
            .map { AppearanceOptionItem(it.id, it.displayName) }

    private val defaultRectangleExtra: RectangleExtra
        get() = ShapeExtraManager.defaultRectangleExtra

    private val defaultLineExtra: LineExtra
        get() = ShapeExtraManager.defaultLineExtra

    private fun createFillAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<CloudItemSelectionState?> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when (val shape = it.singleOrNull()) {
                is Rectangle -> shape.extra.toFillAppearanceVisibilityState()
                is Text -> shape.extra.boundExtra.toFillAppearanceVisibilityState()
                null,
                is Group,
                is Line,
                is MockShape -> null
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map { type ->
            val defaultState = when (type) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT ->
                    defaultRectangleExtra.userSelectedFillStyle

                RetainableActionType.ADD_LINE,
                RetainableActionType.IDLE -> null
            }
            defaultState?.let {
                CloudItemSelectionState(defaultRectangleExtra.isFillEnabled, it.id)
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createBorderAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<CloudItemSelectionState?> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when (val shape = it.singleOrNull()) {
                is Rectangle -> shape.extra.toBorderAppearanceVisibilityState()
                is Text -> shape.extra.boundExtra.toBorderAppearanceVisibilityState()
                null,
                is Group,
                is Line,
                is MockShape -> null
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map { type ->
            val defaultState = when (type) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT ->
                    defaultRectangleExtra.userSelectedBorderStyle

                RetainableActionType.ADD_LINE,
                RetainableActionType.IDLE -> null
            }
            defaultState?.let {
                CloudItemSelectionState(defaultRectangleExtra.isBorderEnabled, it.id)
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createBorderDashPatternLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>
    ): LiveData<StraightStrokeDashPattern?> = selectedShapesLiveData.map {
        val boundExtra = when (val shape = it.singleOrNull()) {
            is Text -> shape.extra.boundExtra
            is Rectangle -> shape.extra
            is Line,
            is Group,
            is MockShape,
            null -> null
        }
        boundExtra?.dashPattern.takeIf { boundExtra?.isBorderEnabled.nullToFalse() }
    }

    private fun createBorderRoundedCornerLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>
    ): LiveData<Boolean> = selectedShapesLiveData.map {
        when (val shape = it.singleOrNull()) {
            is Rectangle -> shape.extra.isRoundedCorner
            is Text -> shape.extra.boundExtra.isRoundedCorner
            is Group,
            is Line,
            is MockShape,
            null -> false
        }
    }

    private fun createLineStrokeAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<CloudItemSelectionState?> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when (val shape = it.singleOrNull()) {
                is Line -> shape.extra.toStrokeVisibilityState()
                null,
                is Rectangle,
                is Text,
                is Group,
                is MockShape -> null
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map { type ->
            val defaultState = when (type) {
                RetainableActionType.ADD_LINE -> defaultLineExtra.strokeStyle
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            defaultState?.let {
                CloudItemSelectionState(defaultRectangleExtra.isBorderEnabled, it.id)
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createLineStrokeDashPatternLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>
    ): LiveData<StraightStrokeDashPattern?> = selectedShapesLiveData.map {
        val extra = when (val shape = it.singleOrNull()) {
            is Line -> shape.extra
            is Group,
            is Text,
            is Rectangle,
            is MockShape,
            null -> null
        }
        extra?.dashPattern.takeIf { extra?.isStrokeEnabled.nullToFalse() }
    }

    private fun createLineStrokeRoundedCornerLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>
    ): LiveData<Boolean> = selectedShapesLiveData.map {
        when (val shape = it.singleOrNull()) {
            is Line -> shape.extra.isRoundedCorner
            is Rectangle,
            is Text,
            is Group,
            is MockShape,
            null -> false
        }
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

    private fun createAppearanceVisibilityLiveData(
        selectedShapeVisibilityLiveData: LiveData<CloudItemSelectionState?>,
        defaultVisibilityLiveData: LiveData<CloudItemSelectionState?>
    ): LiveData<CloudItemSelectionState?> = combineLiveData(
        selectedShapeVisibilityLiveData,
        defaultVisibilityLiveData
    ) { selected, default -> selected ?: default }

    private fun RectangleExtra.toFillAppearanceVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(isFillEnabled, userSelectedFillStyle.id)

    private fun RectangleExtra.toBorderAppearanceVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(isBorderEnabled, userSelectedBorderStyle.id)

    private fun LineExtra.toStrokeVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(isStrokeEnabled, userSelectedStrokeStyle.id)

    private fun Line.toStartHeadAppearanceVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(extra.isStartAnchorEnabled, extra.userSelectedStartAnchor.id)

    private fun Line.toEndHeadAppearanceVisibilityState(): CloudItemSelectionState =
        CloudItemSelectionState(extra.isEndAnchorEnabled, extra.userSelectedEndAnchor.id)
}

internal data class AppearanceOptionItem(val id: String, val name: String)

internal data class CloudItemSelectionState(val isChecked: Boolean, val selectedId: String?)
