/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.nav.workingfile

import kotlinx.browser.document
import mono.common.Key
import mono.common.onKeyDown
import mono.common.post
import mono.html.Div
import mono.html.Input
import mono.html.InputType
import mono.html.px
import mono.html.setOnFocusOut
import mono.html.style
import org.w3c.dom.Element

/**
 * A class for renaming the working project.
 */
internal class RenameProjectModal(private val onDismiss: (String) -> Unit) {
    private var modal: Element? = null
    fun show(initName: String, anchor: Element) {
        modal = document.body?.Div("rename-project-modal") {
            Input(inputType = InputType.TEXT, classes = "rename-project-input") {
                value = initName
                focus()

                setOnFocusOut {
                    dismiss(value)
                }
                onKeyDown {
                    when (it.which) {
                        Key.KEY_ESC -> dismiss("")
                        Key.KEY_ENTER -> dismiss(value)
                    }
                }
            }

            val anchorRect = anchor.getBoundingClientRect()
            style(
                "left" to anchorRect.left.px,
                "top" to (anchorRect.bottom - 4).px
            )

            console.log(anchor)
        }
    }

    private fun dismiss(newName: String) {
        onDismiss(newName)
        post {
            modal?.remove()
        }
    }
}
