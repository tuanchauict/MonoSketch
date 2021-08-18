package mono.state

import mono.bitmap.manager.MonoBitmapManager
import mono.common.exhaustive
import mono.export.ExportShapesHelper
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.toolbar.OneTimeActionType
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.command.ChangeBound
import mono.shape.command.ChangeExtra
import mono.shape.command.ChangeOrder
import mono.shape.extra.manager.ShapeExtraManager
import mono.shape.extra.manager.model.TextAlign
import mono.shape.remove
import mono.shape.serialization.SerializableGroup
import mono.shape.serialization.ShapeSerializationUtil
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import mono.state.command.CommandEnvironment
import mono.state.command.text.EditTextShapeHelper

/**
 * A class to handle one time actions.
 */
internal class OneTimeActionHandler(
    lifecycleOwner: LifecycleOwner,
    oneTimeActionLiveData: LiveData<OneTimeActionType>,
    private val environment: CommandEnvironment,
    bitmapManager: MonoBitmapManager,
    shapeClipboardManager: ShapeClipboardManager,
) {
    private val fileMediator: FileMediator = FileMediator()
    private val exportShapesHelper = ExportShapesHelper(
        bitmapManager::getBitmap,
        shapeClipboardManager::setClipboardText
    )

    private val clipboardManager: ClipboardManager =
        ClipboardManager(lifecycleOwner, environment, shapeClipboardManager)

    init {
        oneTimeActionLiveData.observe(lifecycleOwner) {
            when (it) {
                OneTimeActionType.Idle -> Unit

                OneTimeActionType.SaveShapesAs ->
                    saveCurrentShapesToFile()
                OneTimeActionType.OpenShapes ->
                    loadShapesFromFile()
                OneTimeActionType.ExportSelectedShapes ->
                    exportSelectedShapes(true)

                OneTimeActionType.CopyText ->
                    exportSelectedShapes(false)

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

                is OneTimeActionType.MoveShapes ->
                    moveSelectedShapes(it.offsetRow, it.offsetCol)
                is OneTimeActionType.ChangeShapeBound ->
                    setSelectedShapeBound(it.newLeft, it.newTop, it.newWidth, it.newHeight)

                is OneTimeActionType.ChangeShapeFillExtra ->
                    setSelectedShapeFillExtra(it.isEnabled, it.newFillStyleId)
                is OneTimeActionType.ChangeShapeBorderExtra ->
                    setSelectedShapeBorderExtra(it.isEnabled, it.newBorderStyleId)
                is OneTimeActionType.ChangeLineStartAnchorExtra ->
                    setSelectedShapeStartAnchorExtra(it.isEnabled, it.newHeadId)
                is OneTimeActionType.ChangeLineEndAnchorExtra ->
                    setSelectedShapeEndAnchorExtra(it.isEnabled, it.newHeadId)

                is OneTimeActionType.ReorderShape ->
                    changeShapeOrder(it.orderType)

                is OneTimeActionType.Copy ->
                    clipboardManager.copySelectedShapes(it.isRemoveRequired)
                OneTimeActionType.Duplicate ->
                    clipboardManager.duplicateSelectedShapes()

                OneTimeActionType.Undo ->
                    TODO()
                OneTimeActionType.Redo ->
                    TODO()
            }.exhaustive
        }
    }

    private fun saveCurrentShapesToFile() {
        val serializableRoot = environment.shapeManager.root.toSerializableShape(true)
        fileMediator.saveFile(ShapeSerializationUtil.toJson(serializableRoot))
    }

    private fun loadShapesFromFile() {
        fileMediator.openFile { jsonString ->
            val serializableRoot = ShapeSerializationUtil.fromJson(jsonString) as? SerializableGroup
            if (serializableRoot != null) {
                environment.replaceRoot(Group(serializableRoot, parentId = null))
            }
        }
    }

    private fun exportSelectedShapes(isModalRequired: Boolean) {
        val selectedShapes = environment.getSelectedShapes()
        val extractableShapes = when {
            selectedShapes.isNotEmpty() ->
                environment.workingParentGroup.items.filter { it in selectedShapes }
            isModalRequired ->
                listOf(environment.workingParentGroup)
            else ->
                emptyList()
        }

        exportShapesHelper.exportText(extractableShapes, isModalRequired)
    }

    private fun deleteSelectedShapes() {
        for (shape in environment.getSelectedShapes()) {
            environment.shapeManager.remove(shape)
        }
        environment.clearSelectedShapes()
    }

    private fun editSelectedShape(shape: AbstractShape?) {
        when (shape) {
            is Text -> EditTextShapeHelper.showEditTextDialog(environment, shape)
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
