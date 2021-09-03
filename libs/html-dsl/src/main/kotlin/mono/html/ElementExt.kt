package mono.html

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

fun Element.setOnClickListener(listener: (Event) -> Unit) = addEventListener("click", listener)

fun Element.setOnMouseWheelListener(listener: (Event) -> Unit) =
    addEventListener("mousewheel", listener)

fun HTMLInputElement.setOnChangeListener(listener: (Event) -> Unit) =
    addEventListener("change", listener)

fun Element.appendElement(children: List<Element>) = appendElement(*children.toTypedArray())

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
