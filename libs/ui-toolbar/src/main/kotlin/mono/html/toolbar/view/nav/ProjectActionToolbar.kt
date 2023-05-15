/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import androidx.compose.runtime.Composable
import mono.actionmanager.OneTimeActionType
import mono.html.modal.compose.ProjectItem
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

                onClick { onManageProjectClick(openingProjectId, workspaceDao, onActionSelected) }
            }
        ) {
            Icons.Inbox(iconSize = 18)
        }
    }
}

private fun onManageProjectClick(
    openingProjectId: String,
    workspaceDao: WorkspaceDao,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    val projects = workspaceDao.getObjects()
        .map { ProjectItem(it.objectId, it.name, it.objectId == openingProjectId) }
        .toList()
    showRecentProjectsModal(projects) { projectItem, isRemoved ->
        if (isRemoved) {
            onActionSelected(OneTimeActionType.RemoveProject(projectItem.id))
        } else {
            onActionSelected(OneTimeActionType.SwitchProject(projectItem.id))
        }
    }
}
