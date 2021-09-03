@file:Suppress("FunctionName")

package mono.export

import kotlinx.browser.document
import kotlinx.dom.addClass
import mono.html.A
import mono.html.Div
import mono.html.Heading
import mono.html.HeadingLevel
import mono.html.Pre
import mono.html.TextArea
import mono.html.ext.px
import mono.html.ext.styleOf
import mono.html.setAttributes
import mono.html.setOnClickListener
import org.w3c.dom.Element

/**
 * A modal which is for showing the board rendering selected shapes and allowing users to copy as
 * text.
 */
internal class ExportShapesModal {
    fun show(content: String) = createModalContent(content)

    private fun createModalContent(content: String) {
        val body = document.body ?: return
        with(body) {
            val rootView = Div("modal fade") {
                id = MODAL_ID
                setAttributes(
                    "tabindex" to "-1",
                    "aria-labelledby" to "export-shape-modal-title",
                    "aria-hidden" to "true"
                )

                Div {
                    addClass(
                        "modal-dialog",
                        "modal-dialog-scrollable",
                        "modal-dialog-centered",
                        "modal-xl",
                        "modal-fullscreen-lg-down"
                    )
                    setAttributes("role" to "document")

                    Div("modal-content") {
                        Header()
                        Body(content)
                        Footer(content)
                    }
                }
            }
            rootView.addEventListener(
                "hidden.bs.modal",
                {
                    rootView.remove()
                }
            )
        }
        js(
            """
            var modal = new bootstrap.Modal(document.getElementById("$MODAL_ID"));
            modal.show();
            """
        )
    }

    private fun Element.Header() {
        Div("modal-header") {
            Heading(HeadingLevel.H5, "modal-title", "Export") {
                id = "export-shape-modal-title"
            }
            A(classes = "btn-close") {
                setAttributes(
                    "type" to "button",
                    "data-bs-dismiss" to "modal",
                    "aria-label" to "Close"
                )
            }
        }
    }

    private fun Element.Body(content: String) {
        Div("modal-body") {
            Pre(text = content) {
                setAttributes(
                    "contenteditable" to true,
                    "style" to styleOf(
                        "height" to "100%",
                        "min-height" to 160.px,
                        "line-height" to 15.px,
                        "font-size" to 12.px,
                        "outline" to "none"
                    )
                )
            }
        }
    }

    private fun Element.Footer(content: String) {
        Div("modal-footer") {
            val textBox = TextArea(classes = "hidden", content = content)
            A(classes = "btn btn-outline-primary btn-sm", text = "Copy") {
                setOnClickListener {
                    textBox.select()
                    document.execCommand("copy")
                }
            }
        }
    }

    companion object {
        private const val MODAL_ID = "export-shape-modal"
    }
}
