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
import mono.scopedcss.css
import org.w3c.dom.Element

private val exportText = css(
    """
    position: fixed;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    backdrop-filter: blur(2px);
    z-index: var(--export-text-zindex);
    """
)

private val background = css(
    """
    background: var(--modal-background-bg);

    width: 100%;
    height: 100%;

    .in > & {
        animation: fadeIn 0.3s;
    }

    .out > & {
        animation: fadeOut 0.3s;
    }
    """
)

private val modal = css(
    """
    @extend %common-modal-container-position;

    display: flex;
    flex-direction: column;
    top: 100px;
    background: var(--exporttext-container-bg);

    .in > & {
        animation: moveIn 0.3s, fadeIn 0.3s;
    }

    .out > & {
        animation: moveOut 0.3s, fadeOut 0.3s;
    }
    """
)

private val title = css(
    """
    @extend %common-modal-title;

    display: block;
    width: 100%;
    """
)

private val closeButton = css(
    """
    @extend %common-modal-close-button;
    """
)

private val contentContainer = css(
    """
    height: calc(100% - 24px - 16px - 20px);
    color: var(--exporttext-content-color);

    pre {
        width: 100%;
        height: 100%;
        padding: 32px 32px 64px;
        margin: 0;
        font-size: 13px;
        line-height: 18px;
        outline: none;
        overflow: scroll;
        font-family: var(--monospace-font);
    }
    """
)

private val copyButton = css(
    """
    position: absolute;
    left: 8px;
    bottom: 4px;
    padding: 8px;
    color: rgba(0, 0, 0, 0.3);
    cursor: pointer;
    user-select: none;

    &:hover {
        color: rgba(0, 0, 0, 0.6);
    }

    &:active {
        color: rgba(0, 0, 0, 0.4);
    }
    """
)

/**
 * A modal which is for showing the board rendering selected shapes and allowing users to copy as
 * text.
 */
internal class ExportShapesModal {
    private var root: Element? = null
    fun show(content: String) {
        root = document.body?.Div(classes = exportText) {
            Div(classes = background)

            Div(classes = modal) {
                CloseButton()
                Span(text = "Export", classes = title)
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
        Span(classes = closeButton) {
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
        Div(classes = contentContainer) {
            val textContent = Pre(text = content) {
                setAttributes("contenteditable" to true)
            }
            val textBox = TextArea(classes = "sr-only", content = content)
            CopyButton {
                textBox.value = textContent.innerText
                textBox.select()
                document.execCommand("copy")
            }
        }
    }

    private fun Element.CopyButton(copyContent: () -> Unit) {
        Span(classes = copyButton) {
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
