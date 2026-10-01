/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.nav.projectmanagement

import androidx.compose.runtime.mutableStateOf
import kotlinx.browser.document
import mono.common.post
import mono.html.Div
import mono.html.px
import mono.html.style
import mono.scopedcss.css
import org.jetbrains.compose.web.attributes.InputType.Text
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.renderComposable

private val renameProjectModal = css(
    """
    position: fixed;
    z-index: var(--rename-project-modal-zindex);
    background-color: var(--modal-container-bg);
    border-radius: 4px;

    .rename-project-input {
        border-radius: 3px;
        font-family: var(--monospace-font);
        padding: 4px 8px;
        border: 2px solid var(--text-input-border-color);

        &:focus {
            outline: none;
            border-color: var(--text-input-border-focus-color);
        }
    }
    """
)

internal fun showRenameProjectModal(
    initName: String,
    anchorSelector: String,
    onDismiss: (String) -> Unit
) {
    val anchor = document.querySelector(anchorSelector) ?: return
    val modal = document.body?.Div(renameProjectModal) {
        val anchorRect = anchor.getBoundingClientRect()
        style(
            "left" to anchorRect.left.px,
            "top" to (anchorRect.bottom - 4).px
        )
    } ?: return
    val name = mutableStateOf(initName)
    val composition = renderComposable(modal) {}

    val dismiss = { newName: String ->
        onDismiss(newName)
        composition.dispose()
        post { modal.remove() }
    }

    composition.setContent {
        Input(Text) {
            classes("rename-project-input")
            defaultValue(initName)

            onKeyDown {
                when (it.key) {
                    "Enter" -> dismiss(name.value)
                    "Escape" -> dismiss("")
                }
            }
            onFocusOut { dismiss(name.value) }
            onChange { name.value = it.value }

            ref {
                it.focus()
                // Move the cursor to the end of the text box
                it.setSelectionRange(Int.MAX_VALUE, Int.MAX_VALUE)
                onDispose {}
            }
        }
    }
}
