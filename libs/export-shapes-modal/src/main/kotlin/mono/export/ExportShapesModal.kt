/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.export

import kotlinx.browser.document
import kotlinx.dom.addClass
import mono.common.setTimeout
import mono.html.Div
import mono.html.Pre
import mono.html.Span
import mono.html.SvgIcon
import mono.html.TextArea
import mono.html.setAttributes
import mono.html.setOnClickListener
import org.w3c.dom.Element

/**
 * A modal which is for showing the board rendering selected shapes and allowing users to copy as
 * text.
 */
internal class ExportShapesModal {
    private var root: Element? = null
    fun show(content: String) {
        root = document.body?.Div(classes = "export-text") {
            Div(classes = "background fade-in")

            Div(classes = "export-text__modal") {
                CloseButton()
                Span(text = "Export", classes = "export-text__title")
                Content(content)

                setOnClickListener {
                    it.stopPropagation()
                }
            }

            setOnClickListener { dismiss() }
        }
        root?.addClass("in")
    }

    private fun Element.CloseButton() {
        Span(classes = "export-text__close") {
            /* ktlint-disable max-line-length */
            SvgIcon(
                16,
                "M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z",
                "M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z"
            )
            /* ktlint-enable max-line-length */

            setOnClickListener { dismiss() }
        }
    }

    private fun Element.Content(content: String) {
        Div(classes = "export-text__content") {
            val textContent = Pre(text = content) {
                setAttributes("contenteditable" to true)
            }
            val textBox = TextArea(classes = "hidden", content = content)
            CopyButton {
                textBox.value = textContent.innerText
                textBox.select()
                document.execCommand("copy")
            }
        }
    }

    private fun Element.CopyButton(copyContent: () -> Unit) {
        Span(classes = "export-text__copy") {
            SvgIcon(
                24,
                "M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z" // ktlint-disable max-line-length
            )

            setAttributes("title" to "Copy")
            setOnClickListener { copyContent() }
        }
    }

    private fun dismiss() {
        val nonNullRoot = root ?: return
        nonNullRoot.addClass("out")
        setTimeout(300) {
            nonNullRoot.remove()
        }
    }
}
