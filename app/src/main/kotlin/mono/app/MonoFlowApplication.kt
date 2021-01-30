package mono.app

import kotlinx.browser.document
import mono.graphics.bitmap.MonoBitmapManager
import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import mono.html.canvas.CanvasViewController
import mono.lifecycle.LifecycleOwner
import mono.shape.ShapeManager
import mono.state.MainStateManager
import org.w3c.dom.HTMLDivElement

/**
 * Main class of the app to handle all kinds of events, UI, actions, etc.
 */
class MonoFlowApplication : LifecycleOwner() {
    private val model: MonoFlowAppModel = MonoFlowAppModel()

    private val mainBoard: MonoBoard = MonoBoard()
    private val shapeManager = ShapeManager()
    private val bitmapManager = MonoBitmapManager()
    private var mainStateManager: MainStateManager? = null

    /**
     * The entry point for all actions. This is called after window is loaded (`window.onload`)
     */
    override fun onStartInternal() {
        val boardCanvasContainer =
            document.getElementById(CONTAINER_ID) as? HTMLDivElement ?: return

        val canvasViewController = CanvasViewController(
            this,
            boardCanvasContainer,
            mainBoard,
            model.windowSizeLiveData
        )

        mainStateManager = MainStateManager(
            this,
            mainBoard,
            shapeManager,
            bitmapManager,
            canvasViewController
        )

        onResize()
    }

    fun onResize() {
        val body = document.body ?: return
        val newSize = Size(body.clientWidth, body.clientHeight)
        model.setWindowSize(newSize)
    }

    companion object {
        private const val CONTAINER_ID = "monoboard-canvas-container"
    }
}
