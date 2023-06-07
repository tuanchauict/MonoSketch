/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2.viewdata

import mono.actionmanager.RetainableActionType
import mono.common.nullToFalse
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.extra.RectangleExtra
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
internal class RectangleAppearanceDataController(
    shapesLiveData: LiveData<Set<AbstractShape>>,
    retainableActionLiveData: LiveData<RetainableActionType>
) {

    private val defaultRectangleExtraLiveData: LiveData<RectangleExtra?> =
        retainableActionLiveData.map {
            when (it) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT -> ShapeExtraManager.defaultRectangleExtra

                RetainableActionType.IDLE,
                RetainableActionType.ADD_LINE -> null
            }
        }

    val fillToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createFillAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val borderToolStateLiveData: LiveData<CloudItemSelectionState?> =
        createBorderAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val borderDashPatternLiveData: LiveData<StraightStrokeDashPattern?> =
        createBorderDashPatternLiveData(shapesLiveData)
    val borderRoundedCornerLiveData: LiveData<Boolean?> =
        createBorderRoundedCornerLiveData(shapesLiveData, retainableActionLiveData)

    val hasAnyVisibleToolLiveData: LiveData<Boolean> = combineLiveData(
        fillToolStateLiveData,
        borderToolStateLiveData,
        borderDashPatternLiveData,
        borderRoundedCornerLiveData
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
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<Boolean?> {
        val selectedShapeCornerLiveData = selectedShapesLiveData.map {
            when (val shape = it.singleOrNull()) {
                is Rectangle -> shape.extra.isRoundedCorner
                is Text -> shape.extra.boundExtra.isRoundedCorner
                is Group,
                is Line,
                is MockShape,
                null -> null
            }
        }

        val defaultCornerPatternLiveData = retainableActionTypeLiveData.map { type ->
            when (type) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT -> defaultRectangleExtra.isRoundedCorner

                RetainableActionType.IDLE,
                RetainableActionType.ADD_LINE -> null
            }
        }
        return combineLiveData(
            selectedShapeCornerLiveData,
            defaultCornerPatternLiveData
        ) { selected, default -> selected ?: default }
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
}

internal data class AppearanceOptionItem(val id: String, val name: String)

internal data class CloudItemSelectionState(val isChecked: Boolean, val selectedId: String?)

private fun LiveData<Set<AbstractShape>>.singleRectangleExtraLiveData(): LiveData<RectangleExtra?> =
    map {
        when (val shape = it.singleOrNull()) {
            is Rectangle -> shape.extra
            is Text -> shape.extra.boundExtra
            is Group,
            is Line,
            is MockShape,
            null -> null
        }
    }
