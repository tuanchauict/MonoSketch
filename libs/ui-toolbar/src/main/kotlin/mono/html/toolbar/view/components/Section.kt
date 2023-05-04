/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.components

import androidx.compose.runtime.Composable
import mono.ui.compose.ext.classes
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
internal fun Section(
    title: String = "",
    hasBorderTop: Boolean = true,
    content: ContentBuilder<HTMLDivElement>
) {
    Div(
        attrs = {
            classes(
                "section" to true,
                "notitle" to title.isEmpty(),
                "border-top" to hasBorderTop
            )
        }
    ) {
        if (title.isNotEmpty()) {
            Div(attrs = { classes("section-title") }) {
                Text(title)
            }
        }

        Div(attrs = { classes("section-body") }) {
            content()
        }
    }
}
