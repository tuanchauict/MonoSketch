/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar

import kotlinx.browser.document
import mono.actionmanager.ActionManager
import mono.html.Div
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
        document.select("#nav-toolbar").run {
            Div("left-toolbar-container") {

            }
            Div("middle-toolbar-container") {
                MouseActionGroup(
                    lifecycleOwner,
                    actionManager.retainableActionLiveData,
                    actionManager::setRetainableAction
                )
            }
            Div("right-toolbar-container") {
                RightToolbar(
                    lifecycleOwner,
                    appUiStateManager,
                    actionManager::setOneTimeAction
                )
            }
        }
    }
}
