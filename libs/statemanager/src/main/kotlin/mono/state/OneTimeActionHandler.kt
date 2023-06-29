/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state

import mono.actionmanager.OneTimeActionType
import mono.bitmap.manager.MonoBitmapManager
import mono.common.exhaustive
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.ShapeExtraManager
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.command.ChangeBound
import mono.shape.command.ChangeExtra
import mono.shape.command.ChangeOrder
import mono.shape.command.MakeTextEditable
import mono.shape.command.UpdateTextEditingMode
import mono.shape.extra.style.RectangleBorderCornerPattern
import mono.shape.extra.style.TextAlign
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import mono.state.command.CommandEnvironment
import mono.state.command.text.EditTextShapeHelper
import mono.state.onetimeaction.AppSettingActionHelper
import mono.state.onetimeaction.FileRelatedActionsHelper
import mono.state.utils.UpdateShapeBoundHelper
import mono.ui.appstate.AppUiStateManager

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

    private val fileRelatedActionsHelper = FileRelatedActionsHelper(
        environment,
        bitmapManager,
        shapeClipboardManager
    )

    private val appSettingActionHelper = AppSettingActionHelper(uiStateManager)

    init {
        oneTimeActionLiveData.observe(lifecycleOwner) {
            when (it) {
                OneTimeActionType.Idle -> Unit

                is OneTimeActionType.ProjectAction ->
                    fileRelatedActionsHelper.handleProjectAction(it)

                is OneTimeActionType.AppSettingAction ->
                    appSettingActionHelper.handleAppSettingAction(it)

                // ---------
                OneTimeActionType.CopyText ->
                    fileRelatedActionsHelper.exportSelectedShapes(false)
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

                is OneTimeActionType.ChangeShapeBorderCornerExtra ->
                    setSelectedShapeBorderCornerExtra(it.isRoundedCorner)

                is OneTimeActionType.ChangeLineStrokeExtra ->
                    setSelectedLineStrokeExtra(it.isEnabled, it.newStrokeStyleId)

                is OneTimeActionType.ChangeLineStrokeDashPatternExtra ->
                    setSelectedLineStrokeDashPattern(it.dash, it.gap, it.offset)

                is OneTimeActionType.ChangeLineStrokeCornerExtra ->
                    setSelectedLineStrokeCornerExtra(it.isRoundedCorner)

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
            environment.shapeManager.shapeConnector.removeShape(shape)
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
        val offset = Point(offsetCol, offsetRow)
        UpdateShapeBoundHelper.moveShapes(
            environment,
            environment.getSelectedShapes(),
            isUpdateConfirmed = true
        ) { it.bound.position + offset }

        environment.updateInteractionBounds()
    }

    private fun setSelectedShapeBound(left: Int?, top: Int?, width: Int?, height: Int?) {
        val singleShape = environment.getSelectedShapes().singleOrNull() ?: return
        val currentBound = singleShape.bound
        val newBound = Rect.byLTWH(
            left = left ?: currentBound.left,
            top = top ?: currentBound.top,
            width = width ?: currentBound.width,
            height = height ?: currentBound.height
        )
        UpdateShapeBoundHelper.updateConnectors(environment, singleShape, newBound, true)
        environment.shapeManager.execute(
            ChangeBound(singleShape, newBound)
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
        val singleShape = environment.getSelectedShapes().singleOrNull()
        if (singleShape == null) {
            val currentDefaultDashPattern = ShapeExtraManager.defaultRectangleExtra.dashPattern
            val newDefaultDashPattern = currentDefaultDashPattern.copy(
                dash = dash ?: currentDefaultDashPattern.dash,
                gap = gap ?: currentDefaultDashPattern.gap,
                offset = offset ?: currentDefaultDashPattern.offset
            )
            ShapeExtraManager.setDefaultValues(borderDashPattern = newDefaultDashPattern)
            return
        }

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

    private fun setSelectedShapeBorderCornerExtra(isRoundedCorner: Boolean) {
        val singleShape = environment.getSelectedShapes().singleOrNull()
        val rectangleExtra = when (singleShape) {
            is Rectangle -> singleShape.extra
            is Text -> singleShape.extra.boundExtra
            is Group,
            is Line,
            is MockShape,
            null -> null
        }
        if (singleShape == null || rectangleExtra == null) {
            ShapeExtraManager.setDefaultValues(isBorderRoundedCorner = isRoundedCorner)
            return
        }
        val newCorner =
            if (isRoundedCorner) {
                RectangleBorderCornerPattern.ENABLED
            } else {
                RectangleBorderCornerPattern.DISABLED
            }
        val newRectangleExtra = rectangleExtra.copy(corner = newCorner)
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
        val line = environment.getSelectedShapes().singleOrNull() as? Line
        if (line == null) {
            val currentDefaultDashPattern = ShapeExtraManager.defaultLineExtra.dashPattern
            val newDefaultDashPattern = currentDefaultDashPattern.copy(
                dash = dash ?: currentDefaultDashPattern.dash,
                gap = gap ?: currentDefaultDashPattern.gap,
                offset = offset ?: currentDefaultDashPattern.offset
            )
            ShapeExtraManager.setDefaultValues(lineDashPattern = newDefaultDashPattern)
            return
        }
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

    private fun setSelectedLineStrokeCornerExtra(isRoundedCorner: Boolean) {
        val line = environment.getSelectedShapes().singleOrNull() as? Line
        if (line == null) {
            ShapeExtraManager.setDefaultValues(isLineStrokeRoundedCorner = isRoundedCorner)
            return
        }
        val currentExtra = line.extra
        val newExtra = currentExtra.copy(isRoundedCorner = isRoundedCorner)

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
