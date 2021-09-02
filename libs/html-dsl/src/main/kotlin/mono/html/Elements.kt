@file:Suppress("FunctionName")

package mono.html

import kotlinx.browser.document
import kotlinx.dom.addClass
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement

fun Element.Div(
    classes: String = "",
    block: HTMLDivElement.() -> Unit
): HTMLDivElement = Div(this, classes, block)

fun Div(
    parent: Element? = null,
    classes: String = "",
    block: HTMLDivElement.() -> Unit
): HTMLDivElement {
    val div = parent.createElement<HTMLDivElement>("div")
    div.addClass(classes)
    div.block()
    return div
}

fun Element.Span(classes: String = "", text: String): HTMLSpanElement = Span(this, classes, text)

fun Span(parent: Element? = null, classes: String = "", text: String): HTMLSpanElement {
    val span = parent.createElement<HTMLSpanElement>("span")
    span.innerText = text
    return span
}

private fun <T : Element> Element?.createElement(type: String): T {
    @Suppress("UNCHECKED_CAST")
    val element = document.createElement(type) as T
    this?.append(element)
    return element
}
