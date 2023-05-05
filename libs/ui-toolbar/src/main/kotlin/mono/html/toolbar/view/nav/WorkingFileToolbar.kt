/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import mono.actionmanager.OneTimeActionType
import mono.html.modal.DropDownMenu
import mono.html.modal.compose.ProjectItem
import mono.html.modal.compose.showRecentProjectsModal
import mono.html.modal.tooltip
import mono.html.toolbar.view.nav.DropDownItem.Forwarding
import mono.html.toolbar.view.nav.DropDownItem.NewProject
import mono.html.toolbar.view.nav.DropDownItem.Rename
import mono.html.toolbar.view.nav.workingfile.showRenameProjectModal
import mono.store.dao.workspace.WorkspaceDao
import mono.ui.compose.components.Icons
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.Element

private const val WORKING_PROJECT_ID = "working-project"

@Composable
internal fun WorkingFileToolbar(
    projectNameState: State<String>,
    workspaceDao: WorkspaceDao,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    CurrentProject(projectNameState.value, workspaceDao, onActionSelected) { element ->
        showWorkingFileMenu(element) {
            when (it) {
                is Forwarding -> onActionSelected(it.actionType)

                NewProject -> {
                    onActionSelected(OneTimeActionType.NewProject)
                    renameProject(projectNameState, onActionSelected)
                }

                Rename -> renameProject(projectNameState, onActionSelected)
            }
        }
    }
}

@Composable
private fun CurrentProject(
    title: String,
    workspaceDao: WorkspaceDao,
    onActionSelected: (OneTimeActionType) -> Unit,
    showProjectMenu: (Element) -> Unit
) {
    Div(
        attrs = {
            classes("working-file-container")
        }
    ) {
        Div(attrs = { classes("divider") })

        Div(attrs = {
            id(WORKING_PROJECT_ID)
            classes("file-info")

            onClick {
                val anchor = it.currentTarget?.unsafeCast<Element>()
                if (anchor != null) {
                    showProjectMenu(anchor)
                }
            }
        }) {
            Span(attrs = { classes("title") }) {
                Text(title)
            }

            Div(attrs = { classes("menu-down-icon") }) {
                Icons.ChevronDown(12)
            }
        }

        Toolbar(workspaceDao, onActionSelected)
    }
}

@Composable
private fun Toolbar(
    workspaceDao: WorkspaceDao,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    Div(
        attrs = {
            classes("toolbar-container")
        }
    ) {
        Div(
            attrs = {
                classes("app-icon-container")
            }
        ) {
            Div(
                attrs = {
                    classes("app-icon")
                    tooltip("Manage projects")

                    onClick { onManageProjectClick(workspaceDao, onActionSelected) }
                }
            ) {
                Icons.Inbox()
            }
        }
    }
}

private fun showWorkingFileMenu(anchor: Element, onItemSelected: (DropDownItem) -> Unit) {
    val items = listOf(
        DropDownMenu.Item.Text("New project", NewProject),
        DropDownMenu.Item.Text("Rename", Rename),
        DropDownMenu.Item.Text("Save As...", Forwarding(OneTimeActionType.SaveShapesAs)),
        DropDownMenu.Item.Text("Open File...", Forwarding(OneTimeActionType.OpenShapes)),
        DropDownMenu.Item.Text("Export Text", Forwarding(OneTimeActionType.ExportSelectedShapes))
    )
    DropDownMenu(items) {
        val textItem = it as DropDownMenu.Item.Text
        onItemSelected(textItem.key as DropDownItem)
    }.show(anchor)
}

private fun onManageProjectClick(
    workspaceDao: WorkspaceDao,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    val projects = workspaceDao.getObjects().map { ProjectItem(it.objectId, it.name) }.toList()
    showRecentProjectsModal(projects) { projectItem, isRemoved ->
        if (isRemoved) {
            onActionSelected(OneTimeActionType.RemoveProject(projectItem.id))
        } else {
            onActionSelected(OneTimeActionType.SwitchProject(projectItem.id))
        }
    }
}

private fun renameProject(
    projectNameState: State<String>,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    showRenameProjectModal(
        projectNameState.value,
        "#$WORKING_PROJECT_ID"
    ) { newName ->
        if (newName.isNotEmpty()) {
            onActionSelected(OneTimeActionType.RenameCurrentProject(newName))
        }
    }
}

/**
 * A sealed interface for dropdown menu items of working file.
 */
private sealed interface DropDownItem {
    class Forwarding(val actionType: OneTimeActionType) : DropDownItem
    object NewProject : DropDownItem
    object Rename : DropDownItem
}
