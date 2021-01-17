package mono.app

import kotlinx.browser.document
import mono.graphics.board.MonoBoard
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import org.w3c.dom.HTMLDivElement

/**
 * Main class of the app to handle all kinds of events, UI, actions, etc.
 */
class MonoFlowApplication {
    private var canvasViewController: CanvasViewController? = null
    private val monoBoard: MonoBoard = MonoBoard().apply {
        // TODO: This is for testing. Remove then.
        fill(Rect.byLTWH(1, 1, 10, 10), '|')
        fill(Rect.byLTWH(50, 15, 10, 10), '▒')
        fill(Rect.byLTWH(55, 10, 10, 10), '█')
    }

    /**
     * The entry point for all actions. This is called after window is loaded (`window.onload`)
     */
    fun onStart() {
        val boardCanvasContainer =
            document.getElementById(CONTAINER_ID) as? HTMLDivElement
                ?: return
        canvasViewController = CanvasViewController(boardCanvasContainer, monoBoard)
        onResize()
    }

    fun onResize() {
        canvasViewController?.onResize()
    }

    companion object {
        private const val CONTAINER_ID = "monoboard-canvas-container"
    }
}
