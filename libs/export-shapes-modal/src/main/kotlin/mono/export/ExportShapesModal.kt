@file:Suppress("FunctionName")

package mono.export

import kotlinx.browser.document
import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.h5
import kotlinx.html.id
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.textArea
import kotlinx.html.pre
import kotlinx.html.style
import mono.html.ext.px
import mono.html.ext.styleOf
import org.w3c.dom.HTMLElement

/**
 * A modal which is for showing the board rendering selected shapes and allowing users to copy as
 * text.
 */
internal class ExportShapesModal {
    fun show(content: String) = createModalContent(content)

    private fun createModalContent(content: String) {
        val body = document.body ?: return
        body.append {
            val rootView = div("modal fade") {
                id = MODAL_ID
                attributes["tabindex"] = "-1"
                attributes["aria-labelledby"] = "export-shape-modal-title"
                attributes["aria-hidden"] = "true"

                div {
                    classes = setOf(
                        "modal-dialog",
                        "modal-dialog-scrollable",
                        "modal-dialog-centered",
                        "modal-xl",
                        "modal-fullscreen-lg-down"
                    )
                    attributes["role"] = "document"

                    div("modal-content") {
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

    private fun TagConsumer<HTMLElement>.Header() {
        div("modal-header") {
            h5("modal-title") {
                id = "export-shape-modal-title"

                +"Export"
            }
            button(classes = "btn-close") {
                attributes["type"] = "button"
                attributes["data-bs-dismiss"] = "modal"
                attributes["aria-label"] = "Close"
            }
        }
    }

    private fun TagConsumer<HTMLElement>.Body(content: String) {
        div("modal-body") {
            pre {
                attributes["contenteditable"] = "true"
                style = styleOf(
                    "height" to "100%",
                    "min-height" to 160.px,
                    "line-height" to 15.px,
                    "font-size" to 12.px,
                    "outline" to "none"
                )
                +content
            }
        }
    }

    private fun TagConsumer<HTMLElement>.Footer(content: String) {
        div("modal-footer") {
            val textBox = textArea(classes = "hidden", content = content)
            button(classes = "btn btn-outline-primary btn-sm") {
                +"Copy"

                onClickFunction = {
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
