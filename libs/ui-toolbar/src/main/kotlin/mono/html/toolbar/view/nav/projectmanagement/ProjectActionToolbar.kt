/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav.projectmanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import mono.actionmanager.OneTimeActionType
import mono.html.modal.compose.ProjectItem
import mono.html.modal.compose.ProjectManagementActionItem
import mono.html.modal.compose.showRecentProjectsModal
import mono.html.modal.tooltip
import mono.store.dao.workspace.WorkspaceDao
import mono.ui.compose.components.Icons
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.Div

@Composable
internal fun ProjectManagerIcon(
    openingProjectId: String,
    projectNameState: State<String>,
    workspaceDao: WorkspaceDao,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    Div(
        attrs = {
            classes("app-icon-container")
        }
    ) {
        Div(
            attrs = {
                classes("app-icon")
                style {
                    color(Color("#f0f0f0"))
                }
                tooltip("Manage projects")

                onClick {
                    onManageProjectClick(
                        openingProjectId,
                        workspaceDao,
                        projectNameState,
                        onActionSelected
                    )
                }
            }
        ) {
            Icons.Inbox(iconSize = 18)
        }
    }
}

private fun onManageProjectClick(
    openingProjectId: String,
    workspaceDao: WorkspaceDao,
    projectNameState: State<String>,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    val projects = workspaceDao.getObjects()
        .map { ProjectItem(it.objectId, it.name, it.objectId == openingProjectId) }
        .toList()
    showRecentProjectsModal(
        projects,
        onManagementAction = {
            onProjectManagementActionClick(
                it,
                projectNameState,
                onActionSelected
            )
        },
        onProjectSelect = { projectItem, isRemoved ->
            onProjectSelectionActionClick(projectItem, isRemoved, onActionSelected)
        }
    )
}

private fun onProjectManagementActionClick(
    actionItem: ProjectManagementActionItem,
    projectNameState: State<String>,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    when (actionItem) {
        ProjectManagementActionItem.ImportFile ->
            onActionSelected(OneTimeActionType.ProjectAction.OpenShapes)

        ProjectManagementActionItem.NewProject -> {
            onActionSelected(OneTimeActionType.ProjectAction.NewProject)
            renameProject(projectNameState, onActionSelected)
        }
    }
}

private fun onProjectSelectionActionClick(
    projectItem: ProjectItem,
    isRemoved: Boolean,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    if (isRemoved) {
        onActionSelected(OneTimeActionType.ProjectAction.RemoveProject(projectItem.id))
    } else {
        onActionSelected(OneTimeActionType.ProjectAction.SwitchProject(projectItem.id))
    }
}
