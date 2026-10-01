/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import mono.browser.manager.BrowserManager
import mono.html.modal.TooltipPosition
import mono.html.modal.tooltip
import mono.scopedcss.css
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
import org.w3c.dom.Element

private typealias ProjectManagementAction = (ProjectManagementActionItem) -> Unit
private typealias ProjectSelectAction = (ProjectItem, isRemoved: Boolean) -> Unit

private val recentProjectModal = css(
    $$"""
    $modal-width: 500px;

    position: fixed;
    top: 0;
    left: calc(50% - $modal-width / 2);
    width: $modal-width;
    z-index: $all-projects-modal-zindex;
    outline: none;

    display: flex;
    """
)

private val modalContainer = css(
    $$"""
    margin-top: 6px;
    background: $modal-container-bg;
    border-radius: 6px;
    border: 1px solid $modal-container-border-color;
    box-shadow: 0px 8px 20px rgba(0, 0, 0, 0.25);
    padding: 8px 0;
    width: 100%;
    flex-grow: 0;

    color: $modal-content-text-color;
    font-family: $monospaceFont;
    """
)

private val filterContainer = css(
    $$"""
    width: 100%;
    margin-bottom: 8px;
    padding: 0px 6px;

    input[type="text"] {
        width: 100%;
        padding: 6px 8px;
        border-radius: 3px;
        outline: none;
        border: 1px solid $modal-text-field-border;
        background: none;
        color: $modal-text-field-color;
        font-family: $monospaceFont;

        &:hover,
        &:focus {
            border-color: $modal-text-field-focus-border;
        }
    }
    """
)

internal val projectList = css(
    """
    width: 100%;
    min-height: 50px;
    max-height: 300px;
    overflow-y: auto;
    padding: 0px 6px;
    """
)

private val noItem = css(
    $$"""
    height: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: $modal-indicator-color;
    """
)

internal val projectItem = css(
    $$"""
    height: 26px;
    padding: 0 6px;
    margin-top: 2px;
    border-radius: 4px;
    font-size: 14px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;

    &.normal {
        &:hover {
            background: $modal-item-hover-bg;

            .actions {
                display: flex;
            }
        }

        &:active {
            opacity: 0.8;
        }

        .actions {
            display: none;
        }
    }

    &.removing {
        background: #E53935;
        color: white;
    }
    """
)

internal val projectNameContainer = css(
    """
    display: flex;
    align-items: center;
    """
)

internal val projectIcon = css(
    """
    display: flex;
    align-items: center;
    margin-right: 8px;
    opacity: 0.7;
    """
)

internal val projectName = css(
    """
    display: flex;
    align-items: center;
    """
)

private val projectActions = css(
    """
    display: flex;
    align-items: center;
    """
)

private val projectAction = css(
    """
    opacity: 0.7;
    padding: 3px;
    margin-left: 4px;
    display: flex;
    justify-content: center;
    align-items: center;

    &:hover {
        opacity: 1;
    }
    """
)

class ProjectItem(val id: String, val name: String, val isOpening: Boolean)

fun showRecentProjectsModal(
    projectItems: List<ProjectItem>,
    onManagementAction: ProjectManagementAction,
    onProjectSelect: ProjectSelectAction
) {
    NoBackgroundModal(
        attrs = {
            classes(recentProjectModal)
        }
    ) {
        ModalContent(onManagementAction, projectItems, onProjectSelect, dismiss)
    }
}

@Composable
private fun ModalContent(
    onManagementAction: ProjectManagementAction,
    projects: List<ProjectItem>,
    onProjectSelect: ProjectSelectAction,
    onDismiss: () -> Unit
) {
    Div(
        attrs = {
            classes(modalContainer)
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

@Composable
private fun FilterInput(onChange: (String) -> Unit) {
    Div(
        attrs = {
            classes(filterContainer)
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
            attrs = { classes(projectList) }
        ) {
            for (project in filteredProjects) {
                val isRemoveConfirming = project.id == requestingRemoveProjectId
                ProjectContent(project, isRemoveConfirming, onAction)
            }
        }
    } else {
        Div(
            attrs = { classes(noItem) }
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
                projectItem,
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
    Div(attrs = { classes(projectNameContainer) }) {
        Icon { Icons.Warning() }
        Span(attrs = { classes(projectName) }) {
            Text("""Delete "$name"?""")
        }
    }
}

@Composable
private fun ProjectName(name: String, isOpening: Boolean) {
    Div(attrs = { classes(projectNameContainer) }) {
        Icon {
            if (isOpening) Icons.FolderOpen() else Icons.Folder()
        }
        Span(attrs = { classes(projectName) }) {
            Text(name)
        }
    }
}

@Composable
private fun Icon(icon: ContentBuilder<Element>) {
    Span(attrs = { classes(projectIcon) }) { icon() }
}

@Composable
private fun Actions(
    isRemoveConfirming: Boolean,
    item: ProjectItem,
    onAction: (Action) -> Unit
) {
    Div(attrs = { classes(projectActions, "actions") }) {
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
            classes(projectAction)
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
            classes(projectAction)
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
            classes(projectAction)
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
            classes(projectAction)
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
