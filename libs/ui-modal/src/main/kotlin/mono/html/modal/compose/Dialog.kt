/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import mono.scopedcss.css
import mono.ui.compose.ext.classes
import mono.ui.compose.ext.onConsumeClick
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Element
import mono.html.Div as MonoDiv

private val dialogBackground = css(
    """
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--modal-background-bg);
    """
)

private val dialogContainer = css(
    $$"""
    background: var(--modal-container-bg);
    border: 1px solid var(--mainmenu-divider-color);
    min-width: 350px;
    max-width: 450px;
    border-radius: 5px;
    padding: 12px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
    font-family: $monospaceFont;

    display: flex;
    flex-direction: column;
    """
)

private val dialogContent = css(
    """
    @include scoped-theme((
        --contentText: (#5e5d62, #f2f2f2),
    ));

    font-size: 14px;
    line-height: 1.4;
    margin: 0 8px 8px;
    color: var(--contentText);

    &.no-title {
        font-size: 16px;
    }
    """
)

private val dialogActions = css(
    """
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
    """
)

private val dialogTitle = css(
    """
    font-size: 19px;
    margin: 4px 8px 8px;
    color: var(--modal-title-color);
    """
)

private val actionButton = css(
    """
    margin-left: 8px;
    font-size: 14px;
    cursor: pointer;
    user-select: none;
    border-radius: 4px;
    border: 1px solid transparent;
    padding: 4px 8px;

    &.primary {
        @include scoped-theme((
            --primary-action-color: (#007bff, #cc8108),
            --primary-action-hover-color: (#328ced, #daa113),
            --primary-action-pressed-color: (#0062cc, #ab7a08),
        ));

        background: var(--primary-action-color);
        color: white;

        &:hover {
            background: var(--primary-action-hover-color);
        }

        &:active {
            background: var(--primary-action-pressed-color);
        }
    }

    &.primary.danger {
        @include scoped-theme((
            --primary-action-color: (#E53935, #E53935),
            --primary-action-hover-color: (#FF5252, #FF5252),
            --primary-action-pressed-color: (#C62828, #C62828),
        ));
    }

    &.secondary {
        @include scoped-theme((
            --secondary-action-border-color: (#D9D8DC, #74727a),
            --secondary-action-text-color: (#323136, #e0dce9),
            --secondary-action-hover-color: (#6c757d, #3e3d43),
            --secondary-action-pressed-color: (#424950, #262628),
        ));

        border-color: var(--secondary-action-border-color);
        color: var(--secondary-action-text-color);

        &:hover {
            background-color: var(--secondary-action-hover-color);
            color: white;
            border-color: transparent;
        }

        &:active {
            background-color: var(--secondary-action-pressed-color);
            color: white;
            border-color: transparent;
        }
    }
    """
)

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
    content: ContentBuilder<Element>,
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
                classes(dialogBackground)
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
    content: ContentBuilder<Element>,
    primaryAction: DialogAction? = null,
    secondaryAction: DialogAction? = null,
    dismiss: () -> Unit
) {
    Div(
        attrs = {
            classes(dialogContainer)
            onConsumeClick {
                // Do nothing, just block the dismiss event
            }
        }
    ) {
        Title(title)
        Div(
            attrs = {
                classes(dialogContent, "no-title" to title.isBlank())
            }
        ) { content() }

        if (primaryAction != null || secondaryAction != null) {
            Div(
                attrs = { classes(dialogActions) }
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
    H2(attrs = { classes(dialogTitle) }) {
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
            classes(actionButton, className, "danger" to action.isDanger)
            onClick {
                action.action()
                dismiss()
            }
        }
    ) {
        Text(action.name)
    }
}
