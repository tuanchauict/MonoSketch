package mono.html.canvas

import kotlinx.html.dom.append
import kotlinx.html.js.canvas
import mono.common.firstOrNull
import mono.graphics.board.MonoBoard
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import mono.html.canvas.canvas.BoardCanvasViewController
import mono.html.canvas.canvas.DrawingInfoController
import mono.html.canvas.canvas.GridCanvasViewController
import mono.html.canvas.canvas.InteractionCanvasViewController
import mono.html.canvas.canvas.SelectionCanvasViewController
import mono.html.canvas.mouse.MouseEventObserver
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
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

    private val windowBoardBoundMutableLiveData: MutableLiveData<Rect> =
        MutableLiveData(Rect.ZERO)
    val windowBoardBoundLiveData: LiveData<Rect> = windowBoardBoundMutableLiveData

    init {
        val drawingInfoLiveData = drawingInfoController.drawingInfoLiveData

        val mouseEventController = MouseEventObserver(
            lifecycleOwner,
            container,
            drawingInfoLiveData
        )
        mousePointerLiveData = mouseEventController.mousePointerLiveData

        container.append {
            canvas(CLASS_NAME_GRID) {}
            canvas(CLASS_NAME_BOARD) {}
            canvas(CLASS_NAME_INTERACTION) {}
            canvas(CLASS_NAME_SELECTION) {}
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

    fun getInteractionPoint(point: Point): InteractionPoint? =
        interactionCanvasViewController.getInteractionPoint(point)

    fun setFont(fontSize: Int) {
        drawingInfoController.setFont(fontSize)
        windowBoardBoundMutableLiveData.value = gridCanvasViewController.drawingInfo.boardBound
    }

    private fun updateCanvasSize() {
        val widthPx = container.clientWidth
        val heightPx = container.clientHeight
        drawingInfoController.setSize(widthPx, heightPx)
        windowBoardBoundMutableLiveData.value = gridCanvasViewController.drawingInfo.boardBound
    }

    companion object {
        private const val CLASS_NAME_GRID = "grid-canvas"
        private const val CLASS_NAME_BOARD = "board-canvas"
        private const val CLASS_NAME_INTERACTION = "interaction-canvas"
        private const val CLASS_NAME_SELECTION = "selection-canvas"
    }
}
