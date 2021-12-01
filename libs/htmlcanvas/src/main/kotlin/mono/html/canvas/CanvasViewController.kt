package mono.html.canvas

import kotlinx.dom.addClass
import mono.common.MouseCursor
import mono.graphics.board.MonoBoard
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import mono.html.Canvas
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
import org.w3c.dom.HTMLDivElement

/**
 * A view controller class which renders the board to user.
 */
class CanvasViewController(
    lifecycleOwner: LifecycleOwner,
    private val container: HTMLDivElement,
    axisContainer: HTMLDivElement,
    board: MonoBoard,
    windowSizeLiveData: LiveData<Size>,
    shiftKeyStateLiveData: LiveData<Boolean>
) {
    private val drawingInfoController = DrawingInfoController(container)

    private val gridCanvasViewController: GridCanvasViewController
    private val boardCanvasViewController: BoardCanvasViewController
    private val interactionCanvasViewController: InteractionCanvasViewController
    private val selectionCanvasViewController: SelectionCanvasViewController

    val windowBoundPx: Rect
        get() = gridCanvasViewController.drawingInfo.boundPx

    private val windowBoardBoundMediatorLiveData: MediatorLiveData<Rect> =
        MediatorLiveData(Rect.ZERO)
    val windowBoardBoundLiveData: LiveData<Rect> = windowBoardBoundMediatorLiveData

    private val drawingInfo: DrawingInfoController.DrawingInfo
        get() = drawingInfoController.drawingInfoLiveData.value

    private val mouseEventController = MouseEventObserver(
        lifecycleOwner,
        container,
        drawingInfoController.drawingInfoLiveData,
        shiftKeyStateLiveData
    )

    val mousePointerLiveData: LiveData<MousePointer> = mouseEventController.mousePointerLiveData
    val drawingOffsetPointPxLiveData: LiveData<Point> =
        mouseEventController.drawingOffsetPointPxLiveData

    init {
        val drawingInfoLiveData = drawingInfoController.drawingInfoLiveData

        mouseEventController.drawingOffsetPointPxLiveData.observe(
            lifecycleOwner,
            throttleDurationMillis = 0,
            listener = drawingInfoController::setOffset
        )

        container.addClass("top-divider")

        gridCanvasViewController = GridCanvasViewController(
            lifecycleOwner,
            Canvas(container, CLASS_NAME_GRID),
            drawingInfoLiveData
        )
        boardCanvasViewController = BoardCanvasViewController(
            lifecycleOwner,
            Canvas(container, CLASS_NAME_BOARD),
            board,
            drawingInfoLiveData
        )
        interactionCanvasViewController = InteractionCanvasViewController(
            lifecycleOwner,
            Canvas(container, CLASS_NAME_INTERACTION),
            drawingInfoLiveData,
            mouseEventController.mousePointerLiveData
        )
        selectionCanvasViewController = SelectionCanvasViewController(
            lifecycleOwner,
            Canvas(container, CLASS_NAME_SELECTION),
            drawingInfoLiveData
        )
        AxisCanvasViewController(
            lifecycleOwner,
            axisContainer,
            drawingInfoLiveData
        ) {
            mouseEventController.forceUpdateOffset(Point.ZERO)
        }

        windowSizeLiveData.distinctUntilChange().observe(lifecycleOwner) {
            updateCanvasSize()
        }
        windowBoardBoundMediatorLiveData.add(drawingInfoLiveData) { value = it.boardBound }
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

    fun setOffset(offsetPx: Point) {
        mouseEventController.forceUpdateOffset(offsetPx)
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

    fun setMouseCursor(mouseCursor: MouseCursor) {
        container.style.cursor = mouseCursor.value
    }

    fun toXPx(column: Double): Double = drawingInfo.toXPx(column)

    fun toYPx(row: Double): Double = drawingInfo.toYPx(row)

    fun toWidthPx(width: Double) = drawingInfo.toWidthPx(width)

    fun toHeightPx(height: Double) = drawingInfo.toHeightPx(height)

    companion object {
        private const val CLASS_NAME_GRID = "grid-canvas"
        private const val CLASS_NAME_BOARD = "board-canvas"
        private const val CLASS_NAME_INTERACTION = "interaction-canvas"
        private const val CLASS_NAME_SELECTION = "selection-canvas"
    }
}
