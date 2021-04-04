package mono.html.canvas

import kotlinx.html.dom.append
import kotlinx.html.js.canvas
import mono.common.firstOrNull
import mono.graphics.board.MonoBoard
import mono.graphics.geo.EdgeRelatedPosition
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import mono.html.canvas.canvas.BaseCanvasViewController
import mono.html.canvas.canvas.BoardCanvasViewController
import mono.html.canvas.canvas.GridCanvasViewController
import mono.html.canvas.canvas.InteractionCanvasViewController
import mono.html.canvas.canvas.SelectionCanvasViewController
import mono.html.canvas.mouse.MouseEventObserver
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
import mono.shapebound.InteractionBound
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
    private val gridCanvasViewController: GridCanvasViewController
    private val boardCanvasViewController: BoardCanvasViewController
    private val interactionCanvasViewController: InteractionCanvasViewController
    private val selectionCanvasViewController: SelectionCanvasViewController

    private val canvasControllers: List<BaseCanvasViewController>

    val mousePointerLiveData: LiveData<MousePointer>

    val windowBoundPx: Rect
        get() = gridCanvasViewController.drawingInfo.boundPx

    private val windowBoardBoundMutableLiveData: MutableLiveData<Rect> =
        MutableLiveData(Rect.ZERO)
    val windowBoardBoundLiveData: LiveData<Rect> = windowBoardBoundMutableLiveData

    init {
        container.append {
            canvas(CLASS_NAME_GRID) {}
            canvas(CLASS_NAME_BOARD) {}
            canvas(CLASS_NAME_INTERACTION) {}
            canvas(CLASS_NAME_SELECTION) {}
        }

        gridCanvasViewController = GridCanvasViewController(getCanvas(CLASS_NAME_GRID))
        boardCanvasViewController = BoardCanvasViewController(getCanvas(CLASS_NAME_BOARD), board)
        interactionCanvasViewController =
            InteractionCanvasViewController(getCanvas(CLASS_NAME_INTERACTION))
        selectionCanvasViewController =
            SelectionCanvasViewController(getCanvas(CLASS_NAME_SELECTION))

        canvasControllers = listOf(
            gridCanvasViewController,
            boardCanvasViewController,
            interactionCanvasViewController,
            selectionCanvasViewController
        )

        val mouseEventController = MouseEventObserver(
            container,
            gridCanvasViewController::drawingInfo
        )
        mousePointerLiveData = mouseEventController.mousePointerLiveData

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

    fun drawSelectionBound(bound: Rect?, boundType: BoundType) {
        selectionCanvasViewController.selectingBound = bound
        selectionCanvasViewController.boundType = boundType
        selectionCanvasViewController.draw()
    }

    fun getInteractionPosition(point: Point): EdgeRelatedPosition? =
        selectionCanvasViewController.getDotIndex(point)

    fun setFont(fontSize: Int) {
        for (controller in canvasControllers) {
            controller.setFont(fontSize)
        }
        windowBoardBoundMutableLiveData.value = gridCanvasViewController.drawingInfo.boardBound
    }

    private fun updateCanvasSize() {
        val widthPx = container.clientWidth
        val heightPx = container.clientHeight
        for (controller in canvasControllers) {
            controller.setSizeAndRedraw(widthPx, heightPx)
        }
        windowBoardBoundMutableLiveData.value = gridCanvasViewController.drawingInfo.boardBound
    }

    enum class BoundType(val boundStyleColor: String, val isDash: Boolean) {
        NINE_DOTS("#6b6b6b", isDash = false),
        SIMPLE_RECTANGLE("#858585", isDash = true)
    }

    companion object {
        private const val CLASS_NAME_GRID = "grid-canvas"
        private const val CLASS_NAME_BOARD = "board-canvas"
        private const val CLASS_NAME_INTERACTION = "interaction-canvas"
        private const val CLASS_NAME_SELECTION = "selection-canvas"
    }
}
