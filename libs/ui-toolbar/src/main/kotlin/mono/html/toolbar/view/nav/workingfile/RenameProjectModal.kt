/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.nav.workingfile

import kotlinx.browser.document
import mono.actionmanager.OneTimeActionType
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
import org.w3c.dom.HTMLSpanElement

/**
 * A class for renaming the working project.
 */
internal class RenameProjectModal(
    private val targetElement: HTMLSpanElement,
    private val onActionSelected: (OneTimeActionType) -> Unit
) {
    private var modal: Element? = null
    fun show(anchor: Element) {
        modal = document.body?.Div("rename-project-modal") {
            Input(inputType = InputType.TEXT, classes = "rename-project-input") {
                value = targetElement.innerText
                focus()

                setOnFocusOut {
                    confirmInputAndDismiss(value)
                }
                onKeyDown {
                    when (it.which) {
                        Key.KEY_ESC -> dismiss()
                        Key.KEY_ENTER -> confirmInputAndDismiss(value)
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

    private fun confirmInputAndDismiss(newName: String) {
        if (newName.isNotBlank()) {
            onActionSelected(OneTimeActionType.RenameProject(newName))
            targetElement.innerText = newName
        }

        dismiss()
    }

    private fun dismiss() {
        post {
            modal?.remove()
        }
    }
}
