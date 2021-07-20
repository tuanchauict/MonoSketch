package mono.html.canvas

import kotlinx.dom.addClass
import kotlinx.html.dom.append
import kotlinx.html.js.canvas
import mono.common.firstOrNull
import mono.graphics.board.MonoBoard
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import mono.html.canvas.canvas.AxisCanvasViewController
import mono.html.canvas.canvas.BoardCanvasViewController
import mono.html.canvas.canvas.DrawingInfoController
import mono.html.canvas.canvas.GridCanvasViewController
import mono.html.canvas.canvas.InteractionCanvasViewController
import mono.html.canvas.canvas.SelectionCanvasViewController
import mono.html.canvas.mouse.MouseEventObserver
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MediatorLiveData
import mono.livedata.distinctUntilChange
import mono.shapebound.InteractionBound
import mono.shapebound.InteractionPoint
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement

/**
 * A view controller class which renders the board to user.
 */
class CanvasViewController(
    lifecycleOwner: LifecycleOwner,
    private val container: HTMLDivElement,
    axisContainer: HTMLDivElement,
    board: MonoBoard,
    windowSizeLiveData: LiveData<Size>
) {
    private val drawingInfoController = DrawingInfoController(container)

    private val gridCanvasViewController: GridCanvasViewController
    private val boardCanvasViewController: BoardCanvasViewController
    private val interactionCanvasViewController: InteractionCanvasViewController
    private val selectionCanvasViewController: SelectionCanvasViewController

    val mousePointerLiveData: LiveData<MousePointer>

    val windowBoundPx: Rect
        get() = gridCanvasViewController.drawingInfo.boundPx

    private val windowBoardBoundMediatorLiveData: MediatorLiveData<Rect> =
        MediatorLiveData(Rect.ZERO)
    val windowBoardBoundLiveData: LiveData<Rect> = windowBoardBoundMediatorLiveData

    init {
        val drawingInfoLiveData = drawingInfoController.drawingInfoLiveData

        val mouseEventController = MouseEventObserver(
            lifecycleOwner,
            container,
            drawingInfoLiveData
        )
        mousePointerLiveData = mouseEventController.mousePointerLiveData
        mouseEventController.drawingOffsetPointPxLiveData.observe(
            lifecycleOwner,
            throttleDurationMillis = 0,
            listener = drawingInfoController::setOffset
        )

        container.addClass("top-divider")

        container.append {
            canvas(CLASS_NAME_GRID) {}
            canvas(CLASS_NAME_BOARD) {}
            canvas(CLASS_NAME_INTERACTION) {}
            canvas(CLASS_NAME_SELECTION) {}
        }
        axisContainer.append {
            val axisCanvas = canvas(CLASS_NAME_AXIS) {}
            AxisCanvasViewController(lifecycleOwner, axisCanvas, drawingInfoLiveData)
        }

        gridCanvasViewController = GridCanvasViewController(
            lifecycleOwner,
            getCanvas(CLASS_NAME_GRID),
            drawingInfoLiveData
        )

        boardCanvasViewController = BoardCanvasViewController(
            lifecycleOwner,
            getCanvas(CLASS_NAME_BOARD),
            board,
            drawingInfoLiveData
        )
        interactionCanvasViewController = InteractionCanvasViewController(
            lifecycleOwner,
            getCanvas(CLASS_NAME_INTERACTION),
            drawingInfoLiveData,
            mousePointerLiveData
        )
        selectionCanvasViewController = SelectionCanvasViewController(
            lifecycleOwner,
            getCanvas(CLASS_NAME_SELECTION),
            drawingInfoLiveData
        )

        windowSizeLiveData.distinctUntilChange().observe(lifecycleOwner) {
            updateCanvasSize()
        }
        windowBoardBoundMediatorLiveData.add(drawingInfoLiveData) { value = it.boardBound }
    }

    private fun getCanvas(className: String): HTMLCanvasElement {
        return container.getElementsByClassName(className).firstOrNull()!!
    }

    fun drawBoard() {
        boardCanvasViewController.draw()
        interactionCanvasViewController.draw()
        selectionCanvasViewController.draw()
    }

    fun drawInteractionBounds(interactionBounds: List<InteractionBound>) {
        interactionCanvasViewController.interactionBounds = interactionBounds
        interactionCanvasViewController.draw()
    }

    fun drawSelectionBound(bound: Rect?) {
        selectionCanvasViewController.selectingBound = bound
        selectionCanvasViewController.draw()
    }

    fun getInteractionPoint(pointPx: Point): InteractionPoint? =
        interactionCanvasViewController.getInteractionPoint(pointPx)

    fun setFont(fontSize: Int) {
        drawingInfoController.setFont(fontSize)
    }

    private fun updateCanvasSize() {
        val widthPx = container.clientWidth
        val heightPx = container.clientHeight

        // Avoid layout mistake on Safari when height is set to 0 after being correct.
        if (widthPx == 0 || heightPx == 0) {
            return
        }
        drawingInfoController.setSize(widthPx, heightPx)
    }

    fun setMouseCursor(mouseCursor: String) {
        container.style.cursor = mouseCursor
    }

    companion object {
        private const val CLASS_NAME_GRID = "grid-canvas"
        private const val CLASS_NAME_BOARD = "board-canvas"
        private const val CLASS_NAME_INTERACTION = "interaction-canvas"
        private const val CLASS_NAME_SELECTION = "selection-canvas"
        private const val CLASS_NAME_AXIS = "axis-canvas"
    }
}
