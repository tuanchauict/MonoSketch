package mono.app

import kotlinx.browser.document
import mono.html.canvas.CanvasViewController
import org.w3c.dom.HTMLDivElement

/**
 * Main class of the app to handle all kinds of events, UI, actions, etc.
 */
class MonoFlowApplication {
    private var canvasViewController: CanvasViewController? = null
    /**
     * The entry point for all actions. This is called after window is loaded (`window.onload`)
     */
    fun onStart() {
        val boardCanvasContainer =
            document.getElementById("monoboard-canvas-container") as? HTMLDivElement
                ?: return
        canvasViewController = CanvasViewController(boardCanvasContainer)
    }

    fun onResize() {
        canvasViewController?.onResize()
    }
}
