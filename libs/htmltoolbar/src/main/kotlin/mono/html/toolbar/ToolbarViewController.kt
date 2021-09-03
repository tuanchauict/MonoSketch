package mono.html.toolbar

import mono.html.toolbar.view.MiddleToolbar
import mono.html.toolbar.view.RightToolbar
import mono.lifecycle.LifecycleOwner
import org.w3c.dom.HTMLDivElement

/**
 * A view controller to manage toolbar.
 */
class ToolbarViewController(
    lifecycleOwner: LifecycleOwner,
    toolbarContainer: HTMLDivElement,
    private val actionManager: ActionManager
) {
    init {

        with(toolbarContainer) {
            MiddleToolbar(
                lifecycleOwner,
                actionManager.retainableActionLiveData,
                actionManager::setRetainableAction
            )
            RightToolbar {
                actionManager.setOneTimeAction(it.actionType)
            }
        }
    }
}
