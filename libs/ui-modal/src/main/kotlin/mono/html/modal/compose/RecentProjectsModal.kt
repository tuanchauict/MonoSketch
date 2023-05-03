/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.browser.document
import mono.ui.compose.components.IconClose
import mono.ui.compose.ext.sideEffectFocus
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import mono.html.Div as MonoDiv

private typealias SelectAction = (ProjectItem, isRemoved: Boolean) -> Unit

class ProjectItem(val id: String, val name: String)

fun showRecentProjectsModal(
    projectItems: List<ProjectItem>,
    onSelect: SelectAction
) {
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
    onSelect: SelectAction,
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
            ProjectList(filter.value, projects) { item, isRemoved ->
                onSelect(item, isRemoved)
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
            id("recent-project-filter-input")
            placeholder("Filter by name")

            onInput { onChange(it.value) }
        }

        sideEffectFocus("#recent-project-filter-input")
    }
}

@Composable
private fun ProjectList(filter: String, projects: List<ProjectItem>, onSelect: SelectAction) {
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
private fun ProjectContent(item: ProjectItem, onSelect: SelectAction) {
    Div(
        attrs = {
            classes("item")
            onClick { onSelect(item, false) }
        }
    ) {
        Div {
            Text(item.name)
        }
        Div(
            attrs = {
                classes("remove")
                onClick {
                    // TODO: Show confirm dialog
                    onSelect(item, true)
                }
            }
        ) {
            IconClose(12)
        }
    }
}
