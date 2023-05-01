/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import mono.html.Div as MonoDiv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.browser.document
import mono.ui.compose.components.IconClose
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

class ProjectItem(val id: String, val name: String)

fun showRecentProjectsModal(projectItems: List<ProjectItem>, onSelect: (ProjectItem) -> Unit) {
    val body = document.body ?: return
    val container = body.MonoDiv()
    renderComposable(container) {
        RecentProjectsModal(projectItems, onSelect) {
            container.remove()
        }
    }
}

@Composable
private fun RecentProjectsModal(
    projects: List<ProjectItem>,
    onSelect: (ProjectItem) -> Unit,
    onDismiss: () -> Unit
) {
    Div(
        attrs = {
            classes("recent-project-modal")

            onClick {
                onDismiss()
            }
        }
    ) {
        Div(attrs = {
            classes("container")

            onClick {
                it.preventDefault()
                it.stopPropagation()
            }
        }) {
            val filter = remember { mutableStateOf("") }

            FilterInput { filter.value = it }
            ProjectList(filter.value, projects) {
                onSelect(it)
                onDismiss()
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
            autoFocus()

            onInput { onChange(it.value) }
        }
    }
}

@Composable
private fun ProjectList(
    filter: String,
    projects: List<ProjectItem>,
    onSelect: (ProjectItem) -> Unit
) {
    val filteredProjects = projects.filter { it.name.contains(filter, ignoreCase = true) }
    if (filteredProjects.isNotEmpty()) {
        Div(
            attrs = { classes("list") }
        ) {
            for (project in filteredProjects) {
                ProjectContent(project, onSelect)
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
private fun ProjectContent(item: ProjectItem, onSelect: (ProjectItem) -> Unit) {
    Div(
        attrs = {
            classes("item")
            onClick { onSelect(item) }
        }
    ) {
        Div(
//            attrs = { classes(RecentProjectsStyleSheet.projectItemText) }
        ) {
            Text(item.name)
        }
        Div(attrs = { classes("remove") }) {
            IconClose(12)
        }
    }
}
