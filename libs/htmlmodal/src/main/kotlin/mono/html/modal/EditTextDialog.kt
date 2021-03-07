package mono.html.modal

import kotlinx.browser.document
import kotlinx.html.contentEditable
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.style
import mono.common.getOnlyElementByClassName
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

/**
 * A concrete class of [BaseHtmlFullscreenModal] for editing text.
 */
class EditTextDialog(
    private val classes: String,
    private val initText: String = "",
    private val onTextChange: (newText: String) -> Unit
) : BaseHtmlFullscreenModal() {
    override val contentPosition: ModalPosition =
        ModalPosition(ModalPosition.Horizontal.MIDDLE, ModalPosition.Vertical.BOTTOM)

    override fun initContent(parent: HTMLElement) {
        parent.append {
            div(TEXT_AREA_CONTAINER_CSS_CLASS) {
                div("$TEXT_AREA_CSS_CLASS $classes") {
                    contentEditable = true
                    style = "width: 500px; min-height: 60px; max-height: 200px"
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
        textArea.onkeydown = {
            it.stopPropagation()
        }
        textArea.focus()
        insertText(initText)
    }

    private fun insertText(text: String) {
        // TODO: `execCommand` is deprecated. Find a new solution.
        document.execCommand("insertText", false, text)
    }

    companion object {
        private const val TEXT_AREA_CONTAINER_CSS_CLASS = "modal-textarea-container"
        private const val TEXT_AREA_CSS_CLASS = "modal-textarea"
    }
}
