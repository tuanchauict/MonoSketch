/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal

import kotlinx.browser.document
import mono.common.Cancelable
import mono.common.Key
import mono.common.onKeyDown
import mono.common.setTimeout
import mono.html.Div
import mono.html.Input
import mono.html.InputType
import mono.html.Li
import mono.html.Ul
import mono.html.px
import mono.html.setOnClickListener
import mono.html.setOnFocusOut
import mono.html.style
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

/**
 * A modal for dropdown menu
 */
class DropDownMenu(
    private val items: List<Item>,
    private val onClickAction: (Item) -> Unit
) {
    private var menu: Element? = null

    private var dismissTimeout: Cancelable? = null

    fun show(anchor: Element) {
        val body = document.body ?: return
        val menu = body.Menu(items)
        menu.adjustPosition(anchor, body.clientWidth)

        this.menu = menu
        menu.run {
            val hiddenInput = Input(inputType = InputType.CHECK_BOX, classes = "hidden-input") {
                setOnFocusOut { dismiss() }
                focus()

                onKeyDown {
                    if (it.which == Key.KEY_ESC) {
                        dismiss()
                    }
                }
            }

            onmousedown = {
                setTimeout(5) {
                    dismissTimeout?.cancel()
                    hiddenInput.focus()
                }
            }
        }
    }

    private fun Element.adjustPosition(anchor: Element, maxWidth: Int) {
        val menuWidthPx = clientWidth
        val anchorRect = anchor.getBoundingClientRect()

        val leftPx =
            (anchorRect.left + anchorRect.width / 2 - menuWidthPx / 2).toInt()
                .coerceIn(4, maxWidth - menuWidthPx - 4)
        style(
            "left" to leftPx.px,
            "top" to anchorRect.bottom.px
        )
    }

    private fun Element.Menu(items: List<Item>): HTMLDivElement = Div(classes = "drop-down-menu") {
        Items(items)
    }

    private fun Element.Items(items: List<Item>) {
        val ul = Ul()

        for (item in items.filter { it.isVisible() }) {
            when (item) {
                is Item.Divider -> ul.Li(classes = "drop-down-divider")
                is Item.Text -> ul.Li(classes = "drop-down-item") {
                    innerText = item.title
                    setOnClickListener {
                        onClickAction(item)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun dismiss() {
        dismissTimeout = setTimeout(20) {
            menu?.remove()
        }
    }

    sealed class Item(internal val isVisible: () -> Boolean) {
        class Divider(isVisible: () -> Boolean = { true }) : Item(isVisible)
        class Text(val title: String, val key: Any, isVisible: () -> Boolean = { true }) :
            Item(isVisible)
    }
}
