/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import mono.ui.compose.ext.classes
import mono.ui.compose.ext.onConsumeClick
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import mono.html.Div as MonoDiv

class DialogAction(val name: String, val isDanger: Boolean = false, val action: () -> Unit)

/**
 * Creates a dialog modal looks like
 * ```
 * ┌─────────────────────────────────────────────────┐
 * │ Title                                           │
 * │                                                 │
 * │ Message                                         │
 * │                      ┌───────────┐ ┌───────────┐│
 * │                      │ Secondary │ │  Primary  ││
 * │                      └───────────┘ └───────────┘│
 * └─────────────────────────────────────────────────┘
 * ```
 */
fun Dialog(
    title: String = "",
    message: String = "",
    primaryAction: DialogAction? = null,
    secondaryAction: DialogAction? = null
) {
    check(title.isNotBlank() || message.isNotBlank()) {
        "Either title or message must not be blank"
    }
    Dialog(
        title,
        { Div(attrs = { classes("dialog-text-content") }) { Text(message) } },
        primaryAction,
        secondaryAction
    )
}

/**
 * Creates a dialog modal looks like
 * ```
 * ┌─────────────────────────────────────────────────┐
 * │ Title                                           │
 * │                                                 │
 * │ Content                                         │
 * │                      ┌───────────┐ ┌───────────┐│
 * │                      │ Secondary │ │  Primary  ││
 * │                      └───────────┘ └───────────┘│
 * └─────────────────────────────────────────────────┘
 * ```
 */
fun Dialog(
    title: String,
    content: @Composable () -> Unit,
    primaryAction: DialogAction? = null,
    secondaryAction: DialogAction? = null
) {
    val body = document.body ?: return

    val container = body.MonoDiv {}
    val composition = renderComposable(container) {
    }

    val dismiss = {
        composition.dispose()
        container.remove()
    }

    composition.setContent {
        Div(
            attrs = {
                classes("dialog-bg")
                onConsumeClick { dismiss() }
            }
        ) {
            DialogContainer(title, content, primaryAction, secondaryAction, dismiss)
        }
    }
}

@Composable
private fun DialogContainer(
    title: String,
    content: @Composable () -> Unit,
    primaryAction: DialogAction? = null,
    secondaryAction: DialogAction? = null,
    dismiss: () -> Unit
) {
    Div(
        attrs = {
            classes("dialog-container" to true)
            onConsumeClick {
                // Do nothing, just block the dismiss event
            }
        }
    ) {
        Title(title)
        Div(
            attrs = {
                classes(
                    "dialog-content" to true,
                    "no-title" to title.isBlank()
                )
            }
        ) { content() }

        if (primaryAction != null || secondaryAction != null) {
            Div(
                attrs = { classes("dialog-actions") }
            ) {
                ActionButton(secondaryAction, "secondary", dismiss)
                ActionButton(primaryAction, "primary", dismiss)
            }
        }
    }
}

@Composable
private fun Title(title: String) {
    if (title.isBlank()) {
        return
    }
    H2(
        attrs = {
            classes("dialog-title")
        }
    ) {
        Text(title)
    }
}

@Composable
private fun ActionButton(action: DialogAction?, className: String, dismiss: () -> Unit) {
    if (action == null) {
        return
    }
    Div(
        attrs = {
            classes("dialog-action" to true, className to true, "danger" to action.isDanger)
            onClick {
                action.action()
                dismiss()
            }
        }
    ) {
        Text(action.name)
    }
}
