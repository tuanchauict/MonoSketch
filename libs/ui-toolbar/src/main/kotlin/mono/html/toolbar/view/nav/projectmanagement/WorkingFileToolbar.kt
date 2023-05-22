/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav.projectmanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.browser.document
import mono.actionmanager.OneTimeActionType
import mono.html.modal.DropDownMenu
import mono.html.toolbar.view.nav.projectmanagement.DropDownItem.Forwarding
import mono.html.toolbar.view.nav.projectmanagement.DropDownItem.NewProject
import mono.html.toolbar.view.nav.projectmanagement.DropDownItem.Rename
import mono.ui.compose.components.Icons
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.Element

private const val WORKING_PROJECT_ID = "working-project"

@Composable
internal fun WorkingFileToolbar(
    projectNameState: State<String>,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    CurrentProject(projectNameState.value) { element ->
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
private fun CurrentProject(title: String, showProjectMenu: (Element) -> Unit) {
    Div(
        attrs = {
            classes("working-file-container")
        }
    ) {
        Div(attrs = {
            id(WORKING_PROJECT_ID)
            classes("file-info-container")
        }) {
            Div(
                attrs = {
                    classes("file-info")

                    onClick {
                        val anchor = document.querySelector("#$WORKING_PROJECT_ID")
                        if (anchor != null) {
                            showProjectMenu(anchor)
                        }
                    }
                }
            ) {
                Span(attrs = { classes("title") }) {
                    Text(title)
                }

                Div(attrs = { classes("menu-down-icon") }) {
                    Icons.ChevronDown(12)
                }
            }
        }
    }
}

private fun showWorkingFileMenu(anchor: Element, onItemSelected: (DropDownItem) -> Unit) {
    val items = listOf(
        DropDownMenu.Item.Text("Rename", Rename),
        DropDownMenu.Item.Text("Save As...", Forwarding(OneTimeActionType.SaveShapesAs)),
        DropDownMenu.Item.Text("Export Text", Forwarding(OneTimeActionType.ExportSelectedShapes))
    )
    DropDownMenu(items) {
        val textItem = it as DropDownMenu.Item.Text
        onItemSelected(textItem.key as DropDownItem)
    }.show(anchor)
}

internal fun renameProject(
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
