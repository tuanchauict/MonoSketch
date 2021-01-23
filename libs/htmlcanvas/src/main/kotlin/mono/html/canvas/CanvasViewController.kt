package mono.html.canvas

import kotlinx.html.dom.append
import kotlinx.html.js.canvas
import mono.graphics.board.MonoBoard
import mono.graphics.geo.Size
import mono.html.canvas.canvas.BaseCanvasViewController
import mono.html.canvas.canvas.BoardCanvasViewController
import mono.html.canvas.canvas.GridCanvasViewController
import mono.html.canvas.mouse.MouseEventObserver
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
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

    private lateinit var canvasControllers: List<BaseCanvasViewController>

    init {
        container.append {
            val gridCanvas = canvas {}
            val boardCanvas = canvas {}

            gridCanvasViewController = GridCanvasViewController(gridCanvas)
            boardCanvasViewController = BoardCanvasViewController(boardCanvas, board)
            canvasControllers = listOf(
                gridCanvasViewController,
                boardCanvasViewController
            )
        }

        val mouseEventController = MouseEventObserver(
            container,
            gridCanvasViewController::toBoardColumn,
            gridCanvasViewController::toBoardRow
        )
        mouseEventController.mousePointerLiveData.observe(lifecycleOwner, isDistinct = true) {
            println(it)
        }

        windowSizeLiveData.observe(lifecycleOwner, isDistinct = true) {
            updateCanvasSize()
        }
    }

    private fun updateCanvasSize() {
        val widthPx = container.clientWidth
        val heightPx = container.clientHeight
        for (controller in canvasControllers) {
            controller.setSizeAndRedraw(widthPx, heightPx)
        }
    }
}
