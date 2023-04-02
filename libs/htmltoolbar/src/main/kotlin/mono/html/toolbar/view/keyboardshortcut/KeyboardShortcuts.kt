/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view.keyboardshortcut

import kotlinx.browser.document
import kotlinx.browser.window
import mono.common.isCommandKeySupported
import mono.html.Cell
import mono.html.Div
import mono.html.Row
import mono.html.Span
import mono.html.SvgPath
import mono.html.Table
import mono.html.setOnClickListener
import mono.html.style
import mono.html.toolbar.view.utils.SvgIcon
import org.w3c.dom.Element

/**
 * An HTML component for displaying keyboard shortcut hints.
 */
class KeyboardShortcuts private constructor() {
    private val root: Element?

    init {
        root = document.body?.Div(classes = "keyboard-shortcuts") {
            id = KEYBOARD_SHORTCUTS_ID
            CloseButton()
            Content()
        }
    }

    private fun Element.CloseButton() {
        Span(classes = "keyboard-shortcuts__close") {
            SvgIcon(16) {
                /* ktlint-disable max-line-length */
                SvgPath(
                    "M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z"
                )
                SvgPath(
                    "M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z"
                )
                /* ktlint-enable max-line-length */
            }

            setOnClickListener { dismiss() }
        }
    }

    private fun Element.Content() {
        val isMacOs = window.isCommandKeySupported()
        val metaCmdKey = if (isMacOs) "cmd" else "ctrl"
        val metaShiftKey = "shift"
        Div(classes = "keyboard-shortcuts__content") {
            Span("keyboard-shortcuts__content-title", "Keyboard shortcuts")

            Div(classes = "keyboard-shortcuts__content-container") {
                Table("keyboard-shortcuts__content-list") {
                    style("width" to "200px")

                    Shortcut("Add Rectangle", "R")
                    Shortcut("Add Text", "T")
                    Shortcut("Add Line", "L")
                }
                Table("keyboard-shortcuts__content-list") {
                    style("width" to "270px")

                    Shortcut("Select tool", "V")
                    Shortcut("Select all", metaCmdKey, "A")
                    Shortcut("Deselect", "esc")
                    Shortcut("Undo", metaCmdKey, "Z")
                    Shortcut("Redo", metaCmdKey, metaShiftKey, "Z")
                }

                Table("keyboard-shortcuts__content-list") {
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
            Cell(classes = "description", text = description)
        }
    }

    private fun Element.Key(key: String) {
        val bold = if (key.length == 1) "bold" else ""
        Span(classes = "key $bold", text = key)
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
