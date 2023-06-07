/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import androidx.compose.runtime.Composable
import mono.ui.compose.ext.classes
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

@Composable
internal fun ToolbarContainer(
    addSpaceLeft: Boolean = false,
    addSpaceRight: Boolean = false,
    content: ContentBuilder<HTMLDivElement>? = null
) {
    Div(
        attrs = {
            classes(
                "toolbar-container",
                "add-space-left" to addSpaceLeft,
                "add-space-right" to addSpaceRight
            )
        }
    ) {
        content?.invoke(this)
    }
}
