package mono.state

import kotlinx.html.currentTimeMillis
import mono.graphics.bitmap.MonoBitmapManager
import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import mono.lifecycle.LifecycleOwner
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.add
import mono.shape.command.ChangeBound
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Rectangle

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
    private var workingParentGroup: Group = shapeManager.root
    private var focusingShapes: Set<AbstractShape> = emptySet()

    private var windowBoardBound: Rect = Rect.ZERO

    init {
        // TODO: This is for testing
        for (i in 0..1000) {
            shapeManager.add(Rectangle(Rect.byLTWH(i, 10, 10, 10)))
        }

        canvasManager.mousePointerLiveData
            .distinctUntilChange()
            .observe(lifecycleOwner, listener = ::addShapeWithMouse)
        canvasManager.windowBoardBoundLiveData
            .observe(lifecycleOwner, throttleDurationMillis = 10) {
                windowBoardBound = it
                console.warn("Drawing info: window board size $windowBoardBound • " +
                        "pixel size ${canvasManager.windowBoundPx}")

                redraw()
            }

        shapeManager.versionLiveData
            .distinctUntilChange()
            .observe(lifecycleOwner, throttleDurationMillis = 0) {
                redraw()
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
        clear(windowBoardBound)
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
    }

    private fun addShapeWithMouse(mousePointer: MousePointer) = when (mousePointer) {
        is MousePointer.Down -> {
            val rectangle =
                Rectangle(mousePointer.point, mousePointer.point, workingParentGroup.id)
            focusingShapes = setOf(rectangle)
            shapeManager.add(rectangle)
        }
        is MousePointer.Move ->
            changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)
        is MousePointer.Up ->
            changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)
        MousePointer.Idle,
        is MousePointer.Click -> Unit
    }

    private fun changeShapeBound(point1: Point, point2: Point) {
        val rect = Rect.byLTRB(
            left = point1.left,
            top = point1.top,
            right = point2.left,
            bottom = point2.top
        )
        for (shape in focusingShapes) {
            shapeManager.execute(ChangeBound(shape, rect))
        }
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

    companion object {
        private const val DEBUG_PERFORMANCE_AUDIT_ENABLED = false
    }
}
