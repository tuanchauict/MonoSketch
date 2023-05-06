/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

@Composable
internal fun ToolbarContainer(content: ContentBuilder<HTMLDivElement>? = null) {
    Div(
        attrs = { classes("toolbar-container") }
    ) {
        content?.invoke(this)
    }
}
