package mono.app

import kotlinx.browser.document
import kotlinx.browser.localStorage
import mono.common.setTimeout
import mono.graphics.bitmap.MonoBitmapManager
import mono.graphics.board.MonoBoard
import mono.graphics.geo.Size
import mono.html.canvas.CanvasViewController
import mono.html.toolbar.ActionManager
import mono.html.toolbar.ToolbarViewController
import mono.keycommand.KeyCommandController
import mono.lifecycle.LifecycleOwner
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.replaceWithJson
import mono.shape.toJson
import mono.state.MainStateManager
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * Main class of the app to handle all kinds of events, UI, actions, etc.
 */
class MonoFlowApplication : LifecycleOwner() {
    private val model: MonoFlowAppModel = MonoFlowAppModel()

    private val mainBoard: MonoBoard = MonoBoard()
    private val shapeManager = ShapeManager()
    private val bitmapManager = MonoBitmapManager()
    private var mainStateManager: MainStateManager? = null

    init {
        restoreShapes()

        shapeManager.versionLiveData.distinctUntilChange().observe(this, 500) {
            registerBackupShapes(it)
        }
    }

    /**
     * The entry point for all actions. This is called after window is loaded (`window.onload`)
     */
    override fun onStartInternal() {
        val body = document.body ?: return

        val boardCanvasContainer =
            document.getElementById(CONTAINER_ID) as? HTMLDivElement ?: return

        val canvasViewController = CanvasViewController(
            this,
            boardCanvasContainer,
            mainBoard,
            model.windowSizeLiveData
        )
        val keyCommandController = KeyCommandController(body)

        val actionManager = ActionManager(this, keyCommandController.keyCommandLiveData)

        mainStateManager = MainStateManager(
            this,
            mainBoard,
            shapeManager,
            bitmapManager,
            canvasViewController,
            ShapeClipboardManager(body),
            canvasViewController.mousePointerLiveData,
            actionManager
        )

        ToolbarViewController(
            this,
            document.getElementById("header-toolbar") as HTMLDivElement,
            actionManager
        )
        onResize()
    }

    fun onResize() {
        val body = document.body ?: return
        val newSize = Size(body.clientWidth, body.clientHeight)
        model.setWindowSize(newSize)
    }

    private fun registerBackupShapes(version: Int) {
        setTimeout(500) {
            // Only backup if the shape manager is idle.
            if (shapeManager.versionLiveData.value == version) {
                backupShapes()
            }
        }
    }

    private fun backupShapes() {
        localStorage[BACKUP_SHAPES_KEY] = shapeManager.toJson()
    }

    private fun restoreShapes() {
        val backedUpJson = localStorage[BACKUP_SHAPES_KEY] ?: return
        val isRestoreSuccessful = shapeManager.replaceWithJson(backedUpJson)
        if (!isRestoreSuccessful) {
            // Wipe local data with current shapes.
            backupShapes()
        }
    }

    companion object {
        private const val CONTAINER_ID = "monoboard-canvas-container"
        private const val BACKUP_SHAPES_KEY = "backup-shapes"
    }
}
