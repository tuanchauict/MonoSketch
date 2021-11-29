package mono.html.modal

import kotlinx.browser.document
import mono.common.setTimeout
import mono.html.Div
import mono.html.Input
import mono.html.InputType
import mono.html.Li
import mono.html.Ul
import mono.html.setOnClickListener
import mono.html.setOnFocusOut
import org.w3c.dom.Element

/**
 * A modal for dropdown menu
 */
class DropDownMenu(classes: String, items: List<Item>, private val onClickAction: (Any) -> Unit) {
    private val menu: Element?

    init {
        menu = document.body?.Div(classes = "drop-down-menu $classes") {
            initItems(items)

            Input(inputType = InputType.CHECK_BOX, classes = "hidden-input") {
                setOnFocusOut { dismiss() }
                focus()
            }
        }
    }

    private fun Element.initItems(items: List<Item>) {
        Ul {
            for (item in items) {
                when (item) {
                    Item.Divider -> Li(classes = "drop-down-divider")
                    is Item.Text -> Li(classes = "drop-down-item") {
                        innerText = item.title
                        setOnClickListener { onClickAction(item) }
                    }
                }
            }
        }
    }

    private fun dismiss() {
        setTimeout(120) {
            menu?.remove()
        }
    }

    sealed class Item {
        object Divider : Item()
        class Text(val title: String, val key: Any) : Item()
    }
}
