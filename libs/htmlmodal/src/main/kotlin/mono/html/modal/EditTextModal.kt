package mono.html.modal

import kotlinx.browser.document
import mono.common.Key
import mono.common.commandKey
import mono.common.onKeyDown
import mono.common.post
import mono.html.Div
import mono.html.ext.px
import mono.html.ext.styleOf
import mono.html.setAttributes
import mono.html.setOnClickListener
import mono.html.setOnMouseWheelListener
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
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
        root = document.body?.Div("modal-edit-text") {
            Div("modal-edit-text-container") {
                Div("modal-edit-text-area-container") {
                    setAttributes(
                        "style" to styleOf(
                            "left" to (leftPx - 1).px,
                            "top" to (topPx - 1).px,
                            "width" to (widthPx + 2).px,
                            "height" to (heightPx + 2).px
                        )
                    )

                    initTextArea()

                    setOnClickListener { it.stopPropagation() }
                }
            }

            setOnClickListener { dismiss() }
            setOnMouseWheelListener { it.preventDefault() }
        }
    }

    private fun dismiss() {
        root?.remove()
        onDismiss()
    }

    fun setOnDismiss(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }

    private fun Element.initTextArea() {
        val textArea = Div("modal-edit-text-area") {
            setAttributes("contenteditable" to true)
        }
        // This div is for HTML decoding
        val converterDiv = Div {
            setAttributes("style" to styleOf("display" to "none"))
        }
        val htmlAdjustmentRegex = "(^<div>|</div>|<br/?>)".toRegex()
        textArea.oninput = {
            val html = textArea.innerHTML.replace(htmlAdjustmentRegex, "")
            val lines = html.split("<div>")
            val text = lines.joinToString("\n") {
                converterDiv.innerHTML = it
                converterDiv.innerText
            }
            onTextChange(text)
        }
        textArea.onpaste = {
            it.preventDefault()
            it.stopPropagation()
            val text = it.clipboardData?.getData("text/plain").orEmpty()
            insertText(text)
        }
        textArea.onKeyDown(action = ::checkKeyCommand)
        // Suspend down a trampoline to let the environment cleans up previous event (like ENTER key)
        post {
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
