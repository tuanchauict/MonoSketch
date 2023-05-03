/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar

import androidx.compose.runtime.State
import kotlinx.browser.document
import mono.actionmanager.ActionManager
import mono.actionmanager.RetainableActionType
import mono.html.Div
import mono.html.select
import mono.html.toolbar.view.nav.MouseActionGroup
import mono.html.toolbar.view.nav.RightToolbar
import mono.html.toolbar.view.nav.WorkingFileToolbar
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.combineLiveData
import mono.store.dao.workspace.WorkspaceDao
import mono.ui.appstate.AppUiStateManager
import mono.ui.compose.ext.toState
import org.jetbrains.compose.web.renderComposable

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
    // A live data for updating the UI due to the change in the nav bar's actions
    private val environmentUpdateLiveData = MutableLiveData(Unit)
    private val projectNameState: State<String> = combineLiveData(
        applicationActiveStateLiveData,
        environmentUpdateLiveData,
        currentRootIdLiveData
    ) { (_, _, rootId) -> workspaceDao.getObject(rootId as String).name }
        .toState(lifecycleOwner)

    private val selectedMouseActionState: State<RetainableActionType> =
        actionManager.retainableActionLiveData.toState(lifecycleOwner)

    init {
        document.select("#nav-toolbar").run {
            Div("left-toolbar-container") {
                renderComposable(this) {
                    WorkingFileToolbar(projectNameState, workspaceDao) {
                        actionManager.setOneTimeAction(it)
                        // Notify the change in storage
                        // Note: This won't work if updating the name is done concurrently
                        environmentUpdateLiveData.value = Unit
                    }
                }
            }
            Div("middle-toolbar-container") {
                renderComposable(this) {
                    MouseActionGroup(selectedMouseActionState, actionManager::setRetainableAction)
                }
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
