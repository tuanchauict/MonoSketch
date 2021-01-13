package mono.html.canvas

import kotlinx.html.dom.append
import kotlinx.html.js.canvas
import org.w3c.dom.HTMLDivElement

/**
 * A view controller class which renders the board to user.
 */
class CanvasViewController(private val container: HTMLDivElement) {
    private lateinit var gridCanvasViewController: GridCanvasViewController
    private lateinit var boardCanvasViewController: BoardCanvasViewController

    init {
        container.append {
            val gridCanvas = canvas {
                attributes["style"] = "width: 100%; height: 100%"
            }
            gridCanvasViewController = GridCanvasViewController(gridCanvas)

            val boardCanvas = canvas {
                attributes["style"] = "width: 100%; height: 100%"
            }
            boardCanvasViewController = BoardCanvasViewController(boardCanvas)
        }
    }
}
