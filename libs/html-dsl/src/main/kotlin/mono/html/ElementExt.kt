package mono.html

import org.w3c.dom.Document
import org.w3c.dom.Element

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

fun Element.style(vararg attrs: Pair<String, String>) = setAttributes("style" to styleOf(*attrs))

fun Document.select(query: String): Element = querySelector(query) as Element
