/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import mono.actionmanager.OneTimeActionType
import mono.html.Div
import mono.html.Span
import mono.html.SvgIcon
import mono.html.modal.DropDownMenu
import mono.html.modal.compose.ProjectItem
import mono.html.modal.compose.showRecentProjectsModal
import mono.html.setOnClickListener
import mono.html.toolbar.view.nav.DropDownItem.Forwarding
import mono.html.toolbar.view.nav.DropDownItem.ManageProject
import mono.html.toolbar.view.nav.DropDownItem.Rename
import mono.html.toolbar.view.nav.workingfile.RenameProjectModal
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.store.dao.workspace.WorkspaceDao
import org.w3c.dom.Element

internal fun Element.WorkingFileToolbar(
    lifecycleOwner: LifecycleOwner,
    workspaceDao: WorkspaceDao,
    filenameLiveData: LiveData<String>,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    Div("working-file-container") {
        Div("divider")

        val fileInfo = Div("file-info")
        val fileName = fileInfo.Span("title") {
            filenameLiveData.observe(lifecycleOwner) {
                innerText = it
            }
        }
        fileInfo.Div("menu-down-icon") {
            SvgIcon(
                width = 12,
                height = 12,
                viewPortWidth = 16,
                viewPortHeight = 16,
                "M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z" // ktlint-disable max-line-length
            )
        }

        fileInfo.setOnClickListener {
            showWorkingFileMenu(fileInfo) {
                when (it) {
                    is Forwarding -> onActionSelected(it.actionType)
                    Rename -> RenameProjectModal(fileName, onActionSelected).show(fileInfo)
                    ManageProject -> onManageProjectClick(workspaceDao, onActionSelected)
                }
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
            // TODO: Remove action
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
