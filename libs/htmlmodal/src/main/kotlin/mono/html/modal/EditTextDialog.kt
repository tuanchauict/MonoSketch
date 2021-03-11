package mono.html.modal

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.contentEditable
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.style
import mono.common.Key
import mono.common.getOnlyElementByClassName
import mono.common.isCommandKeySupported
import mono.common.onKeyDown
import mono.common.setTimeout
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.KeyboardEvent

/**
 * A concrete class of [BaseHtmlFullscreenModal] for editing text.
 */
class EditTextDialog(
    private val classes: String,
    private val initText: String = "",
    private val onTextChange: (newText: String) -> Unit
) : BaseHtmlFullscreenModal() {
    override val contentPosition: ModalPosition =
        ModalPosition(ModalPosition.Horizontal.MIDDLE, ModalPosition.Vertical.TOP)

    override fun initContent(parent: HTMLElement) {
        parent.append {
            div(TEXT_AREA_CONTAINER_CSS_CLASS) {
                div("$TEXT_AREA_CSS_CLASS $classes") {
                    contentEditable = true
                    style = "width: 500px; min-height: 60px; max-height: 100px"
                }
            }
        }
        val textArea =
            parent.getOnlyElementByClassName<HTMLDivElement>(TEXT_AREA_CSS_CLASS) ?: return
        textArea.oninput = {
            onTextChange(textArea.innerText)
        }
        textArea.onpaste = {
            it.preventDefault()
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
        val isWithCommand = if (window.isCommandKeySupported()) event.metaKey else event.ctrlKey
        if (event.keyCode == Key.KEY_ENTER && isWithCommand || event.keyCode == Key.KEY_ESC) {
            dismiss()
        }
    }

    companion object {
        private const val TEXT_AREA_CONTAINER_CSS_CLASS = "modal-textarea-container"
        private const val TEXT_AREA_CSS_CLASS = "modal-textarea"
    }
}
