/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view

import androidx.compose.runtime.State
import mono.actionmanager.ActionManager
import mono.actionmanager.RetainableActionType
import mono.html.toolbar.view.nav.AppMenuIcon
import mono.html.toolbar.view.nav.MouseActionGroup
import mono.html.toolbar.view.nav.ProjectManagerIcon
import mono.html.toolbar.view.nav.ScrollModeButton
import mono.html.toolbar.view.nav.ThemeIcons
import mono.html.toolbar.view.nav.ToolbarContainer
import mono.html.toolbar.view.nav.WorkingFileToolbar
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.combineLiveData
import mono.store.dao.workspace.WorkspaceDao
import mono.ui.appstate.AppUiStateManager
import mono.ui.appstate.state.ScrollMode
import mono.ui.compose.ext.toState
import org.jetbrains.compose.web.dom.Div
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

    private val scrollModeState: State<ScrollMode> =
        appUiStateManager.scrollModeLiveData.toState(lifecycleOwner)

    init {
        val currentProjectState = currentRootIdLiveData.toState(lifecycleOwner)
        renderComposable("nav-toolbar") {
            Div {
                MouseActionGroup(selectedMouseActionState, actionManager::setRetainableAction)
            }

            Div {
                ToolbarContainer {
                    ProjectManagerIcon(
                        currentProjectState.value,
                        projectNameState,
                        workspaceDao,
                        actionManager::setOneTimeAction
                    )
                }
                WorkingFileToolbar(projectNameState) {
                    actionManager.setOneTimeAction(it)
                    // Notify the change in storage
                    // Note: This won't work if updating the name is done concurrently
                    environmentUpdateLiveData.value = Unit
                }
            }

            ToolbarContainer {
                ScrollModeButton(scrollModeState.value, appUiStateManager::updateUiState)
                ThemeIcons()
                AppMenuIcon(appUiStateManager, actionManager::setOneTimeAction)
            }
        }
    }
}
