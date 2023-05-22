/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import mono.browser.manager.BrowserManager
import mono.common.Cancelable
import mono.common.setTimeout
import mono.html.modal.TooltipPosition
import mono.html.modal.tooltip
import mono.ui.compose.components.Icons
import mono.ui.compose.ext.classes
import mono.ui.compose.ext.onConsumeClick
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Element
import mono.html.Div as MonoDiv

private typealias ProjectManagementAction = (ProjectManagementActionItem) -> Unit
private typealias ProjectSelectAction = (ProjectItem, isRemoved: Boolean) -> Unit

class ProjectItem(val id: String, val name: String, val isOpening: Boolean)

fun showRecentProjectsModal(
    projectItems: List<ProjectItem>,
    onManagementAction: ProjectManagementAction,
    onProjectSelect: ProjectSelectAction
) {
    val body = document.body ?: return
    val container = body.MonoDiv()
    val composition = renderComposable(container) {}
    composition.setContent {
        RecentProjectsModal(projectItems, onManagementAction, onProjectSelect) {
            composition.dispose()
            container.remove()
        }
    }
}

@Composable
private fun RecentProjectsModal(
    projects: List<ProjectItem>,
    onManagementAction: ProjectManagementAction,
    onProjectSelect: ProjectSelectAction,
    onDismiss: () -> Unit
) {
    var cancelable: Cancelable? by remember { mutableStateOf(null) }
    Div(
        attrs = {
            classes("recent-project-modal")
            tabIndex(-1)

            onClick { onDismiss() }

            onFocusIn { cancelable?.cancel() }

            onFocusOut {
                // Only trigger dismiss when the window is active.
                if (document.hasFocus()) {
                    cancelable = setTimeout(20) {
                        onDismiss()
                    }
                }
            }

            onKeyDown {
                when (it.key) {
                    "Escape" -> onDismiss()
                    // TODO: Use ArrowDown and ArrowUp for changing the active project
                    // TODO: Use Enter for opening the project by keyboard
                }
            }
        }
    ) {
        Div(
            attrs = {
                classes("container")

                onConsumeClick {}
            }
        ) {
            val filter = remember { mutableStateOf("") }
            val requestingRemoveProjectId = remember { mutableStateOf("") }

            FilterInput {
                filter.value = it
                requestingRemoveProjectId.value = ""
            }
            ProjectManagementSection(filter.value.isNotEmpty()) {
                onManagementAction(it)
                onDismiss()
            }
            ProjectList(filter.value, projects, requestingRemoveProjectId.value) {
                when (it) {
                    is Action.Open -> {
                        if (it.withNewTab) {
                            BrowserManager.openInNewTab(it.project.id)
                        } else {
                            onProjectSelect(it.project, false)
                        }

                        onDismiss()
                    }

                    is Action.RemoveConfirm -> {
                        onProjectSelect(it.project, true)
                        onDismiss()
                    }

                    is Action.RequestRemove -> requestingRemoveProjectId.value = it.project.id
                    Action.SuspendRemove -> requestingRemoveProjectId.value = ""
                }
            }
        }
    }
}

@Composable
private fun FilterInput(onChange: (String) -> Unit) {
    Div(
        attrs = {
            classes("filter-container")
        }
    ) {
        Input(type = InputType.Text) {
            placeholder("Filter by name")

            onInput { onChange(it.value) }

            ref {
                it.focus()
                onDispose { }
            }
        }
    }
}

@Composable
private fun ProjectList(
    filter: String,
    projects: List<ProjectItem>,
    requestingRemoveProjectId: String,
    onAction: (Action) -> Unit
) {
    val filteredProjects = if (filter.isNotEmpty()) {
        projects
            .filter { it.name.contains(filter, ignoreCase = true) }
            .sortedBy { it.name }
    } else {
        projects
    }

    if (filteredProjects.isNotEmpty()) {
        Div(
            attrs = { classes("list") }
        ) {
            for (project in filteredProjects) {
                val isRemoveConfirming = project.id == requestingRemoveProjectId
                ProjectContent(project, isRemoveConfirming, onAction)
            }
        }
    } else {
        Div(
            attrs = { classes("no-item") }
        ) {
            Span {
                Text("Not found")
            }
        }
    }
}

@Composable
private fun ProjectContent(
    item: ProjectItem,
    isRemoveConfirming: Boolean,
    onAction: (Action) -> Unit
) {
    Div(
        attrs = {
            classes(
                "item" to true,
                "removing" to isRemoveConfirming,
                "normal" to !isRemoveConfirming
            )
            if (!isRemoveConfirming) {
                onConsumeClick { onAction(Action.Open(item, withNewTab = false)) }
            }
        }
    ) {
        if (isRemoveConfirming) {
            DeleteConfirmName(item.name)
        } else {
            ProjectName(item.name, item.isOpening)
        }
        Actions(isRemoveConfirming, item, onAction)
    }
}

@Composable
private fun DeleteConfirmName(name: String) {
    Div(attrs = { classes("name-container", "delete-confirm") }) {
        Icon { Icons.Warning() }
        Span(attrs = { classes("name") }) {
            Text("""Delete "$name"?""")
        }
    }
}

@Composable
private fun ProjectName(name: String, isOpening: Boolean) {
    Div(attrs = { classes("name-container") }) {
        Icon {
            if (isOpening) Icons.FolderOpen() else Icons.Folder()
        }
        Span(attrs = { classes("name") }) {
            Text(name)
        }
    }
}

@Composable
private fun Icon(icon: ContentBuilder<Element>) {
    Span(attrs = { classes("icon") }) { icon() }
}

@Composable
private fun Actions(
    isRemoveConfirming: Boolean,
    item: ProjectItem,
    onAction: (Action) -> Unit
) {
    Div(attrs = { classes("actions") }) {
        if (!isRemoveConfirming) {
            ActionOpenInNewTab(item, onAction)
            ActionRemoveProjectRequest(item, onAction)
        } else {
            ActionRemoveProjectConfirm(item, onAction)
        }
    }
}

@Composable
private fun ActionOpenInNewTab(project: ProjectItem, onAction: (Action) -> Unit) {
    Div(
        attrs = {
            classes("action")
            tooltip("Open in new tab", TooltipPosition.TOP)

            onConsumeClick {
                onAction(Action.Open(project, withNewTab = true))
            }
        }
    ) {
        Icons.OpenInNewTab(12)
    }
}

@Composable
private fun ActionRemoveProjectRequest(project: ProjectItem, onAction: (Action) -> Unit) {
    Div(
        attrs = {
            classes("action")
            tooltip("Delete", TooltipPosition.TOP)
            onConsumeClick { onAction(Action.RequestRemove(project)) }
        }
    ) {
        Icons.Remove(12)
    }
}

@Composable
private fun ActionRemoveProjectConfirm(item: ProjectItem, onAction: (Action) -> Unit) {
    Div(
        attrs = {
            classes("action")
            tooltip("Confirm", TooltipPosition.TOP)
            onConsumeClick {
                onAction(Action.RemoveConfirm(item))
            }
        }
    ) {
        Icons.Remove(12)
    }
    Div(
        attrs = {
            classes("action")
            tooltip("Cancel", TooltipPosition.TOP)
            onConsumeClick {
                onAction(Action.SuspendRemove)
            }
        }
    ) {
        Icons.Cross(12)
    }
}

private sealed class Action {
    data class Open(val project: ProjectItem, val withNewTab: Boolean) : Action()
    data class RequestRemove(val project: ProjectItem) : Action()
    data class RemoveConfirm(val project: ProjectItem) : Action()
    object SuspendRemove : Action()
}
