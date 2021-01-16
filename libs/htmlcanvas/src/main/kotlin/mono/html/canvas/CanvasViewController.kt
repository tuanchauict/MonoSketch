package mono.html.canvas

import kotlinx.html.dom.append
import kotlinx.html.js.canvas
import mono.html.canvas.canvas.BaseCanvasViewController
import mono.html.canvas.canvas.BoardCanvasViewController
import mono.html.canvas.canvas.GridCanvasViewController
import org.w3c.dom.HTMLDivElement

/**
 * A view controller class which renders the board to user.
 */
class CanvasViewController(private val container: HTMLDivElement) {
    private lateinit var gridCanvasViewController: GridCanvasViewController
    private lateinit var boardCanvasViewController: BoardCanvasViewController

    private lateinit var canvasControllers: List<BaseCanvasViewController>

    init {
        container.append {
            val gridCanvas = canvas {
                attributes["style"] = "position: absolute; top: 0; left: 0"
            }
            val boardCanvas = canvas {
                attributes["style"] = "position: absolute; top: 0; left: 0"
            }

            gridCanvasViewController = GridCanvasViewController(gridCanvas)
            boardCanvasViewController = BoardCanvasViewController(boardCanvas)
            canvasControllers = listOf(
                gridCanvasViewController,
                boardCanvasViewController
            )
        }

        onResize()
    }

    fun onResize() {
        val widthPx = container.clientWidth
        val heightPx = container.clientHeight
        for (controller in canvasControllers) {
            controller.setSize(widthPx, heightPx)
        }
        forceRedraw()
    }

    private fun forceRedraw() {
        gridCanvasViewController.draw()
    }
}
