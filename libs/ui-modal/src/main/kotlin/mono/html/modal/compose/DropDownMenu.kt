/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import mono.html.px
import mono.html.style
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.Element

/**
 * Show drop down menu at the bottom of the anchor, horizontal center aligned with the anchor.
 */
fun DropDownMenu(
    anchor: Element,
    items: List<DropDownItem>,
    onClick: (DropDownItem) -> Unit
) {
    NoBackgroundModal(
        attrs = {
            classes("drop-down-menu")

            ref {
                it.adjustPosition(anchor)
                onDispose { }
            }
        }
    ) {
        Ul {
            for (item in items) {
                if (!item.isVisible()) {
                    continue
                }
                when (item) {
                    is DropDownItem.Divider -> Divider()
                    is DropDownItem.Text -> Item(item) {
                        onClick(it)
                        dismiss()
                    }
                }
            }
        }
    }
}

@Composable
private fun Divider() {
    Li(attrs = { classes("drop-down-divider") })
}

@Composable
private fun Item(item: DropDownItem.Text, onClick: (DropDownItem) -> Unit) {
    Li(
        attrs = {
            classes("drop-down-item")
            onClick { onClick(item) }
        }
    ) {
        Text(item.title)
    }
}

private fun Element.adjustPosition(anchor: Element) {
    val maxWidth = document.body?.clientWidth ?: return
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

sealed class DropDownItem(internal val isVisible: () -> Boolean) {
    class Divider(isVisible: () -> Boolean = { true }) : DropDownItem(isVisible)
    class Text(val title: String, val key: Any, isVisible: () -> Boolean = { true }) :
        DropDownItem(isVisible)
}
