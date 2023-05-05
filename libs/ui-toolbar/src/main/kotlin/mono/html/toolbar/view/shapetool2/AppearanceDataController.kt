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

    val lineStrokeToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createLineStrokeAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val lineStrokeDashPatternLiveData: LiveData<StraightStrokeDashPattern?> =
        createLineStrokeDashPatternLiveData(shapesLiveData)
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
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT ->
                    ShapeExtraManager.defaultRectangleExtra.userSelectedFillStyle

                RetainableActionType.ADD_LINE,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedRectangleFillStyles().indexOf(defaultState)
                CloudItemSelectionState(
                    ShapeExtraManager.defaultRectangleExtra.isFillEnabled,
                    selectedFillPosition
                )
            } else {
                null
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
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT ->
                    ShapeExtraManager.defaultRectangleExtra.userSelectedBorderStyle

                RetainableActionType.ADD_LINE,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(defaultState)
                CloudItemSelectionState(
                    ShapeExtraManager.defaultRectangleExtra.isBorderEnabled,
                    selectedFillPosition
                )
            } else {
                null
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
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_LINE -> ShapeExtraManager.defaultLineExtra.strokeStyle
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(defaultState)
                CloudItemSelectionState(
                    ShapeExtraManager.defaultRectangleExtra.isBorderEnabled,
                    selectedFillPosition
                )
            } else {
                null
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
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_LINE ->
                    ShapeExtraManager.defaultLineExtra.userSelectedStartAnchor

                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedStartHeaderPosition =
                    ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(defaultState)
                CloudItemSelectionState(
                    ShapeExtraManager.defaultLineExtra.isStartAnchorEnabled,
                    selectedStartHeaderPosition
                )
            } else {
                null
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
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_LINE ->
                    ShapeExtraManager.defaultLineExtra.userSelectedEndAnchor

                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(defaultState)
                CloudItemSelectionState(
                    ShapeExtraManager.defaultLineExtra.isEndAnchorEnabled,
                    selectedFillPosition
                )
            } else {
                null
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

    private fun RectangleExtra.toFillAppearanceVisibilityState(): CloudItemSelectionState {
        val selectedFillPosition =
            ShapeExtraManager.getAllPredefinedRectangleFillStyles()
                .indexOf(userSelectedFillStyle)
        return CloudItemSelectionState(isFillEnabled, selectedFillPosition)
    }

    private fun RectangleExtra.toBorderAppearanceVisibilityState(): CloudItemSelectionState {
        val selectedBorderPosition =
            ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(userSelectedBorderStyle)
        return CloudItemSelectionState(isBorderEnabled, selectedBorderPosition)
    }

    private fun LineExtra.toStrokeVisibilityState(): CloudItemSelectionState {
        val selectedStrokePosition =
            ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(userSelectedStrokeStyle)
        return CloudItemSelectionState(isStrokeEnabled, selectedStrokePosition)
    }

    private fun Line.toStartHeadAppearanceVisibilityState(): CloudItemSelectionState {
        val selectedStartHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedStartAnchor)
        return CloudItemSelectionState(extra.isStartAnchorEnabled, selectedStartHeadPosition)
    }

    private fun Line.toEndHeadAppearanceVisibilityState(): CloudItemSelectionState {
        val selectedEndHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedEndAnchor)
        return CloudItemSelectionState(extra.isEndAnchorEnabled, selectedEndHeadPosition)
    }
}

internal data class AppearanceOptionItem(val id: String, val name: String)

internal data class CloudItemSelectionState(val isChecked: Boolean, val selectedPosition: Int)
