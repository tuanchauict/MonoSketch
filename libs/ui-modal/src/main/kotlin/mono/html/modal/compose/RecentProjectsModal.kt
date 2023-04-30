/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.browser.document
import mono.html.modal.compose.css.CommonModalStyleSheet
import mono.html.modal.compose.css.RecentProjectsStyleSheet
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import mono.html.Div as MonoDiv

class ProjectItem(val id: String, val name: String)

fun showRecentProjectModal(projectItems: List<ProjectItem>, onSelect: (ProjectItem) -> Unit) {
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
    Style(RecentProjectsStyleSheet)
    Style(CommonModalStyleSheet)
    Div(
        attrs = {
            classes(CommonModalStyleSheet.fullscreenModal, CommonModalStyleSheet.blurBackdrop)

            onClick {
                onDismiss()
            }
        }
    ) {
        Div(attrs = {
            classes(CommonModalStyleSheet.container, RecentProjectsStyleSheet.container)

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
            classes(RecentProjectsStyleSheet.filterInputContainer)
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
            attrs = { classes(RecentProjectsStyleSheet.projectList) }
        ) {
            for (project in filteredProjects) {
                ProjectContent(project, onSelect)
            }
        }
    } else {
        Div(
            attrs = { classes(RecentProjectsStyleSheet.noItems) }
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
            classes(RecentProjectsStyleSheet.projectItem)

            onClick { onSelect(item) }
        }
    ) {
        Text(item.name)
    }
}
