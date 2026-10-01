/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.keyboardshortcut

import kotlinx.browser.document
import kotlinx.browser.window
import mono.common.isCommandKeySupported
import mono.html.Cell
import mono.html.Div
import mono.html.Row
import mono.html.Span
import mono.html.SvgIcon
import mono.html.Table
import mono.html.setOnClickListener
import mono.html.style
import mono.scopedcss.css
import org.w3c.dom.Element

private val keyboardShortcuts = css(
    """
    @extend %common-modal-container-position;
    @extend %common-modal-container-color;

    height: 230px;
    z-index: var(--keyboard-shortcuts-zindex);
    """
)

private val closeButton = css(
    """
    @extend %common-modal-close-button;
    """
)

private val content = css(
    """
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    font-family: var(--monospace-font);
    color: var(--modal-content-color);
    """
)

private val contentTitle = css(
    """
    @extend %common-modal-title;
    """
)

private val contentContainer = css(
    """
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    align-content: flex-start;
    justify-content: center;
    flex-wrap: wrap;
    width: 100%;
    max-height: 168px;
    overflow-y: scroll;
    """
)

private val shortcutTable = css(
    """
    table-layout: auto;
    font-size: 14px;
    margin: 0 16px 16px;

    tr {
        line-height: 26px;
    }

    td {
        white-space: nowrap;
        padding: 0px 6px 2px;

        &:first-child {
            width: 1%;
        }

        &:last-child {
            width: 99%;
        }
    }
    """
)

private val shortcutDescription = css(
    """
    font-weight: 300;
    line-height: 15px;
    """
)

private val shortcutKey = css(
    """
    display: inline-block;
    text-align: center;
    padding: 3px 4px;
    min-width: 24px;
    border-radius: 4px;
    background: var(--keyboard-content-key-bg);
    border: 1px solid var(--keyboard-content-key-border-color);
    font-size: 13px;
    color: var(--keyboard-content-key-color);
    line-height: 1.2;
    margin-right: 4px;

    &.bold {
        font-weight: bold;
    }
    """
)

/**
 * An HTML component for displaying keyboard shortcut hints.
 */
class KeyboardShortcuts private constructor() {
    private val root: Element?

    init {
        root = document.body?.Div(classes = keyboardShortcuts) {
            id = KEYBOARD_SHORTCUTS_ID
            CloseButton()
            Content()
        }
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

    private fun Element.Content() {
        val isMacOs = window.isCommandKeySupported()
        val metaCmdKey = if (isMacOs) "cmd" else "ctrl"
        val metaShiftKey = "shift"
        Div(classes = content) {
            Span(contentTitle, "Keyboard shortcuts")

            Div(classes = contentContainer) {
                Table(shortcutTable) {
                    style("width" to "200px")

                    Shortcut("Add Rectangle", "R")
                    Shortcut("Add Text", "T")
                    Shortcut("Add Line", "L")
                }
                Table(shortcutTable) {
                    style("width" to "270px")

                    Shortcut("Select tool", "V")
                    Shortcut("Select all", metaCmdKey, "A")
                    Shortcut("Deselect", "esc")
                    Shortcut("Undo", metaCmdKey, "Z")
                    Shortcut("Redo", metaCmdKey, metaShiftKey, "Z")
                }

                Table(shortcutTable) {
                    Shortcut("Duplicate", metaCmdKey, "D")
                    Shortcut("Copy as text", metaCmdKey, metaShiftKey, "C")
                    Shortcut("Remove shapes", "delete")
                    Shortcut("Edit selected text", "enter")
                }
            }
        }
    }

    private fun Element.Shortcut(description: String, vararg keys: String) {
        Row {
            Cell {
                for (key in keys) {
                    Key(key)
                }
            }
            Cell(classes = shortcutDescription, text = description)
        }
    }

    private fun Element.Key(key: String) {
        val bold = if (key.length == 1) "bold" else ""
        Span(classes = "$shortcutKey $bold", text = key)
    }

    private fun dismiss() {
        root?.remove()
    }

    companion object {
        private const val KEYBOARD_SHORTCUTS_ID = "keyboard-shortcuts"
        fun showHint() {
            if (document.getElementById(KEYBOARD_SHORTCUTS_ID) != null) {
                // Keyboard shortcuts is already shown.
                return
            }
            KeyboardShortcuts()
        }
    }
}
