/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import mono.scopedcss.css
import mono.ui.compose.components.Icons
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private val divider = css(
    """
    height: 1px;
    width: 100%;
    padding: 0 16px;
    background: var(--mainmenu-divider-color);
    margin: 8px 0 6px;
    """
)

@Composable
internal fun ProjectManagementSection(
    isFiltering: Boolean,
    onActionClick: (ProjectManagementActionItem) -> Unit
) {
    if (isFiltering) {
        return
    }
    Div(attrs = { classes(projectList) }) {
        ManagementItem("New project", { Icons.NewFile() }) {
            onActionClick(ProjectManagementActionItem.NewProject)
        }
        ManagementItem("Import from file...", { Icons.FileImport() }) {
            onActionClick(ProjectManagementActionItem.ImportFile)
        }
    }

    Div(attrs = { classes(divider) })
}

@Composable
private fun ManagementItem(title: String, icon: @Composable () -> Unit, onActionClick: () -> Unit) {
    Div(
        attrs = {
            classes(projectItem, "normal")
            onClick { onActionClick() }
        }
    ) {
        Div(attrs = { classes(projectNameContainer) }) {
            Icon { icon() }
            Span(attrs = { classes(projectName) }) {
                Text(title)
            }
        }
    }
}

@Composable
private fun Icon(icon: @Composable () -> Unit) {
    Span(attrs = { classes(projectIcon) }) { icon() }
}

/**
 * A sealed interface for dropdown menu items of working file.
 */
sealed interface ProjectManagementActionItem {
    object NewProject : ProjectManagementActionItem
    object ImportFile : ProjectManagementActionItem
}
