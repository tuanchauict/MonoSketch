/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.app

import kotlinx.browser.document
import kotlinx.browser.window
import mono.actionmanager.ActionManager
import mono.bitmap.manager.MonoBitmapManager
import mono.graphics.board.MonoBoard
import mono.graphics.geo.Size
import mono.html.canvas.CanvasViewController
import mono.html.toolbar.NavBarViewController
import mono.html.toolbar.ShapeToolViewController
import mono.keycommand.KeyCommand
import mono.keycommand.KeyCommandController
import mono.lifecycle.LifecycleOwner
import mono.livedata.map
import mono.shape.ShapeManager
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.selection.SelectedShapeManager
import mono.state.MainStateManager
import mono.ui.appstate.AppUiStateManager
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.get
import org.w3c.dom.url.URLSearchParams

/**
 * Main class of the app to handle all kinds of events, UI, actions, etc.
 */
class MonoSketchApplication : LifecycleOwner() {
    private val model = MonoSketchAppModel()

    private val mainBoard = MonoBoard()
    private val shapeManager = ShapeManager()
    private val selectedShapeManager = SelectedShapeManager()
    private val bitmapManager = MonoBitmapManager()

    // Init AppUiStateManager here to apply theme as soon as possible.
    private val appUiStateManager = AppUiStateManager(this)

    private var mainStateManager: MainStateManager? = null

    /**
     * The entry point for all actions. This is called after window is loaded (`window.onload`)
     */
    override fun onStartInternal() {
        val body = document.body ?: return

        val boardCanvasContainer =
            document.getElementById(CONTAINER_ID) as? HTMLDivElement ?: return
        val axisCanvasContainer =
            document.getElementById(AXIS_CONTAINER_ID) as? HTMLDivElement ?: return

        val keyCommandController = KeyCommandController(body)

        val canvasViewController = CanvasViewController(
            this,
            boardCanvasContainer,
            axisCanvasContainer,
            mainBoard,
            model.windowSizeLiveData,
            keyCommandController.keyCommandLiveData.map { it == KeyCommand.SHIFT_KEY },
            appUiStateManager.scrollModeLiveData
        )

        val actionManager = ActionManager(this, keyCommandController.keyCommandLiveData)
        actionManager.installDebugCommand()

        mainStateManager = MainStateManager(
            this,
            mainBoard,
            shapeManager,
            selectedShapeManager,
            bitmapManager,
            canvasViewController,
            ShapeClipboardManager(body),
            canvasViewController.mousePointerLiveData,
            actionManager,
            appUiStateManager,
            initialRootId = getInitialRootIdFromUrl()
        )

        NavBarViewController(
            this,
            appUiStateManager,
            actionManager
        )
        ShapeToolViewController(
            this,
            document.getElementById("shape-tools") as HTMLElement,
            actionManager,
            selectedShapeManager.selectedShapesLiveData,
            shapeManager.versionLiveData,
            appUiStateManager.shapeToolVisibilityLiveData
        )
        onResize()
        observeAppActivateState()

        appUiStateManager.observeTheme(
            document.documentElement!!,
            mainStateManager!!::forceFullyRedrawWorkspace
        )
    }

    fun onResize() {
        val body = document.body ?: return
        val newSize = Size(body.clientWidth, body.clientHeight)
        model.setWindowSize(newSize)
    }

    private fun observeAppActivateState() {
        val callback = object : EventListener {
            override fun handleEvent(event: Event) {
                val isAppActive = document["visibilityState"] == "visible"
                model.setApplicationActiveState(isAppActive)
            }
        }
        document.addEventListener("visibilitychange", callback)
    }

    private fun getInitialRootIdFromUrl(): String {
        val urlParams = URLSearchParams(window.location.search)
        return urlParams.get(URL_PARAM_ID).orEmpty()
    }

    companion object {
        private const val CONTAINER_ID = "monoboard-canvas-container"
        private const val AXIS_CONTAINER_ID = "monoboard-axis-container"

        private const val URL_PARAM_ID = "id"
    }
}
