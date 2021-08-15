package mono.html.modal

import kotlinx.browser.document
import kotlinx.html.contentEditable
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseWheelFunction
import kotlinx.html.style
import mono.common.Key
import mono.common.commandKey
import mono.common.onKeyDown
import mono.common.setTimeout
import mono.html.ext.Tag
import mono.html.ext.px
import mono.html.ext.styleOf
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

/**
 * A modal for edit text.
 */
class EditTextModal(
    private val initText: String = "",
    private val onTextChange: (newText: String) -> Unit
) {
    private var root: HTMLElement? = null

    private var onDismiss: () -> Unit = {}

    fun show(
        leftPx: Double,
        topPx: Double,
        widthPx: Double,
        heightPx: Double
    ) {
        val container = document.body ?: return
        container.append {
            root = div("modal-edit-text") {
                div("modal-edit-text-container") {
                    div("modal-edit-text-area-container") {
                        style = styleOf(
                            "left" to (leftPx - 1).px,
                            "top" to (topPx - 1).px,
                            "width" to (widthPx + 2).px,
                            "height" to (heightPx + 2).px
                        )

                        initTextArea()

                        onClickFunction = Event::stopPropagation
                    }
                }

                onClickFunction = {
                    dismiss()
                }

                onMouseWheelFunction = {
                    it.preventDefault()
                }
            }
        }
    }

    private fun dismiss() {
        root?.remove()
        onDismiss()
    }

    fun setOnDismiss(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }

    private fun Tag.initTextArea() {
        val textArea = div("modal-edit-text-area") {
            contentEditable = true
        }

        textArea.oninput = {
            onTextChange(textArea.innerText)
        }
        textArea.onpaste = {
            it.preventDefault()
            it.stopPropagation()
            val text = it.clipboardData?.getData("text/plain").orEmpty()
            insertText(text)
        }
        textArea.onKeyDown(action = ::checkKeyCommand)
        // Suspend down a trampoline to let the environment cleans up previous event (like ENTER key)
        setTimeout(0) {
            textArea.focus()
            insertText(initText)
        }
    }

    private fun insertText(text: String) {
        // TODO: `execCommand` is deprecated. Find a new solution.
        document.execCommand("insertText", false, text)
    }

    private fun checkKeyCommand(event: KeyboardEvent) {
        if (event.keyCode == Key.KEY_ENTER && event.commandKey || event.keyCode == Key.KEY_ESC) {
            dismiss()
        }
    }
}
