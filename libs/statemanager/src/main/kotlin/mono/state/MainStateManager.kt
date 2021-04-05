package mono.state

import kotlinx.html.currentTimeMillis
import mono.common.nullToFalse
import mono.graphics.bitmap.MonoBitmapManager
import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import mono.keycommand.KeyCommand
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.add
import mono.shape.command.ChangeExtra
import mono.shape.command.ChangeText
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Text
import mono.shapebound.InteractionPoint
import mono.shapesearcher.ShapeSearcher
import mono.state.command.CommandEnvironment
import mono.state.command.CommandType
import mono.state.command.MouseCommand
import mono.state.command.MouseCommandFactory

/**
 * A class which is connect components in the app.
 */
class MainStateManager(
    lifecycleOwner: LifecycleOwner,
    private val mainBoard: MonoBoard,
    private val shapeManager: ShapeManager,
    private val bitmapManager: MonoBitmapManager,
    private val canvasManager: CanvasViewController,
    keyCommandLiveData: LiveData<KeyCommand>,
    mousePointerLiveData: LiveData<MousePointer>
) {
    private val shapeSearcher: ShapeSearcher = ShapeSearcher(shapeManager, bitmapManager)

    private var workingParentGroup: Group = shapeManager.root

    private var selectedShapeManager: SelectedShapeManager =
        SelectedShapeManager(shapeManager, canvasManager, ::requestRedraw)

    private var windowBoardBound: Rect = Rect.ZERO

    private val environment: CommandEnvironment = CommandEnvironmentImpl(this)
    private var currentMouseCommand: MouseCommand? = null
    private var currentCommandType: CommandType = CommandType.IDLE

    private val redrawRequestMutableLiveData: MutableLiveData<Unit> = MutableLiveData(Unit)

    init {
        mousePointerLiveData
            .distinctUntilChange()
            .observe(lifecycleOwner, listener = ::onMouseEvent)
        keyCommandLiveData.observe(lifecycleOwner, listener = ::onKeyEvent)

        canvasManager.windowBoardBoundLiveData
            .observe(lifecycleOwner, throttleDurationMillis = 10) {
                windowBoardBound = it
                console.warn(
                    "Drawing info: window board size $windowBoardBound • " +
                        "pixel size ${canvasManager.windowBoundPx}"
                )
                requestRedraw()
            }

        shapeManager.versionLiveData
            .distinctUntilChange()
            .observe(lifecycleOwner, throttleDurationMillis = 0) {
                requestRedraw()
            }

        redrawRequestMutableLiveData.observe(lifecycleOwner, 1) { redraw() }

        addDemoShape()
    }

    private fun addDemoShape() {
        // TODO: This is for testing
        val commandText =
            """
                Keyboard Command   | Function
                -------------------|----------------------------
                R                  | Draw rectangle
                T                  | Draw text
                DELETE             | Remove selected shape(s)
                ESC                | Deselect, enter select mode
                UP/DOWN/LEFT/RIGHT | Move selected shape(s)
            """.trimIndent()
        val textShape = Text(Rect.byLTWH(1, 1, 51, 9))
        shapeManager.add(textShape)
        shapeManager.execute(ChangeText(textShape, commandText))
    }

    private fun onMouseEvent(mousePointer: MousePointer) {
        if (mousePointer is MousePointer.Down) {
            currentMouseCommand =
                MouseCommandFactory.getCommand(environment, mousePointer, currentCommandType)
        }

        val isFinished = currentMouseCommand?.execute(environment, mousePointer).nullToFalse()
        if (isFinished) {
            currentMouseCommand = null
            requestRedraw()
        }
    }

    private fun onKeyEvent(keyCommand: KeyCommand) {
        when (keyCommand) {
            KeyCommand.ESC ->
                if (selectedShapeManager.selectedShapes.isEmpty()) {
                    currentCommandType = CommandType.IDLE
                } else {
                    selectedShapeManager.setSelectedShapes()
                }
            KeyCommand.ADD_RECTANGLE -> currentCommandType = CommandType.ADD_RECTANGLE
            KeyCommand.ADD_TEXT -> currentCommandType = CommandType.ADD_TEXT

            KeyCommand.DELETE -> selectedShapeManager.deleteSelectedShapes()
            KeyCommand.ENTER_EDIT_MODE -> selectedShapeManager.editSelectedShapes()

            KeyCommand.MOVE_DOWN -> selectedShapeManager.moveSelectedShape(1, 0)
            KeyCommand.MOVE_UP -> selectedShapeManager.moveSelectedShape(-1, 0)
            KeyCommand.MOVE_LEFT -> selectedShapeManager.moveSelectedShape(0, -1)
            KeyCommand.MOVE_RIGHT -> selectedShapeManager.moveSelectedShape(0, 1)
            KeyCommand.IDLE -> Unit
        }
    }

    private fun requestRedraw() {
        redrawRequestMutableLiveData.value = Unit
    }

    private fun redraw() {
        auditPerformance("Redraw") {
            mainBoard.redraw()
        }
        auditPerformance("Draw canvas") {
            canvasManager.drawBoard()
        }
    }

    private fun MonoBoard.redraw() {
        shapeSearcher.clear(windowBoardBound)
        clearAndSetWindow(windowBoardBound)
        drawShape(shapeManager.root)
    }

    private fun MonoBoard.drawShape(shape: AbstractShape) {
        if (shape is Group) {
            for (child in shape.items) {
                drawShape(child)
            }
            return
        }
        val bitmap = bitmapManager.getBitmap(shape) ?: return
        val highlight =
            if (shape in selectedShapeManager.selectedShapes) Highlight.SELECTED else Highlight.NO
        fill(shape.bound.position, bitmap, highlight)
        shapeSearcher.register(shape)
    }

    private fun auditPerformance(
        objective: String,
        isEnabled: Boolean = DEBUG_PERFORMANCE_AUDIT_ENABLED,
        action: () -> Unit
    ) {
        if (!isEnabled) {
            action()
            return
        }
        val t0 = currentTimeMillis()
        action()
        println("$objective runtime: ${currentTimeMillis() - t0}")
    }

    private class CommandEnvironmentImpl(
        private val stateManager: MainStateManager
    ) : CommandEnvironment {
        override val shapeManager: ShapeManager
            get() = stateManager.shapeManager

        override val shapeSearcher: ShapeSearcher
            get() = stateManager.shapeSearcher

        override val workingParentGroup: Group
            get() = stateManager.workingParentGroup

        override val selectedShapeManager: SelectedShapeManager
            get() = stateManager.selectedShapeManager

        override fun getInteractionPoint(point: Point): InteractionPoint? =
            stateManager.canvasManager.getInteractionPoint(point)
    }

    companion object {
        private const val DEBUG_PERFORMANCE_AUDIT_ENABLED = false
    }
}
