/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar

import kotlinx.browser.document
import mono.actionmanager.ActionManager
import mono.html.select
import mono.html.toolbar.view.nav.MouseActionGroup
import mono.html.toolbar.view.nav.RightToolbar
import mono.lifecycle.LifecycleOwner
import mono.ui.appstate.AppUiStateManager

/**
 * A view controller to manage toolbar.
 */
class NavBarViewController(
    lifecycleOwner: LifecycleOwner,
    appUiStateManager: AppUiStateManager,
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
            RightToolbar(
                lifecycleOwner,
                appUiStateManager,
                actionManager::setOneTimeAction
            )
        }
    }
}
