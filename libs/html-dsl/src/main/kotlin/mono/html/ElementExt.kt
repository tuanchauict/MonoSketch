package mono.html

import org.w3c.dom.Element
import org.w3c.dom.events.Event

fun Element.setOnClickListener(listener: (Event) -> Unit) = addEventListener("click", listener)

fun Element.appendElement(vararg children: Element) {
    for (node in children) {
        append(node)
    }
}

fun Element.setAttributes(vararg attrs: Pair<String, Any>) {
    for ((key, value) in attrs) {
        setAttribute(key, value.toString())
    }
}
