/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.shapetool2.components

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
    val hasTitle = title.isNotEmpty()
    Div(
        attrs = {
            classes(
                "section",
                "flex",
                "flex-col",
                "px-2.5",
                "py-2.5" to hasTitle,
                "py-1" to !hasTitle,
                "text-[var(--shapetool-section-content-color)]",
                "border-t-[0.75px]" to hasBorderTop,
                "border-t-[var(--shapetool-section-divider-color)]" to hasBorderTop
            )
        }
    ) {
        if (hasTitle) {
            Div(
                attrs = {
                    classes(
                        "font-medium",
                        "text-[11px]",
                        "text-[var(--shapetool-section-title-color)]",
                        "mb-2.5",
                        "select-none",
                        "tracking-wider"
                    )
                }
            ) {
                Text(title)
            }
        }

        Div(attrs = { classes("flex", "flex-col", "w-full") }) {
            content()
        }
    }
}
