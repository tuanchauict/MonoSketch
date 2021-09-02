package mono.html

import org.w3c.dom.Element
import org.w3c.dom.events.Event

fun Element.addOnClickListener(listener: (Event) -> Unit) = addEventListener("click", listener)

fun Element.appendElement(vararg children: Element) {
    for (node in children) {
        append(node)
    }
}
