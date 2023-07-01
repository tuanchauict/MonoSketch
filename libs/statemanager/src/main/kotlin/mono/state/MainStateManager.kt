/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state

import mono.actionmanager.ActionManager
import mono.actionmanager.OneTimeActionType
import mono.actionmanager.RetainableActionType
import mono.bitmap.manager.MonoBitmapManager
import mono.common.MouseCursor
import mono.common.currentTimeMillis
import mono.common.post
import mono.environment.Build
import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.add
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.connector.ShapeConnector
import mono.shape.remove
import mono.shape.selection.SelectedShapeManager
import mono.shape.selection.SelectedShapeManager.ShapeFocusType
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.RootGroup
import mono.shape.shape.Text
import mono.shapebound.InteractionPoint
import mono.shapebound.LineInteractionBound
import mono.shapebound.ScalableInteractionBound
import mono.shapesearcher.ShapeSearcher
import mono.state.command.CommandEnvironment
import mono.state.command.CommandEnvironment.EditingMode
import mono.state.command.MouseCommandFactory
import mono.state.command.mouse.MouseCommand
import mono.store.dao.workspace.WorkspaceDao
import mono.ui.appstate.AppUiStateManager

/**
 * A class which connects components in the app.
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
    applicationActiveStateLiveData: LiveData<Boolean>,
    private val actionManager: ActionManager,
    appUiStateManager: AppUiStateManager,
    initialRootId: String = "",
    private val workspaceDao: WorkspaceDao = WorkspaceDao.instance
) {
    private val shapeSearcher: ShapeSearcher = ShapeSearcher(shapeManager, bitmapManager::getBitmap)

    /**
     * The current working parent group, which is the group that is focused, shape actions will be
     * applied to this group.
     *
     * This is similar to the concept of "current directory" in file system.
     */
    private var workingParentGroup: Group = shapeManager.root

    private var windowBoardBound: Rect = Rect.ZERO

    private val environment = CommandEnvironmentImpl(this)

    private var currentMouseCommand: MouseCommand? = null
    private var currentRetainableActionType: RetainableActionType = RetainableActionType.IDLE

    private val redrawRequestMutableLiveData = MutableLiveData(Unit)

    private val editingModeLiveData = MutableLiveData(EditingMode.idle(null))
    private val stateHistoryManager =
        StateHistoryManager(lifecycleOwner, environment, canvasManager)

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

        stateHistoryManager.restoreAndStartObserveStateChange(initialRootId)

        OneTimeActionHandler(
            lifecycleOwner,
            actionManager.oneTimeActionLiveData,
            environment,
            bitmapManager,
            shapeClipboardManager,
            stateHistoryManager,
            appUiStateManager
        )

        applicationActiveStateLiveData.observe(lifecycleOwner) { isActive ->
            if (isActive) {
                reflectChangedFromLocal()
            }
        }
    }

    private fun onMouseEvent(mousePointer: MousePointer) {
        if (mousePointer is MousePointer.DoubleClick) {
            val targetedShape =
                environment.getSelectedShapes()
                    .firstOrNull { it.contains(mousePointer.boardCoordinate) }
            actionManager.setOneTimeAction(OneTimeActionType.EditSelectedShape(targetedShape))
            return
        }

        val mouseCommand =
            MouseCommandFactory.getCommand(environment, mousePointer, currentRetainableActionType)
                ?: currentMouseCommand
                ?: return
        currentMouseCommand = mouseCommand

        environment.enterEditingMode()
        val commandResultType = mouseCommand.execute(environment, mousePointer)

        if (commandResultType == MouseCommand.CommandResultType.DONE) {
            environment.exitEditingMode(true)
        }

        if (commandResultType == MouseCommand.CommandResultType.DONE ||
            commandResultType == MouseCommand.CommandResultType.WORKING_PHASE2
        ) {
            currentMouseCommand = null
            requestRedraw()
            // Avoid click when adding shape cause shape selection command
            post { actionManager.setRetainableAction(RetainableActionType.IDLE) }
        }
    }

    /**
     * Redraws all content on the workspace.
     * This is used when the theme is updated.
     */
    fun forceFullyRedrawWorkspace() {
        canvasManager.fullyRedraw()
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
        val highlight = when {
            shape is Text && shape.isTextEditing -> Highlight.TEXT_EDITING
            shape in environment.getSelectedShapes() -> Highlight.SELECTED
            else -> {
                when (selectedShapeManager.getFocusingType(shape)) {
                    ShapeFocusType.LINE_CONNECTING -> Highlight.LINE_CONNECT_FOCUSING
                    null -> Highlight.NO
                }
            }
        }
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
            is MousePointer.Move -> getMouseMovingCursor(mousePointer)

            is MousePointer.Drag -> {
                val mouseCommand = currentMouseCommand
                if (mouseCommand != null) mouseCommand.mouseCursor else MouseCursor.DEFAULT
            }

            is MousePointer.Up -> MouseCursor.DEFAULT

            MousePointer.Idle,
            is MousePointer.Down,
            is MousePointer.Click,
            is MousePointer.DoubleClick -> null
        }
        if (mouseCursor != null) {
            canvasManager.setMouseCursor(mouseCursor)
        }
    }

    private fun getMouseMovingCursor(mousePointer: MousePointer.Move): MouseCursor {
        val interactionPoint = canvasManager.getInteractionPoint(mousePointer.pointPx)
        return interactionPoint?.mouseCursor ?: currentRetainableActionType.mouseCursor
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

    private fun reflectChangedFromLocal() {
        val rootId = shapeManager.root.id
        val rootVersion = shapeManager.root.versionCode
        val currentRoot = workspaceDao.getObject(rootId).rootGroup
        // TODO: load from storage
        val shapeConnector = ShapeConnector()
        when {
            currentRoot == null -> {
                // TODO: Notify to user this project is removed
            }

            rootVersion != currentRoot.versionCode -> {
                environment.replaceRoot(RootGroup(currentRoot), shapeConnector)
            }
        }
    }

    private class CommandEnvironmentImpl(
        private val stateManager: MainStateManager
    ) : CommandEnvironment {
        override val shapeManager: ShapeManager
            get() = stateManager.shapeManager

        private val shapeSearcher: ShapeSearcher
            get() = stateManager.shapeSearcher

        override val editingModeLiveData: LiveData<EditingMode>
            get() = stateManager.editingModeLiveData

        override var workingParentGroup: Group
            get() = stateManager.workingParentGroup
            private set(value) {
                stateManager.workingParentGroup = value
            }

        override fun replaceRoot(newRoot: RootGroup, newShapeConnector: ShapeConnector) {
            val currentRoot = shapeManager.root
            if (currentRoot.id != newRoot.id) {
                stateManager.workspaceDao.getObject(objectId = newRoot.id).updateLastOpened()
                stateManager.canvasManager.setOffset(
                    stateManager.workspaceDao.getObject(newRoot.id).offset
                )
                stateManager.stateHistoryManager.clear()
            }

            shapeManager.replaceRoot(newRoot, newShapeConnector)
            workingParentGroup = shapeManager.root
            clearSelectedShapes()
        }

        override fun enterEditingMode() {
            stateManager.editingModeLiveData.value = EditingMode.edit()
        }

        override fun exitEditingMode(isNewStateAccepted: Boolean) {
            val skippedVersion =
                if (isNewStateAccepted) null else shapeManager.versionLiveData.value
            stateManager.editingModeLiveData.value = EditingMode.idle(skippedVersion)
        }

        override fun addShape(shape: AbstractShape?) {
            if (shape != null) {
                shapeManager.add(shape)
            }
        }

        override fun removeShape(shape: AbstractShape?) = shapeManager.remove(shape)

        override fun getShapes(point: Point): Sequence<AbstractShape> =
            shapeSearcher.getShapes(point)

        override fun getAllShapesInZone(bound: Rect): Sequence<AbstractShape> =
            shapeSearcher.getAllShapesInZone(bound)

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

        override fun setFocusingShape(
            shape: AbstractShape?,
            focusType: ShapeFocusType
        ) = stateManager.selectedShapeManager.setFocusingShape(shape, focusType)

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
