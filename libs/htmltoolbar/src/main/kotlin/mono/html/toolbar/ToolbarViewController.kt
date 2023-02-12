package mono.html.toolbar

import kotlinx.browser.document
import mono.actionmanager.ActionManager
import mono.html.select
import mono.html.toolbar.view.MouseActionGroup
import mono.html.toolbar.view.RightToolbar
import mono.lifecycle.LifecycleOwner

/**
 * A view controller to manage toolbar.
 */
class ToolbarViewController(
    lifecycleOwner: LifecycleOwner,
    private val actionManager: ActionManager
) {
    init {
        with(document.select("#nav-toolbar-center")) {
            MouseActionGroup(
                lifecycleOwner,
                actionManager.retainableActionLiveData,
                actionManager::setRetainableAction
            )
        }
        with(document.select("#nav-toolbar-right")) {
            RightToolbar(actionManager::setOneTimeAction)
        }
    }
}
