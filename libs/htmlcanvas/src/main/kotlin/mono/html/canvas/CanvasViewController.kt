package mono.html.canvas

import kotlinx.html.dom.append
import kotlinx.html.js.canvas
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
import mono.html.canvas.mouse.MouseEventObserver
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
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
    private lateinit var gridCanvasViewController: GridCanvasViewController
    private lateinit var boardCanvasViewController: BoardCanvasViewController
    private lateinit var interactionCanvasViewController: InteractionCanvasViewController

    private lateinit var canvasControllers: List<BaseCanvasViewController>
    private val mouseEventController: MouseEventObserver by lazy {
        MouseEventObserver(
            container,
            gridCanvasViewController::drawingInfo
        )
    }
    val mousePointerLiveData: LiveData<MousePointer> by lazy {
        mouseEventController.mousePointerLiveData
    }

    val windowBoundPx: Rect
        get() = gridCanvasViewController.drawingInfo.boundPx

    private val windowBoardBoundMutableLiveData: MutableLiveData<Rect> =
        MutableLiveData(Rect.ZERO)
    val windowBoardBoundLiveData: LiveData<Rect> = windowBoardBoundMutableLiveData

    init {
        container.append {
            val gridCanvas = canvas {}
            val boardCanvas = canvas {}
            val interactionCanvas = canvas { }

            gridCanvasViewController = GridCanvasViewController(gridCanvas)
            boardCanvasViewController = BoardCanvasViewController(boardCanvas, board)
            interactionCanvasViewController = InteractionCanvasViewController(interactionCanvas)

            canvasControllers = listOf(
                gridCanvasViewController,
                boardCanvasViewController,
                interactionCanvasViewController
            )
        }

        windowSizeLiveData.distinctUntilChange().observe(lifecycleOwner) {
            updateCanvasSize()
        }
    }

    fun drawBoard() {
        boardCanvasViewController.draw()
        interactionCanvasViewController.draw()
    }

    fun drawInteractionBound(bound: Rect?, boundType: BoundType) {
        interactionCanvasViewController.selectedShapesBoundingRect = bound
        interactionCanvasViewController.boundType = boundType
        interactionCanvasViewController.draw()
    }

    fun getInteractionPosition(point: Point): EdgeRelatedPosition? =
        interactionCanvasViewController.getDotIndex(point)

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
}
