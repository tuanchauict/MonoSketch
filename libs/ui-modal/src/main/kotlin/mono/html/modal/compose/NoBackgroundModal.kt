/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import mono.common.Cancelable
import mono.common.setTimeout
import mono.html.Div
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement

/**
 * Show a modal without background
 */
internal fun NoBackgroundModal(
    attrs: AttrBuilderContext<HTMLDivElement>,
    onDismiss: () -> Unit = {},
    content: @Composable ModalElementScope.() -> Unit
) {
    val body = document.body ?: return
    val container = body.Div()
    val composition = renderComposable(container) {}
    composition.setContent {
        var isDismissed by remember { mutableStateOf(false) }
        ModalContainer(attrs, content) {
            if (!isDismissed) {
                composition.dispose()
                container.remove()
                onDismiss()
                isDismissed = true
            }
        }
    }
}

@Composable
private fun ModalContainer(
    attrs: AttrBuilderContext<HTMLDivElement>,
    content: @Composable ModalElementScope.() -> Unit,
    dismiss: () -> Unit
) {
    var cancelable: Cancelable? by remember { mutableStateOf(null) }
    Div(
        attrs = {
            classes("no-background-modal")
            tabIndex(-1)

            onFocusIn { cancelable?.cancel() }

            onFocusOut {
                if (document.hasFocus()) {
                    cancelable = setTimeout(20) {
                        dismiss()
                    }
                }
            }

            onKeyDown {
                when (it.key) {
                    "Escape" -> dismiss()
                    // TODO: Use ArrowDown and ArrowUp for changing the active project
                    // TODO: Use Enter for opening the project by keyboard
                }
            }

            attrs.invoke(this)
        }
    ) {
        val scope = ModalElementScope(this, dismiss)
        content.invoke(scope)
    }
}
