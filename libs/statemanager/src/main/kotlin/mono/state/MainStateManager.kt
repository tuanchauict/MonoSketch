package mono.state

import kotlinx.html.currentTimeMillis
import mono.bitmap.manager.MonoBitmapManager
import mono.common.exhaustive
import mono.common.nullToFalse
import mono.environment.Build
import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import mono.html.toolbar.ActionManager
import mono.html.toolbar.RetainableActionType
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.command.ChangeBound
import mono.shape.command.ChangeExtra
import mono.shape.command.ChangeOrder
import mono.shape.command.ChangeOrder.ChangeOrderType
import mono.shape.extra.manager.ShapeExtraManager
import mono.shape.extra.manager.model.TextAlign
import mono.shape.selection.SelectedShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import mono.shapebound.InteractionPoint
import mono.shapebound.LineInteractionBound
import mono.shapebound.ScalableInteractionBound
import mono.shapesearcher.ShapeSearcher
import mono.state.command.CommandEnvironment
import mono.state.command.MouseCommandFactory
import mono.state.command.mouse.MouseCommand
import mono.state.command.text.EditTextShapeHelper

/**
 * A class which is connect components in the app.
 */
class MainStateManager(
    lifecycleOwner: LifecycleOwner,
    private val mainBoard: MonoBoard,
    private val shapeManager: ShapeManager,
    private val selectedShapeManager: SelectedShapeManager,
    private val bitmapManager: MonoBitmapManager,
    private val canvasManager: CanvasViewController,
    shapeClipboardManager: ShapeClipboardManager,
    mousePointerLiveData: LiveData<MousePointer>,
    actionManager: ActionManager
) {
    private val shapeSearcher: ShapeSearcher = ShapeSearcher(shapeManager, bitmapManager::getBitmap)

    private var workingParentGroup: Group = shapeManager.root

    private var windowBoardBound: Rect = Rect.ZERO

    private val environment: CommandEnvironmentImpl = CommandEnvironmentImpl(this)

    internal val clipboardManager: ClipboardManager =
        ClipboardManager(lifecycleOwner, environment, shapeClipboardManager)


    private var currentMouseCommand: MouseCommand? = null
    private var currentRetainableActionType: RetainableActionType = RetainableActionType.IDLE

    private val redrawRequestMutableLiveData: MutableLiveData<Unit> = MutableLiveData(Unit)

    init {
        mousePointerLiveData
            .distinctUntilChange()
            .observe(lifecycleOwner, listener = ::onMouseEvent)

        mousePointerLiveData
            .distinctUntilChange()
            .observe(lifecycleOwner, listener = ::updateMouseCursor)

        canvasManager.windowBoardBoundLiveData.observe(lifecycleOwner) {
            windowBoardBound = it
            if (Build.DEBUG) {
                println(
                    "¶ Drawing info: window board size $windowBoardBound • " +
                        "pixel size ${canvasManager.windowBoundPx}"
                )
            }
            requestRedraw()
        }

        shapeManager.versionLiveData
            .distinctUntilChange()
            .observe(lifecycleOwner) {
                requestRedraw()
            }

        environment.selectedShapesLiveData.observe(
            lifecycleOwner,
            listener = ::updateInteractionBounds
        )

        redrawRequestMutableLiveData.observe(
            lifecycleOwner,
            throttleDurationMillis = 0
        ) { redraw() }

        actionManager.retainableActionLiveData.observe(lifecycleOwner) {
            currentRetainableActionType = it
        }

        OneTimeActionHandler(
            lifecycleOwner,
            actionManager.oneTimeActionLiveData,
            environment,
            this,
            bitmapManager,
            shapeClipboardManager
        )
    }

    fun moveSelectedShapes(offsetRow: Int, offsetCol: Int) {
        val selectedShapes = environment.getSelectedShapes()
        for (shape in selectedShapes) {
            val bound = shape.bound
            val newPosition = Point(bound.left + offsetCol, bound.top + offsetRow)
            val newBound = shape.bound.copy(position = newPosition)
            shapeManager.execute(ChangeBound(shape, newBound))
        }
        environment.updateInteractionBounds()
    }

    fun setSelectedShapeBound(left: Int?, top: Int?, width: Int?, height: Int?) {
        val singleShape = environment.getSelectedShapes().singleOrNull() ?: return
        val currentBound = singleShape.bound
        val newLeft = left ?: currentBound.left
        val newTop = top ?: currentBound.top
        val newWidth = width ?: currentBound.width
        val newHeight = height ?: currentBound.height
        shapeManager.execute(
            ChangeBound(singleShape, Rect.byLTWH(newLeft, newTop, newWidth, newHeight))
        )
        environment.updateInteractionBounds()
    }

    fun setSelectedShapeFillExtra(isEnabled: Boolean?, newFillStyleId: String?) {
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
        shapeManager.execute(ChangeExtra(singleShape, newExtra))
    }

    fun setSelectedShapeBorderExtra(isEnabled: Boolean?, newBorderStyleId: String?) {
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
        shapeManager.execute(ChangeExtra(singleShape, newExtra))
    }

    fun setSelectedShapeStartAnchorExtra(isEnabled: Boolean?, newAnchorId: String?) {
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
        shapeManager.execute(ChangeExtra(line, newExtra))
    }

    fun setSelectedShapeEndAnchorExtra(isEnabled: Boolean?, newAnchorId: String?) {
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
        shapeManager.execute(ChangeExtra(line, newExtra))
    }

    fun changeShapeOrder(orderType: ChangeOrderType) {
        val singleShape = environment.getSelectedShapes().singleOrNull() ?: return
        shapeManager.execute(ChangeOrder(singleShape, orderType))
    }

    fun editSelectedShape(shape: AbstractShape?) {
        when (shape) {
            is Text -> EditTextShapeHelper.showEditTextDialog(environment, shape)
            is Line,
            is Rectangle,
            is MockShape,
            is Group,
            null -> Unit
        }.exhaustive
    }

    fun setTextAlignment(
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
        shapeManager.execute(ChangeExtra(textShape, newExtra))
    }

    private fun onMouseEvent(mousePointer: MousePointer) {
        if (mousePointer is MousePointer.DoubleClick) {
            val targetedShape =
                environment.getSelectedShapes().firstOrNull { it.contains(mousePointer.point) }
            editSelectedShape(targetedShape)
            return
        }

        currentMouseCommand = MouseCommandFactory.getCommand(
            environment,
            mousePointer,
            currentRetainableActionType
        ) ?: currentMouseCommand

        val isFinished = currentMouseCommand?.execute(environment, mousePointer).nullToFalse()
        if (isFinished) {
            currentMouseCommand = null
            requestRedraw()
        }
    }

    private fun requestRedraw() {
        redrawRequestMutableLiveData.value = Unit
    }

    private fun redraw() {
        auditPerformance("Redraw") {
            redrawMainBoard()
        }
        auditPerformance("Draw canvas") {
            canvasManager.drawBoard()
        }
    }

    private fun redrawMainBoard() {
        shapeSearcher.clear(windowBoardBound)
        mainBoard.clearAndSetWindow(windowBoardBound)
        drawShapeToMainBoard(shapeManager.root)
    }

    private fun drawShapeToMainBoard(shape: AbstractShape) {
        if (shape is Group) {
            for (child in shape.items) {
                drawShapeToMainBoard(child)
            }
            return
        }
        val bitmap = bitmapManager.getBitmap(shape) ?: return
        val highlight =
            if (shape in environment.getSelectedShapes()) Highlight.SELECTED else Highlight.NO
        mainBoard.fill(shape.bound.position, bitmap, highlight)
        shapeSearcher.register(shape)
    }

    private fun auditPerformance(
        objective: String,
        isEnabled: Boolean = DEBUG_PERFORMANCE_AUDIT_ENABLED,
        action: () -> Unit
    ) {
        if (!isEnabled || !Build.DEBUG) {
            action()
            return
        }
        val t0 = currentTimeMillis()
        action()
        println("$objective runtime: ${currentTimeMillis() - t0}")
    }


    private fun updateMouseCursor(mousePointer: MousePointer) {
        val mouseCursor = when (mousePointer) {
            is MousePointer.Move -> {
                val interactionPoint = canvasManager.getInteractionPoint(mousePointer.pointPx)
                interactionPoint?.mouseCursor ?: "default"
            }
            is MousePointer.Drag -> {
                val mouseCommand = currentMouseCommand
                if (mouseCommand != null) mouseCommand.mouseCursor else "default"
            }
            is MousePointer.Up -> "default"

            MousePointer.Idle,
            is MousePointer.Down,
            is MousePointer.Click,
            is MousePointer.DoubleClick -> null
        }
        if (mouseCursor != null) {
            canvasManager.setMouseCursor(mouseCursor)
        }
    }

    private fun updateInteractionBounds(selectedShapes: Collection<AbstractShape>) {
        val bounds = selectedShapes.mapNotNull {
            when (it) {
                is Rectangle,
                is Text -> ScalableInteractionBound(it.id, it.bound)
                is Line -> LineInteractionBound(it.id, it.edges)
                is Group -> null // TODO: Add new Interaction bound type for Group
                is MockShape -> null
            }
        }
        canvasManager.drawInteractionBounds(bounds)
        requestRedraw()
    }

    private class CommandEnvironmentImpl(
        private val stateManager: MainStateManager
    ) : CommandEnvironment {
        override val shapeManager: ShapeManager
            get() = stateManager.shapeManager

        override val shapeSearcher: ShapeSearcher
            get() = stateManager.shapeSearcher

        override var workingParentGroup: Group
            get() = stateManager.workingParentGroup
            set(value) {
                stateManager.workingParentGroup = value
            }

        override fun getWindowBound(): Rect = stateManager.windowBoardBound

        override fun getInteractionPoint(pointPx: Point): InteractionPoint? =
            stateManager.canvasManager.getInteractionPoint(pointPx)

        override fun updateInteractionBounds() =
            stateManager.updateInteractionBounds(stateManager.selectedShapeManager.selectedShapes)

        override fun isPointInInteractionBounds(point: Point): Boolean =
            stateManager.selectedShapeManager.selectedShapes.any { it.contains(point) }

        override fun setSelectionBound(bound: Rect?) =
            stateManager.canvasManager.drawSelectionBound(bound)

        override val selectedShapesLiveData: LiveData<Set<AbstractShape>> =
            stateManager.selectedShapeManager.selectedShapesLiveData

        override fun getSelectedShapes(): Set<AbstractShape> =
            stateManager.selectedShapeManager.selectedShapes

        override fun addSelectedShape(shape: AbstractShape?) {
            if (shape != null) {
                stateManager.selectedShapeManager.addSelectedShape(shape)
            }
        }

        override fun toggleShapeSelection(shape: AbstractShape) =
            stateManager.selectedShapeManager.toggleSelection(shape)

        override fun selectAllShapes() {
            for (shape in stateManager.workingParentGroup.items) {
                addSelectedShape(shape)
            }
        }

        override fun clearSelectedShapes() = stateManager.selectedShapeManager.clearSelectedShapes()

        override fun getEdgeDirection(point: Point): DirectedPoint.Direction? =
            shapeSearcher.getEdgeDirection(point)

        override fun toXPx(column: Double): Double = stateManager.canvasManager.toXPx(column)

        override fun toYPx(row: Double): Double = stateManager.canvasManager.toYPx(row)

        override fun toWidthPx(width: Double): Double = stateManager.canvasManager.toWidthPx(width)

        override fun toHeightPx(height: Double): Double =
            stateManager.canvasManager.toHeightPx(height)
    }

    companion object {
        private const val DEBUG_PERFORMANCE_AUDIT_ENABLED = false
    }
}
