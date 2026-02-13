@file:Suppress("FunctionName")

package mono.mermaid

import kotlinx.browser.document
import kotlinx.dom.addClass
import mono.common.setTimeout
import mono.html.Div
import mono.html.Span
import mono.html.SvgIcon
import mono.html.TextArea
import mono.html.setAttributes
import mono.html.setOnClickListener
import org.w3c.dom.Element
import org.w3c.dom.HTMLTextAreaElement

/**
 * Modal for pasting and importing a Mermaid flowchart.
 * Reuses the existing export-text modal CSS.
 */
class ImportMermaidModal {
    private var root: Element? = null
    private var textArea: HTMLTextAreaElement? = null

    fun show(onImport: (String) -> Unit) {
        root = document.body?.Div(classes = "export-text") {
            Div(classes = "background fade-in")

            Div(classes = "export-text__modal") {
                CloseButton()
                Span(text = "Import Mermaid", classes = "export-text__title")
                InputArea()
                ImportButton {
                    val text = textArea?.value.orEmpty().trim()
                    if (text.isNotEmpty()) {
                        onImport(text)
                        dismiss()
                    }
                }

                setOnClickListener { it.stopPropagation() }
            }

            setOnClickListener { dismiss() }
        }
        root?.addClass("in")
    }

    private fun Element.CloseButton() {
        Span(classes = "export-text__close") {
            SvgIcon(
                16,
                "M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z",
                "M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z"
            )
            setOnClickListener { dismiss() }
        }
    }

    private fun Element.InputArea() {
        Div(classes = "export-text__content") {
            textArea = TextArea(classes = "", content = "") {
                setAttributes(
                    "placeholder" to PLACEHOLDER,
                    "rows" to "12",
                    "spellcheck" to "false"
                )
                style.apply {
                    setProperty("width", "100%")
                    setProperty("min-height", "200px")
                    setProperty("font-family", "monospace")
                    setProperty("font-size", "13px")
                    setProperty("resize", "vertical")
                }
            }
        }
    }

    private fun Element.ImportButton(onClick: () -> Unit) {
        Span(classes = "export-text__copy") {
            SvgIcon(24, "M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z")
            Span(text = " Import")
            setAttributes("title" to "Import")
            setOnClickListener { onClick() }
        }
    }

    private fun dismiss() {
        val r = root ?: return
        r.addClass("out")
        setTimeout(300) { r.remove() }
    }

    companion object {
        private const val PLACEHOLDER = "Paste a Mermaid flowchart, e.g.:\n\n" +
            "graph TD\n" +
            "    A[Start] --> B{Decision}\n" +
            "    B -->|Yes| C[OK]\n" +
            "    B -->|No| D[End]"
    }
}
