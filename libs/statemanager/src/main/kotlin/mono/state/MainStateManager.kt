package mono.state

import kotlinx.html.currentTimeMillis
import mono.common.nullToFalse
import mono.graphics.bitmap.MonoBitmapManager
import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import mono.lifecycle.LifecycleOwner
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.add
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Rectangle
import mono.shapesearcher.ShapeSearcher
import mono.state.command.MouseCommand
import mono.state.command.CommandEnvironment
import mono.state.command.CommandType

/**
 * A class which is connect components in the app.
 */
class MainStateManager(
    lifecycleOwner: LifecycleOwner,
    private val mainBoard: MonoBoard,
    private val shapeManager: ShapeManager,
    private val bitmapManager: MonoBitmapManager,
    private val canvasManager: CanvasViewController
) {
    private val shapeSearcher: ShapeSearcher = ShapeSearcher(shapeManager, bitmapManager)

    private var workingParentGroup: Group = shapeManager.root
    private var focusingShapes: Set<AbstractShape> = emptySet()
    private var windowBoardBound: Rect = Rect.ZERO

    private val environment: CommandEnvironment = CommandEnvironmentImpl(this)
    private var currentMouseCommand: MouseCommand? = null
    private var currentCommandType: CommandType = CommandType.ADD_RECTANGLE


    init {
        // TODO: This is for testing
        for (i in 0..10) {
            shapeManager.add(Rectangle(Rect.byLTWH(i * 4, i * 4, 16, 16)))
        }

        canvasManager.mousePointerLiveData
            .distinctUntilChange()
            .observe(lifecycleOwner, listener = ::onMouseEvent)
        canvasManager.windowBoardBoundLiveData
            .observe(lifecycleOwner, throttleDurationMillis = 10) {
                windowBoardBound = it
                console.warn(
                    "Drawing info: window board size $windowBoardBound • " +
                            "pixel size ${canvasManager.windowBoundPx}"
                )

                redraw()
            }

        shapeManager.versionLiveData
            .distinctUntilChange()
            .observe(lifecycleOwner, throttleDurationMillis = 0) {
                redraw()
            }
    }

    private fun onMouseEvent(mousePointer: MousePointer) {
        if (mousePointer is MousePointer.Down) {
            currentMouseCommand =
                MouseCommand.getCommand(environment, mousePointer, currentCommandType)
        }

        val isFinished = currentMouseCommand?.execute(environment, mousePointer).nullToFalse()
        if (isFinished) {
            currentMouseCommand = null
        }
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
        val highlight = if (shape in focusingShapes) Highlight.SELECTED else Highlight.NO
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

        override fun setSelectedShapes(shapes: Set<AbstractShape>) {
            stateManager.focusingShapes = shapes
        }
    }

    companion object {
        private const val DEBUG_PERFORMANCE_AUDIT_ENABLED = false
    }
}
