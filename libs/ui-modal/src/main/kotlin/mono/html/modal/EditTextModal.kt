/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.modal

import kotlinx.browser.document
import mono.common.Key
import mono.common.commandKey
import mono.common.onKeyDown
import mono.common.post
import mono.html.Div
import mono.html.px
import mono.html.setAttributes
import mono.html.setOnFocusOut
import mono.html.setOnMouseWheelListener
import mono.html.styleOf
import mono.scopedcss.css
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.KeyboardEvent

private val editTextModal = css(
    $$"""
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    z-index: $edit-text-zindex;
    background-color: #00000000;
    color: var(--workspace-edittext-color);
    """
)

private val editTextContainer = css(
    $$"""
    position: absolute;
    left: $canvas-left;
    top: ($canvas-top + $nav-height);
    right: 0;
    bottom: 0;
    """
)

private val editTextAreaContainer = css(
    """
    position: absolute;
    """
)

private val editTextArea = css(
    $$"""
    position: absolute;
    left: 1px;
    top: 1.3px;
    width: 100%;
    height: 100%;

    font-family: $monospaceFont;
    font-size: 13px;
    line-height: 17.1px;
    letter-spacing: 0.2px;

    &:focus {
        outline: none;
    }
    """
)

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
        root = document.body?.Div(editTextModal) {
            Div(editTextContainer) {
                Div(editTextAreaContainer) {
                    setAttributes(
                        "style" to styleOf(
                            "left" to (leftPx - 1).px,
                            "top" to (topPx - 1).px,
                            "width" to (widthPx + 2).px,
                            "height" to (heightPx + 2).px
                        )
                    )

                    initTextArea()
                }
            }

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
        val textArea = Div(editTextArea) {
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

        textArea.setOnFocusOut { dismiss() }
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
