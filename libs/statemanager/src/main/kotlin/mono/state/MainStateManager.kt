package mono.state

import kotlinx.html.currentTimeMillis
import mono.bitmap.manager.MonoBitmapManager
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
import mono.html.toolbar.OneTimeActionType
import mono.html.toolbar.RetainableActionType
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.add
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.remove
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
import mono.store.manager.StoreManager

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
    private val actionManager: ActionManager,
    storeManager: StoreManager
) {
    private val shapeSearcher: ShapeSearcher = ShapeSearcher(shapeManager, bitmapManager::getBitmap)

    private var workingParentGroup: Group = shapeManager.root

    private var windowBoardBound: Rect = Rect.ZERO

    private val environment: CommandEnvironmentImpl = CommandEnvironmentImpl(this)

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

        val stateHistoryManager = StateHistoryManager(lifecycleOwner, environment, storeManager)

        OneTimeActionHandler(
            lifecycleOwner,
            actionManager.oneTimeActionLiveData,
            environment,
            bitmapManager,
            shapeClipboardManager,
            stateHistoryManager
        )
    }

    private fun onMouseEvent(mousePointer: MousePointer) {
        if (mousePointer is MousePointer.DoubleClick) {
            val targetedShape =
                environment.getSelectedShapes().firstOrNull { it.contains(mousePointer.point) }
            actionManager.setOneTimeAction(OneTimeActionType.EditSelectedShape(targetedShape))
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
            private set(value) {
                stateManager.workingParentGroup = value
            }

        override fun replaceRoot(newRoot: Group) {
            shapeManager.replaceRoot(newRoot)
            workingParentGroup = shapeManager.root
            clearSelectedShapes()
        }

        override fun addShape(shape: AbstractShape?) {
            if (shape != null) {
                shapeManager.add(shape)
            }
        }

        override fun removeShape(shape: AbstractShape?) = shapeManager.remove(shape)

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
