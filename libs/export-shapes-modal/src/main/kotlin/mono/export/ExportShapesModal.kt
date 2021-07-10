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
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.board.Highlight
import mono.graphics.board.MonoBoard
import mono.graphics.geo.Rect
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import org.w3c.dom.HTMLElement

/**
 * A modal which is for showing the board rendering selected shapes and allowing users to copy as
 * text.
 */
class ExportShapesModal(
    private val selectedShapes: List<AbstractShape>,
    private val getBitmap: (AbstractShape) -> MonoBitmap?
) {
    fun show() {
        val left = selectedShapes.minOf { it.bound.left }
        val right = selectedShapes.maxOf { it.bound.right }
        val top = selectedShapes.minOf { it.bound.top }
        val bottom = selectedShapes.maxOf { it.bound.bottom }
        val window = Rect.byLTRB(left, top, right, bottom)
        val exportingBoard = MonoBoard().apply { clearAndSetWindow(window) }
        drawShapesOntoExportingBoard(exportingBoard, selectedShapes)

        createModalContent(exportingBoard.toStringInBound(window))
    }

    private fun drawShapesOntoExportingBoard(board: MonoBoard, shapes: Collection<AbstractShape>) {
        for (shape in shapes) {
            if (shape is Group) {
                drawShapesOntoExportingBoard(board, shape.items)
                continue
            }
            val bitmap = getBitmap(shape) ?: continue
            board.fill(shape.bound.position, bitmap, Highlight.NO)
        }
    }

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
                style = "line-height: 15px; font-size: 12px; height: 100%; outline: none; " +
                    "min-height: 160px"
                +content
            }
        }
    }

    private fun TagConsumer<HTMLElement>.Footer(content: String) {
        div("modal-footer") {
            val textBox = textArea {
                style = "position: absolute; left: -1000px; width: 0px; height: 0px;"
                +content
            }
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
