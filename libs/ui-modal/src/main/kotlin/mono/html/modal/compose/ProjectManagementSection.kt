/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import mono.ui.compose.components.Icons
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ProjectManagementSection(
    isFiltering: Boolean,
    onActionClick: (ProjectManagementActionItem) -> Unit
) {
    if (isFiltering) {
        return
    }
    Div(attrs = { classes("list") }) {
        ManagementItem("New project", { Icons.Warning() }) {
            onActionClick(ProjectManagementActionItem.NewProject)
        }
        ManagementItem("Import from file...", { Icons.Warning() }) {
            onActionClick(ProjectManagementActionItem.ImportFile)
        }
    }

    Div(attrs = { classes("divider") })
}

@Composable
private fun ManagementItem(title: String, icon: @Composable () -> Unit, onActionClick: () -> Unit) {
    Div(
        attrs = {
            classes("item", "normal")
            onClick { onActionClick() }
        }
    ) {
        Div(attrs = { classes("name-container") }) {
            Icon { icon() }
            Span(attrs = { classes("name") }) {
                Text(title)
            }
        }
    }
}

@Composable
private fun Icon(icon: @Composable () -> Unit) {
    Span(attrs = { classes("icon") }) { icon() }
}

/**
 * A sealed interface for dropdown menu items of working file.
 */
sealed interface ProjectManagementActionItem {
    object NewProject : ProjectManagementActionItem
    object ImportFile : ProjectManagementActionItem
}
