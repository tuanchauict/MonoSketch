package mono.app

import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mono.common.setTimeout
import mono.graphics.bitmap.MonoBitmapManager
import mono.graphics.board.MonoBoard
import mono.graphics.geo.Size
import mono.html.canvas.CanvasViewController
import mono.keycommand.KeyCommandController
import mono.lifecycle.LifecycleOwner
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.serialization.AbstractSerializableShape
import mono.shape.serialization.SerializableGroup
import mono.shape.shape.Group
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

        mainStateManager = MainStateManager(
            this,
            mainBoard,
            shapeManager,
            bitmapManager,
            canvasViewController,
            keyCommandController.keyCommandLiveData,
            canvasViewController.mousePointerLiveData
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
        val json = Json.encodeToString(shapeManager.root.toSerializableShape())
        localStorage[BACKUP_SHAPES_KEY] = json
    }

    private fun restoreShapes() {
        val backedUpJson = localStorage[BACKUP_SHAPES_KEY] ?: return

        try {
            val serializableGroup =
                Json.decodeFromString<AbstractSerializableShape>(backedUpJson) as SerializableGroup
            val root = Group(serializableGroup, 0)
            shapeManager.replaceRoot(root)
        } catch (e: Exception) {
            console.error("Error while restoring shapes")
            console.error(e)

            // Wipe local data with current shapes.
            backupShapes()
        }
    }

    companion object {
        private const val CONTAINER_ID = "monoboard-canvas-container"
        private const val BACKUP_SHAPES_KEY = "backup-shapes"
    }
}
