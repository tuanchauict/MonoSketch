/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state

import mono.actionmanager.OneTimeActionType
import mono.bitmap.manager.MonoBitmapManager
import mono.common.exhaustive
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.toolbar.view.keyboardshortcut.KeyboardShortcuts
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.ShapeExtraManager
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.command.ChangeBound
import mono.shape.command.ChangeExtra
import mono.shape.command.ChangeOrder
import mono.shape.command.MakeTextEditable
import mono.shape.command.UpdateTextEditingMode
import mono.shape.extra.style.TextAlign
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import mono.state.command.CommandEnvironment
import mono.state.command.text.EditTextShapeHelper
import mono.state.onetimeaction.FileRelatedActionsHelper
import mono.ui.appstate.AppUiStateManager
import mono.ui.appstate.AppUiStateManager.UiStatePayload

/**
 * A class to handle one time actions.
 */
internal class OneTimeActionHandler(
    lifecycleOwner: LifecycleOwner,
    oneTimeActionLiveData: LiveData<OneTimeActionType>,
    private val environment: CommandEnvironment,
    bitmapManager: MonoBitmapManager,
    shapeClipboardManager: ShapeClipboardManager,
    private val stateHistoryManager: StateHistoryManager,
    uiStateManager: AppUiStateManager
) {
    private val clipboardManager: ClipboardManager =
        ClipboardManager(lifecycleOwner, environment, shapeClipboardManager)

    private val fileRelatedActions = FileRelatedActionsHelper(
        environment,
        stateHistoryManager,
        bitmapManager,
        shapeClipboardManager
    )

    init {
        oneTimeActionLiveData.observe(lifecycleOwner) {
            when (it) {
                OneTimeActionType.Idle -> Unit

                // File drop down menu
                OneTimeActionType.NewProject -> fileRelatedActions.newProject()

                is OneTimeActionType.RenameCurrentProject ->
                    fileRelatedActions.renameProject(it.newName)

                OneTimeActionType.SaveShapesAs -> fileRelatedActions.saveCurrentShapesToFile()

                OneTimeActionType.OpenShapes -> fileRelatedActions.loadShapesFromFile()

                OneTimeActionType.ExportSelectedShapes ->
                    fileRelatedActions.exportSelectedShapes(true)

                // Main drop down menu
                OneTimeActionType.ShowFormatPanel ->
                    uiStateManager.updateUiState(UiStatePayload.ShapeToolVisibility(true))

                OneTimeActionType.HideFormatPanel ->
                    uiStateManager.updateUiState(UiStatePayload.ShapeToolVisibility(false))

                OneTimeActionType.ShowKeyboardShortcuts ->
                    KeyboardShortcuts.showHint()

                // ---------
                OneTimeActionType.CopyText ->
                    fileRelatedActions.exportSelectedShapes(false)
                // ---------
                OneTimeActionType.SelectAllShapes ->
                    environment.selectAllShapes()

                OneTimeActionType.DeselectShapes ->
                    environment.clearSelectedShapes()

                OneTimeActionType.DeleteSelectedShapes ->
                    deleteSelectedShapes()

                OneTimeActionType.EditSelectedShapes ->
                    editSelectedShape(environment.getSelectedShapes().singleOrNull())

                is OneTimeActionType.EditSelectedShape ->
                    editSelectedShape(it.shape)

                is OneTimeActionType.TextAlignment ->
                    setTextAlignment(it.newHorizontalAlign, it.newVerticalAlign)
                // ---------
                is OneTimeActionType.MoveShapes ->
                    moveSelectedShapes(it.offsetRow, it.offsetCol)

                is OneTimeActionType.ChangeShapeBound ->
                    setSelectedShapeBound(it.newLeft, it.newTop, it.newWidth, it.newHeight)
                // ---------
                is OneTimeActionType.ChangeShapeFillExtra ->
                    setSelectedShapeFillExtra(it.isEnabled, it.newFillStyleId)

                is OneTimeActionType.ChangeShapeBorderExtra ->
                    setSelectedShapeBorderExtra(it.isEnabled, it.newBorderStyleId)

                is OneTimeActionType.ChangeShapeBorderDashPatternExtra ->
                    setSelectedShapeBorderDashPatternExtra(it.dash, it.gap, it.offset)

                is OneTimeActionType.ChangeLineStrokeExtra ->
                    setSelectedLineStrokeExtra(it.isEnabled, it.newStrokeStyleId)

                is OneTimeActionType.ChangeLineStrokeDashPatternExtra ->
                    setSelectedLineStrokeDashPattern(it.dash, it.gap, it.offset)

                is OneTimeActionType.ChangeLineStartAnchorExtra ->
                    setSelectedShapeStartAnchorExtra(it.isEnabled, it.newHeadId)

                is OneTimeActionType.ChangeLineEndAnchorExtra ->
                    setSelectedShapeEndAnchorExtra(it.isEnabled, it.newHeadId)
                // ---------
                is OneTimeActionType.ReorderShape ->
                    changeShapeOrder(it.orderType)
                // ---------
                is OneTimeActionType.Copy ->
                    clipboardManager.copySelectedShapes(it.isRemoveRequired)

                OneTimeActionType.Duplicate ->
                    clipboardManager.duplicateSelectedShapes()
                // ---------
                OneTimeActionType.Undo ->
                    stateHistoryManager.undo()

                OneTimeActionType.Redo ->
                    stateHistoryManager.redo()
            }.exhaustive
        }
    }

    private fun deleteSelectedShapes() {
        for (shape in environment.getSelectedShapes()) {
            environment.removeShape(shape)
        }
        environment.clearSelectedShapes()
    }

    private fun editSelectedShape(shape: AbstractShape?) {
        when (shape) {
            is Text -> {
                environment.enterEditingMode()
                val oldText = shape.text
                environment.shapeManager.execute(MakeTextEditable(shape))
                environment.shapeManager.execute(UpdateTextEditingMode(shape, true))
                EditTextShapeHelper.showEditTextDialog(environment, shape, false) {
                    environment.shapeManager.execute(UpdateTextEditingMode(shape, false))
                    environment.exitEditingMode(oldText != shape.text)
                }
            }

            is Line,
            is Rectangle,
            is MockShape,
            is Group,
            null -> Unit
        }.exhaustive
    }

    private fun setTextAlignment(
        horizontalAlign: TextAlign.HorizontalAlign?,
        verticalAlign: TextAlign.VerticalAlign?
    ) {
        val textShape = environment.getSelectedShapes().singleOrNull() as? Text
        if (textShape == null) {
            ShapeExtraManager.setDefaultValues(
                textHorizontalAlign = horizontalAlign,
                textVerticalAlign = verticalAlign
            )
            return
        }
        val newTextAlign = textShape.extra.textAlign.copy(
            horizontalAlign ?: textShape.extra.textAlign.horizontalAlign,
            verticalAlign ?: textShape.extra.textAlign.verticalAlign
        )
        val newExtra = textShape.extra.copy(textAlign = newTextAlign)
        environment.shapeManager.execute(ChangeExtra(textShape, newExtra))
    }

    private fun moveSelectedShapes(offsetRow: Int, offsetCol: Int) {
        val selectedShapes = environment.getSelectedShapes()
        for (shape in selectedShapes) {
            val bound = shape.bound
            val newPosition = Point(bound.left + offsetCol, bound.top + offsetRow)
            val newBound = shape.bound.copy(position = newPosition)
            environment.shapeManager.execute(ChangeBound(shape, newBound))
        }
        environment.updateInteractionBounds()
    }

    private fun setSelectedShapeBound(left: Int?, top: Int?, width: Int?, height: Int?) {
        val singleShape = environment.getSelectedShapes().singleOrNull() ?: return
        val currentBound = singleShape.bound
        val newLeft = left ?: currentBound.left
        val newTop = top ?: currentBound.top
        val newWidth = width ?: currentBound.width
        val newHeight = height ?: currentBound.height
        environment.shapeManager.execute(
            ChangeBound(singleShape, Rect.byLTWH(newLeft, newTop, newWidth, newHeight))
        )
        environment.updateInteractionBounds()
    }

    private fun setSelectedShapeFillExtra(isEnabled: Boolean?, newFillStyleId: String?) {
        val singleShape = environment.getSelectedShapes().singleOrNull()

        if (singleShape == null) {
            ShapeExtraManager.setDefaultValues(
                isFillEnabled = isEnabled,
                fillStyleId = newFillStyleId
            )
            return
        }

        val currentRectangleExtra = when (singleShape) {
            is Rectangle -> singleShape.extra
            is Text -> singleShape.extra.boundExtra
            is Line,
            is MockShape,
            is Group -> null
        } ?: return
        val newIsFillEnabled = isEnabled ?: currentRectangleExtra.isFillEnabled
        val newFillStyle = ShapeExtraManager.getRectangleFillStyle(
            newFillStyleId,
            currentRectangleExtra.userSelectedFillStyle
        )
        val rectangleExtra = currentRectangleExtra.copy(
            isFillEnabled = newIsFillEnabled,
            userSelectedFillStyle = newFillStyle
        )
        val newExtra = when (singleShape) {
            is Rectangle -> rectangleExtra
            is Text -> singleShape.extra.copy(boundExtra = rectangleExtra)
            is Line,
            is MockShape,
            is Group -> null
        } ?: return
        environment.shapeManager.execute(ChangeExtra(singleShape, newExtra))
    }

    private fun setSelectedShapeBorderExtra(isEnabled: Boolean?, newBorderStyleId: String?) {
        val singleShape = environment.getSelectedShapes().singleOrNull()
        if (singleShape == null) {
            ShapeExtraManager.setDefaultValues(
                isBorderEnabled = isEnabled,
                borderStyleId = newBorderStyleId
            )
            return
        }

        val currentRectangleExtra = when (singleShape) {
            is Rectangle -> singleShape.extra
            is Text -> singleShape.extra.boundExtra
            is Line,
            is MockShape,
            is Group -> null
        } ?: return
        val newIsBorderEnabled = isEnabled ?: currentRectangleExtra.isBorderEnabled
        val newBorderStyle = ShapeExtraManager.getRectangleBorderStyle(
            newBorderStyleId,
            currentRectangleExtra.userSelectedBorderStyle
        )
        val rectangleExtra = currentRectangleExtra.copy(
            isBorderEnabled = newIsBorderEnabled,
            userSelectedBorderStyle = newBorderStyle
        )
        val newExtra = when (singleShape) {
            is Rectangle -> rectangleExtra
            is Text -> singleShape.extra.copy(boundExtra = rectangleExtra)
            is Line,
            is MockShape,
            is Group -> null
        } ?: return
        environment.shapeManager.execute(ChangeExtra(singleShape, newExtra))
    }

    private fun setSelectedShapeBorderDashPatternExtra(dash: Int?, gap: Int?, offset: Int?) {
        val singleShape = environment.getSelectedShapes().singleOrNull() ?: return
        val rectangleExtra = when (singleShape) {
            is Rectangle -> singleShape.extra
            is Text -> singleShape.extra.boundExtra
            is Group,
            is Line,
            is MockShape -> null
        }
        val currentPattern = rectangleExtra?.dashPattern ?: return
        val newPattern = currentPattern.copy(
            dash = dash ?: currentPattern.dash,
            gap = gap ?: currentPattern.gap,
            offset = offset ?: currentPattern.offset
        )
        val newRectangleExtra = rectangleExtra.copy(dashPattern = newPattern)
        val newExtra = when (singleShape) {
            is Rectangle -> newRectangleExtra
            is Text -> singleShape.extra.copy(boundExtra = newRectangleExtra)
            is Group,
            is Line,
            is MockShape -> null
        } ?: return
        environment.shapeManager.execute(ChangeExtra(singleShape, newExtra))
    }

    private fun setSelectedLineStrokeExtra(isEnabled: Boolean?, newStrokeStyleId: String?) {
        val line = environment.getSelectedShapes().singleOrNull() as? Line
        if (line == null) {
            ShapeExtraManager.setDefaultValues(
                isLineStrokeEnabled = isEnabled,
                lineStrokeStyleId = newStrokeStyleId
            )
            return
        }

        val currentLineExtra = line.extra
        val newIsEnabled = isEnabled ?: currentLineExtra.isStrokeEnabled
        val newStrokeStyle = ShapeExtraManager.getLineStrokeStyle(
            newStrokeStyleId,
            currentLineExtra.userSelectedStrokeStyle
        )
        val newExtra = currentLineExtra.copy(
            isStrokeEnabled = newIsEnabled,
            userSelectedStrokeStyle = newStrokeStyle
        )
        environment.shapeManager.execute(ChangeExtra(line, newExtra))
    }

    private fun setSelectedLineStrokeDashPattern(dash: Int?, gap: Int?, offset: Int?) {
        val line = environment.getSelectedShapes().singleOrNull() as? Line ?: return
        val currentExtra = line.extra
        val currentPattern = currentExtra.dashPattern
        val newPattern = currentPattern.copy(
            dash = dash ?: currentPattern.dash,
            gap = gap ?: currentPattern.gap,
            offset = offset ?: currentPattern.offset
        )
        val newExtra = currentExtra.copy(dashPattern = newPattern)
        environment.shapeManager.execute(ChangeExtra(line, newExtra))
    }

    private fun setSelectedShapeStartAnchorExtra(isEnabled: Boolean?, newAnchorId: String?) {
        val line = environment.getSelectedShapes().singleOrNull() as? Line
        if (line == null) {
            ShapeExtraManager.setDefaultValues(
                isStartHeadAnchorCharEnabled = isEnabled,
                startHeadAnchorCharId = newAnchorId
            )
            return
        }

        val currentExtra = line.extra
        val newIsEnabled = isEnabled ?: currentExtra.isStartAnchorEnabled
        val newAnchor =
            ShapeExtraManager.getStartHeadAnchorChar(
                newAnchorId,
                currentExtra.userSelectedStartAnchor
            )
        val newExtra = currentExtra.copy(
            isStartAnchorEnabled = newIsEnabled,
            userSelectedStartAnchor = newAnchor
        )
        environment.shapeManager.execute(ChangeExtra(line, newExtra))
    }

    private fun setSelectedShapeEndAnchorExtra(isEnabled: Boolean?, newAnchorId: String?) {
        val line = environment.getSelectedShapes().singleOrNull() as? Line
        if (line == null) {
            ShapeExtraManager.setDefaultValues(
                isEndHeadAnchorCharEnabled = isEnabled,
                endHeadAnchorCharId = newAnchorId
            )
            return
        }
        val currentExtra = line.extra
        val newIsEnabled = isEnabled ?: currentExtra.isEndAnchorEnabled
        val newAnchor =
            ShapeExtraManager.getEndHeadAnchorChar(
                newAnchorId,
                currentExtra.userSelectedEndAnchor
            )
        val newExtra = currentExtra.copy(
            isEndAnchorEnabled = newIsEnabled,
            userSelectedEndAnchor = newAnchor
        )
        environment.shapeManager.execute(ChangeExtra(line, newExtra))
    }

    private fun changeShapeOrder(orderType: ChangeOrder.ChangeOrderType) {
        val singleShape = environment.getSelectedShapes().singleOrNull() ?: return
        environment.shapeManager.execute(ChangeOrder(singleShape, orderType))
    }
}
