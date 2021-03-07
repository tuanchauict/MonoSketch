package mono.common

import org.w3c.dom.HTMLCollection
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.get

inline fun <reified T : HTMLElement> HTMLElement.getOnlyElementByClassName(className: String) =
    getElementsByClassName(className).firstOrNull<T>()

inline fun <reified T : HTMLElement> HTMLCollection.firstOrNull(): T? =
    if (length > 0) get(0) as? T else null

fun HTMLElement.onClick(isClickThrough: Boolean = false, action: (MouseEvent) -> Unit) {
    onclick = {
        action(it)
        if (!isClickThrough) {
            it.stopPropagation()
        }
    }
}
