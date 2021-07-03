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
                actionManager.setOneTimeAction(it.actionType)
            }
        }
    }

    enum class RightAction(val title: String, val actionType: OneTimeActionType) {
        SAVE_AS("Save As...", OneTimeActionType.SAVE_SHAPES_AS),
        OPEN_FILE("Open File...", OneTimeActionType.OPEN_SHAPES),
        EXPORT("Export", OneTimeActionType.EXPORT_SELECTED_SHAPES)
    }
}
