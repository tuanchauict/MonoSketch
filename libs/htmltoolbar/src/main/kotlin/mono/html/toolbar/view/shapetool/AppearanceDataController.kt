package mono.html.toolbar.view.shapetool

import mono.html.toolbar.ActionManager
import mono.html.toolbar.RetainableActionType
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.extra.RectangleExtra
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
    actionManager: ActionManager
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

    val fillToolStateLiveData: LiveData<AppearanceSectionViewController.Visibility> =
        createFillAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val borderToolStateLiveData: LiveData<AppearanceSectionViewController.Visibility> =
        createBorderAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val lineStrokeToolStateLiveData: LiveData<AppearanceSectionViewController.Visibility> =
        // TODO: Correct this
        createBorderAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val lineStartHeadToolStateLiveData: LiveData<AppearanceSectionViewController.Visibility> =
        createStartHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)
    val lineEndHeadToolStateLiveData: LiveData<AppearanceSectionViewController.Visibility> =
        createEndHeadAppearanceVisibilityLiveData(shapesLiveData, retainableActionLiveData)

    val hasAnyVisibleToolLiveData: LiveData<Boolean> = combineLiveData(
        fillToolStateLiveData,
        borderToolStateLiveData,
        lineStrokeToolStateLiveData,
        lineStartHeadToolStateLiveData,
        lineEndHeadToolStateLiveData
    ) { list -> list.any { it != AppearanceSectionViewController.Visibility.Hide } }

    private fun createFillAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<AppearanceSectionViewController.Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> AppearanceSectionViewController.Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Rectangle -> shape.extra.toFillAppearanceVisibilityState()
                        is Text -> shape.extra.boundExtra.toFillAppearanceVisibilityState()
                        is Group,
                        is Line,
                        is MockShape -> AppearanceSectionViewController.Visibility.Hide
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
                AppearanceSectionViewController.Visibility.Visible(
                    ShapeExtraManager.defaultRectangleExtra.isFillEnabled,
                    selectedFillPosition
                )
            } else {
                AppearanceSectionViewController.Visibility.Hide
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
    ): LiveData<AppearanceSectionViewController.Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> AppearanceSectionViewController.Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Rectangle -> shape.extra.toBorderAppearanceVisibilityState()
                        is Text -> shape.extra.boundExtra.toBorderAppearanceVisibilityState()
                        is Group,
                        is Line,
                        is MockShape -> AppearanceSectionViewController.Visibility.Hide
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
                AppearanceSectionViewController.Visibility.Visible(
                    ShapeExtraManager.defaultRectangleExtra.isBorderEnabled,
                    selectedFillPosition
                )
            } else {
                AppearanceSectionViewController.Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createStartHeadAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<AppearanceSectionViewController.Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> AppearanceSectionViewController.Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Line -> shape.toStartHeadAppearanceVisibilityState()
                        is Rectangle,
                        is Text,
                        is Group,
                        is MockShape -> AppearanceSectionViewController.Visibility.Hide
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
                AppearanceSectionViewController.Visibility.Visible(
                    ShapeExtraManager.defaultLineExtra.isStartAnchorEnabled,
                    selectedStartHeaderPosition
                )
            } else {
                AppearanceSectionViewController.Visibility.Hide
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
    ): LiveData<AppearanceSectionViewController.Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> AppearanceSectionViewController.Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Line -> shape.toEndHeadAppearanceVisibilityState()
                        is Rectangle,
                        is Text,
                        is Group,
                        is MockShape -> AppearanceSectionViewController.Visibility.Hide
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
                AppearanceSectionViewController.Visibility.Visible(
                    ShapeExtraManager.defaultLineExtra.isEndAnchorEnabled,
                    selectedFillPosition
                )
            } else {
                AppearanceSectionViewController.Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createAppearanceVisibilityLiveData(
        selectedShapeVisibilityLiveData: LiveData<AppearanceSectionViewController.Visibility?>,
        defaultVisibilityLiveData: LiveData<AppearanceSectionViewController.Visibility>
    ): LiveData<AppearanceSectionViewController.Visibility> = combineLiveData(
        selectedShapeVisibilityLiveData,
        defaultVisibilityLiveData
    ) { selected, default -> selected ?: default }

    private fun RectangleExtra.toFillAppearanceVisibilityState(): AppearanceSectionViewController.Visibility {
        val selectedFillPosition =
            ShapeExtraManager.getAllPredefinedRectangleFillStyles()
                .indexOf(userSelectedFillStyle)
        return AppearanceSectionViewController.Visibility.Visible(
            isFillEnabled,
            selectedFillPosition
        )
    }

    private fun RectangleExtra.toBorderAppearanceVisibilityState(): AppearanceSectionViewController.Visibility {
        val selectedBorderPosition =
            ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(userSelectedBorderStyle)
        return AppearanceSectionViewController.Visibility.Visible(
            isBorderEnabled,
            selectedBorderPosition
        )
    }

    private fun Line.toStartHeadAppearanceVisibilityState(): AppearanceSectionViewController.Visibility {
        val selectedStartHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedStartAnchor)
        return AppearanceSectionViewController.Visibility.Visible(
            extra.isStartAnchorEnabled,
            selectedStartHeadPosition
        )
    }

    private fun Line.toEndHeadAppearanceVisibilityState(): AppearanceSectionViewController.Visibility {
        val selectedEndHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedEndAnchor)
        return AppearanceSectionViewController.Visibility.Visible(
            extra.isEndAnchorEnabled,
            selectedEndHeadPosition
        )
    }
}
