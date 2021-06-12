package mono.html.toolbar

import kotlinx.html.dom.append
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
        toolbarContainer.append {
            MiddleToolbar(
                lifecycleOwner,
                actionManager.retainableActionLiveData,
                actionManager::setRetainableAction
            )
            RightToolbar {
                actionManager.setOneTimeAction(OneTimeActionType.EXPORT_SELECTED_SHAPES)
            }
        }
    }
}
