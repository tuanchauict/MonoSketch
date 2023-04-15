/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool

import mono.actionmanager.ActionManager
import mono.actionmanager.OneTimeActionType
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
    private val actionManager: ActionManager
) {
    private val shapesLiveData: LiveData<Set<AbstractShape>> =
        combineLiveData(
            selectedShapesLiveData,
            shapeManagerVersionLiveData
        ) { selected, _ -> selected }

    private val retainableActionLiveData: LiveData<RetainableActionType> =
        combineLiveData(
            actionManager.retainableActionLiveData,
            ShapeExtraManager.defaultExtraStateUpdateLiveData
        ) { action, _ -> action }

    val fillToolStateLiveData: LiveData<AppearanceVisibility> =
        createFillAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val borderToolStateLiveData: LiveData<AppearanceVisibility> =
        createBorderAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val borderDashPatternLiveData: LiveData<AppearanceVisibility> =
        createBorderDashPatternLiveData(shapesLiveData)

    val lineStrokeToolStateLiveData: LiveData<AppearanceVisibility> =
        createLineStrokeAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val lineStrokeDashPatternLiveData: LiveData<AppearanceVisibility> =
        createLineStrokeDashPatternLiveData(shapesLiveData)
    val lineStartHeadToolStateLiveData: LiveData<AppearanceVisibility> =
        createStartHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val lineEndHeadToolStateLiveData: LiveData<AppearanceVisibility> =
        createEndHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)

    val hasAnyVisibleToolLiveData: LiveData<Boolean> = combineLiveData(
        fillToolStateLiveData,
        borderToolStateLiveData,
        lineStrokeToolStateLiveData,
        lineStartHeadToolStateLiveData,
        lineEndHeadToolStateLiveData
    ) { list -> list.any { it != AppearanceVisibility.Hide } }

    val fillOptions: List<AppearanceOptionItem> =
        ShapeExtraManager.getAllPredefinedRectangleFillStyles()
            .map { AppearanceOptionItem(it.id, it.displayName) }

    val strokeOptions: List<AppearanceOptionItem> =
        ShapeExtraManager.getAllPredefinedStrokeStyles()
            .map { AppearanceOptionItem(it.id, it.displayName) }

    val headOptions: List<AppearanceOptionItem> =
        ShapeExtraManager.getAllPredefinedAnchorChars()
            .map { AppearanceOptionItem(it.id, it.displayName) }

    fun setOneTimeAction(actionType: OneTimeActionType) = actionManager.setOneTimeAction(actionType)

    private fun createFillAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<AppearanceVisibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> AppearanceVisibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Rectangle -> shape.extra.toFillAppearanceVisibilityState()
                        is Text -> shape.extra.boundExtra.toFillAppearanceVisibilityState()
                        is Group,
                        is Line,
                        is MockShape -> AppearanceVisibility.Hide
                    }
                }
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
                AppearanceVisibility.GridVisible(
                    ShapeExtraManager.defaultRectangleExtra.isFillEnabled,
                    selectedFillPosition
                )
            } else {
                AppearanceVisibility.Hide
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
    ): LiveData<AppearanceVisibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> AppearanceVisibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Rectangle -> shape.extra.toBorderAppearanceVisibilityState()
                        is Text -> shape.extra.boundExtra.toBorderAppearanceVisibilityState()
                        is Group,
                        is Line,
                        is MockShape -> AppearanceVisibility.Hide
                    }
                }
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
                AppearanceVisibility.GridVisible(
                    ShapeExtraManager.defaultRectangleExtra.isBorderEnabled,
                    selectedFillPosition
                )
            } else {
                AppearanceVisibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createBorderDashPatternLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>
    ): LiveData<AppearanceVisibility> = selectedShapesLiveData.map {
        val boundExtra = when (val shape = it.singleOrNull()) {
            is Text -> shape.extra.boundExtra
            is Rectangle -> shape.extra
            is Line,
            is Group,
            is MockShape,
            null -> null
        }
        val dashPattern =
            boundExtra?.dashPattern.takeIf { boundExtra?.isBorderEnabled.nullToFalse() }
        dashPattern?.let(AppearanceVisibility::DashVisible) ?: AppearanceVisibility.Hide
    }

    private fun createLineStrokeAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<AppearanceVisibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> AppearanceVisibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Line -> shape.extra.toStrokeVisibilityState()
                        is Rectangle,
                        is Text,
                        is Group,
                        is MockShape -> AppearanceVisibility.Hide
                    }
                }
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
                AppearanceVisibility.GridVisible(
                    ShapeExtraManager.defaultRectangleExtra.isBorderEnabled,
                    selectedFillPosition
                )
            } else {
                AppearanceVisibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createLineStrokeDashPatternLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>
    ): LiveData<AppearanceVisibility> = selectedShapesLiveData.map {
        val extra = when (val shape = it.singleOrNull()) {
            is Line -> shape.extra
            is Group,
            is Text,
            is Rectangle,
            is MockShape,
            null -> null
        }
        val dashPattern = extra?.dashPattern.takeIf { extra?.isStrokeEnabled.nullToFalse() }
        dashPattern?.let(AppearanceVisibility::DashVisible) ?: AppearanceVisibility.Hide
    }

    private fun createStartHeadAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<AppearanceVisibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> AppearanceVisibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Line -> shape.toStartHeadAppearanceVisibilityState()
                        is Rectangle,
                        is Text,
                        is Group,
                        is MockShape -> AppearanceVisibility.Hide
                    }
                }
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
                AppearanceVisibility.GridVisible(
                    ShapeExtraManager.defaultLineExtra.isStartAnchorEnabled,
                    selectedStartHeaderPosition
                )
            } else {
                AppearanceVisibility.Hide
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
    ): LiveData<AppearanceVisibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> AppearanceVisibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Line -> shape.toEndHeadAppearanceVisibilityState()
                        is Rectangle,
                        is Text,
                        is Group,
                        is MockShape -> AppearanceVisibility.Hide
                    }
                }
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
                AppearanceVisibility.GridVisible(
                    ShapeExtraManager.defaultLineExtra.isEndAnchorEnabled,
                    selectedFillPosition
                )
            } else {
                AppearanceVisibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createAppearanceVisibilityLiveData(
        selectedShapeVisibilityLiveData: LiveData<AppearanceVisibility?>,
        defaultVisibilityLiveData: LiveData<AppearanceVisibility>
    ): LiveData<AppearanceVisibility> = combineLiveData(
        selectedShapeVisibilityLiveData,
        defaultVisibilityLiveData
    ) { selected, default -> selected ?: default }

    private fun RectangleExtra.toFillAppearanceVisibilityState(): AppearanceVisibility {
        val selectedFillPosition =
            ShapeExtraManager.getAllPredefinedRectangleFillStyles()
                .indexOf(userSelectedFillStyle)
        return AppearanceVisibility.GridVisible(isFillEnabled, selectedFillPosition)
    }

    private fun RectangleExtra.toBorderAppearanceVisibilityState(): AppearanceVisibility {
        val selectedBorderPosition =
            ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(userSelectedBorderStyle)
        return AppearanceVisibility.GridVisible(isBorderEnabled, selectedBorderPosition)
    }

    private fun LineExtra.toStrokeVisibilityState(): AppearanceVisibility {
        val selectedStrokePosition =
            ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(userSelectedStrokeStyle)
        return AppearanceVisibility.GridVisible(isStrokeEnabled, selectedStrokePosition)
    }

    private fun Line.toStartHeadAppearanceVisibilityState(): AppearanceVisibility {
        val selectedStartHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedStartAnchor)
        return AppearanceVisibility.GridVisible(
            extra.isStartAnchorEnabled,
            selectedStartHeadPosition
        )
    }

    private fun Line.toEndHeadAppearanceVisibilityState(): AppearanceVisibility {
        val selectedEndHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedEndAnchor)
        return AppearanceVisibility.GridVisible(extra.isEndAnchorEnabled, selectedEndHeadPosition)
    }
}

internal data class AppearanceOptionItem(val id: String, val name: String)

internal sealed class AppearanceVisibility {
    object Hide : AppearanceVisibility()

    data class GridVisible(
        val isChecked: Boolean,
        val selectedPosition: Int
    ) : AppearanceVisibility()

    data class DashVisible(
        val dashPattern: StraightStrokeDashPattern
    ) : AppearanceVisibility()
}
