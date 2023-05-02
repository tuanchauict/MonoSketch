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
import mono.html.toolbar.view.nav.WorkingFileToolbar
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.store.dao.workspace.WorkspaceDao
import mono.ui.appstate.AppUiStateManager

/**
 * A view controller to manage toolbar.
 */
class NavBarViewController(
    lifecycleOwner: LifecycleOwner,
    applicationActiveStateLiveData: LiveData<Boolean>,
    currentRootIdLiveData: LiveData<String>,
    appUiStateManager: AppUiStateManager,
    private val actionManager: ActionManager,
    workspaceDao: WorkspaceDao = WorkspaceDao.instance
) {
    init {
        val fileNameLiveData =
            combineLiveData(applicationActiveStateLiveData, currentRootIdLiveData) { _, rootId ->
                workspaceDao.getObject(rootId).name
            }
        document.select("#nav-toolbar").run {
            Div("left-toolbar-container") {
                WorkingFileToolbar(
                    lifecycleOwner,
                    workspaceDao,
                    fileNameLiveData,
                    actionManager::setOneTimeAction
                )
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
