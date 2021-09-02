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
    val div = parent.createElement<HTMLDivElement>("div", classes)
    div.block()
    return div
}

fun Element.Span(classes: String = "", text: String): HTMLSpanElement = Span(this, classes, text)

fun Span(parent: Element? = null, classes: String = "", text: String): HTMLSpanElement {
    val span = parent.createElement<HTMLSpanElement>("span", classes)
    span.innerText = text
    return span
}

fun Element.Svg(classes: String = "", block: Element.() -> Unit): Element =
    Svg(this, classes, block)

fun Svg(parent: Element?, classes: String = "", block: Element.() -> Unit): Element {
    val svg = parent.createSvgElement("svg", classes)
    svg.block()
    return svg
}

fun Element.SvgPath(path: String): Element = SvgPath(this, path)

fun SvgPath(parent: Element?, path: String): Element {
    val node = parent.createSvgElement("path", "")
    node.setAttribute("d", path)
    return node
}

private fun <T : Element> Element?.createElement(type: String, classes: String): T {
    @Suppress("UNCHECKED_CAST")
    val element = document.createElement(type) as T
    element.addClass(classes)
    this?.append(element)
    return element
}

private fun Element?.createSvgElement(type: String, classes: String): Element {
    val element = document.createElementNS("http://www.w3.org/2000/svg", type)
    element.setAttribute("class", classes)
    this?.append(element)
    return element
}
