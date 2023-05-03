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
import mono.html.toolbar.view.nav.DropDownItem.Forwarding
import mono.html.toolbar.view.nav.DropDownItem.ManageProject
import mono.html.toolbar.view.nav.DropDownItem.Rename
import mono.html.toolbar.view.nav.workingfile.RenameProjectModal
import mono.store.dao.workspace.WorkspaceDao
import mono.ui.compose.components.IconChevronDown
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.Element

@Composable
internal fun WorkingFileToolbar(
    projectNameState: State<String>,
    workspaceDao: WorkspaceDao,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    CurrentProject(projectNameState.value) { element ->
        showWorkingFileMenu(element) {
            when (it) {
                is Forwarding -> onActionSelected(it.actionType)

                Rename ->
                    RenameProjectModal { newName ->
                        if (newName.isNotEmpty()) {
                            onActionSelected(OneTimeActionType.RenameCurrentProject(newName))
                        }
                    }.show(projectNameState.value, element)

                ManageProject -> onManageProjectClick(workspaceDao, onActionSelected)
            }
        }
    }
}

@Composable
private fun CurrentProject(title: String, showProjectMenu: (Element) -> Unit) {
    Div(
        attrs = {
            classes("working-file-container")
        }
    ) {
        Div(attrs = { classes("divider") })

        Div(attrs = {
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
                IconChevronDown(12)
            }
        }
    }
}

private fun showWorkingFileMenu(anchor: Element, onItemSelected: (DropDownItem) -> Unit) {
    val items = listOf(
        DropDownMenu.Item.Text("New project", Forwarding(OneTimeActionType.NewProject)),
        DropDownMenu.Item.Text("Manage projects", ManageProject),
        DropDownMenu.Item.Divider(),
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

/**
 * A sealed interface for dropdown menu items of working file.
 */
private sealed interface DropDownItem {
    class Forwarding(val actionType: OneTimeActionType) : DropDownItem
    object Rename : DropDownItem
    object ManageProject : DropDownItem
}
