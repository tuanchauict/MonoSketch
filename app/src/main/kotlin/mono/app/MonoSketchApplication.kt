package mono.app

import kotlinx.browser.document
import kotlinx.browser.localStorage
import mono.bitmap.manager.MonoBitmapManager
import mono.common.setTimeout
import mono.graphics.board.MonoBoard
import mono.graphics.geo.Size
import mono.html.canvas.CanvasViewController
import mono.html.toolbar.ActionManager
import mono.html.toolbar.ToolbarViewController
import mono.html.toolbar.view.shapetool.ShapeToolViewController
import mono.keycommand.KeyCommandController
import mono.lifecycle.LifecycleOwner
import mono.livedata.distinctUntilChange
import mono.shape.ShapeManager
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.selection.SelectedShapeManager
import mono.shape.serialization.SerializableGroup
import mono.shape.serialization.ShapeSerializationUtil
import mono.shape.shape.Group
import mono.state.MainStateManager
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * Main class of the app to handle all kinds of events, UI, actions, etc.
 */
class MonoSketchApplication : LifecycleOwner() {
    private val model = MonoSketchAppModel()

    private val mainBoard = MonoBoard()
    private val shapeManager = ShapeManager()
    private val selectedShapeManager = SelectedShapeManager()
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
        val axisCanvasContainer =
            document.getElementById(AXIS_CONTAINER_ID) as? HTMLDivElement ?: return

        val canvasViewController = CanvasViewController(
            this,
            boardCanvasContainer,
            axisCanvasContainer,
            mainBoard,
            model.windowSizeLiveData
        )
        val keyCommandController = KeyCommandController(body)

        val actionManager = ActionManager(this, keyCommandController.keyCommandLiveData)

        mainStateManager = MainStateManager(
            this,
            mainBoard,
            shapeManager,
            selectedShapeManager,
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
        ShapeToolViewController(
            this,
            document.getElementById("shape-tools") as HTMLElement,
            actionManager,
            selectedShapeManager.selectedShapesLiveData,
            shapeManager.versionLiveData
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
        val serializableRoot = shapeManager.root.toSerializableShape(true)
        localStorage[BACKUP_SHAPES_KEY] = ShapeSerializationUtil.toJson(serializableRoot)
    }

    private fun restoreShapes() {
        val backedUpJson = localStorage[BACKUP_SHAPES_KEY] ?: return
        val serializableRoot = ShapeSerializationUtil.fromJson(backedUpJson) as? SerializableGroup
        if (serializableRoot == null) {
            // Wipe local data with current shapes.
            backupShapes()
            return
        }
        val root = Group(serializableRoot, parentId = null)
        shapeManager.replaceRoot(root)
    }

    companion object {
        private const val CONTAINER_ID = "monoboard-canvas-container"
        private const val AXIS_CONTAINER_ID = "monoboard-axis-container"
        private const val BACKUP_SHAPES_KEY = "backup-shapes"
    }
}
