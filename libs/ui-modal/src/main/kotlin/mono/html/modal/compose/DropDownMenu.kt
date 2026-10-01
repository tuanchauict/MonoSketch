/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal.compose

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import mono.html.px
import mono.html.style
import mono.scopedcss.css
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.Element

private val dropDownMenu = css(
    """
    min-width: 180px;
    z-index: var(--dropdown-menu-zindex);
    background: var(--modal-container-bg);
    border-radius: 6px;
    border: 1px solid var(--mainmenu-divider-color);
    padding: 6px 0;
    position: fixed;
    font-family: var(--monospace-font);

    ul {
        list-style: none;
        margin: 0;
        padding: 0;
    }
    """
)

private val dropDownDivider = css(
    """
    border-top: 1px solid var(--mainmenu-divider-color);
    margin: 8px 0;
    """
)

private val dropDownItem = css(
    """
    padding: 5px 8px 5px 16px;
    margin: 0;
    cursor: pointer;
    overflow: hidden;
    color: var(--mainmenu-item-color);
    white-space: nowrap;
    font-size: 14px;
    line-height: 21px;

    &:hover {
        background: var(--mainmenu-item-hover-bg);
    }

    &:last-child {
        border-bottom: none;
    }
    """
)

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
            classes(dropDownMenu)

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

                    is DropDownItem.Custom -> {
                        item.content.invoke(this@NoBackgroundModal)
                    }
                }
            }
        }
    }
}

@Composable
private fun Divider() {
    Li(attrs = { classes(dropDownDivider) })
}

@Composable
private fun Item(item: DropDownItem.Text, onClick: (DropDownItem) -> Unit) {
    Li(
        attrs = {
            classes(dropDownItem)
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

    class Custom(
        isVisible: () -> Boolean,
        internal val content: @Composable ModalElementScope.() -> Unit
    ) : DropDownItem(isVisible)
}
