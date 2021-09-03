@file:Suppress("FunctionName")

package mono.html

import kotlinx.browser.document
import kotlinx.dom.addClass
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLLabelElement
import org.w3c.dom.HTMLPreElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.HTMLUListElement

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

fun Element.Heading(
    level: HeadingLevel,
    classes: String = "",
    text: String = "",
    block: HTMLHeadingElement.() -> Unit
): HTMLHeadingElement = Heading(this, level, classes, text, block)

fun Heading(
    parent: Element?,
    level: HeadingLevel,
    classes: String = "",
    text: String = "",
    block: HTMLHeadingElement.() -> Unit
): HTMLHeadingElement {
    val heading = parent.createElement<HTMLHeadingElement>(level.value, classes)
    heading.innerText = text
    heading.block()
    return heading
}

enum class HeadingLevel(val value: String) {
    H1("h1"), H2("h2"), H3("H3"), H4("h4"), H5("h5")
}

fun Element.Pre(
    classes: String = "",
    text: String = "",
    block: HTMLPreElement.() -> Unit = {}
): HTMLPreElement = Pre(this, classes, text, block)

fun Pre(
    parent: Element?,
    classes: String = "",
    text: String = "",
    block: HTMLPreElement.() -> Unit = {}
): HTMLPreElement {
    val pre = parent.createElement<HTMLPreElement>("pre", classes)
    pre.innerText = text
    pre.block()
    return pre
}

fun Element.Span(
    classes: String = "",
    text: String = "",
    block: HTMLSpanElement.() -> Unit = {}
): HTMLSpanElement = Span(this, classes, text, block)

fun Span(
    parent: Element? = null,
    classes: String = "",
    text: String = "",
    block: HTMLSpanElement.() -> Unit = {}
): HTMLSpanElement {
    val span = parent.createElement<HTMLSpanElement>("span", classes)
    span.innerText = text
    span.block()
    return span
}

fun Element.Ul(classes: String = "", block: HTMLUListElement.() -> Unit): HTMLUListElement =
    Ul(this, classes, block)

fun Ul(
    parent: Element?,
    classes: String = "",
    block: HTMLUListElement.() -> Unit
): HTMLUListElement {
    val ul = parent.createElement<HTMLUListElement>("ul", classes)
    ul.block()
    return ul
}

fun Element.Li(classes: String = "", block: HTMLLIElement.() -> Unit): HTMLLIElement =
    Li(this, classes, block)

fun Li(
    parent: Element?,
    classes: String = "",
    block: HTMLLIElement.() -> Unit
): HTMLLIElement {
    val li = parent.createElement<HTMLLIElement>("li", classes)
    li.block()
    return li
}

fun Element.A(
    classes: String = "",
    text: String = "",
    block: HTMLAnchorElement.() -> Unit
): HTMLAnchorElement = A(this, classes, text, block)

fun A(
    parent: Element?,
    classes: String = "",
    text: String = "",
    block: HTMLAnchorElement.() -> Unit
): HTMLAnchorElement {
    val anchor = parent.createElement<HTMLAnchorElement>("a", classes)
    anchor.innerText = text
    anchor.block()
    return anchor
}

fun Element.TextArea(
    classes: String = "",
    content: String,
    block: HTMLTextAreaElement.() -> Unit = {}
): HTMLTextAreaElement = TextArea(this, classes, content, block)

fun TextArea(
    parent: Element?,
    classes: String,
    content: String,
    block: HTMLTextAreaElement.() -> Unit = {}
): HTMLTextAreaElement {
    val textArea = parent.createElement<HTMLTextAreaElement>("textarea", classes)
    textArea.textContent = content
    textArea.block()
    return textArea
}

fun Element.InputNumber(
    classes: String = "",
    block: HTMLInputElement.() -> Unit
): HTMLInputElement = InputNumber(this, classes, block)

fun InputNumber(
    parent: Element?,
    classes: String = "",
    block: HTMLInputElement.() -> Unit
): HTMLInputElement {
    val input = parent.createElement<HTMLInputElement>("input", classes)
    input.type = "number"
    input.block()
    return input
}

fun Element.CheckBox(classes: String = "", block: HTMLInputElement.() -> Unit): HTMLInputElement =
    CheckBox(this, classes, block)

fun CheckBox(
    parent: Element?,
    classes: String = "",
    block: HTMLInputElement.() -> Unit
): HTMLInputElement {
    val radio = parent.createElement<HTMLInputElement>("input", classes)
    radio.type = "checkbox"
    radio.block()
    return radio
}

fun Element.Radio(
    classes: String = "",
    block: HTMLInputElement.() -> Unit
): HTMLInputElement = Radio(this, classes, block)

fun Radio(
    parent: Element?,
    classes: String = "",
    block: HTMLInputElement.() -> Unit
): HTMLInputElement {
    val radio = parent.createElement<HTMLInputElement>("input", classes)
    radio.type = "radio"
    radio.block()
    return radio
}

fun Element.Label(classes: String = "", block: HTMLLabelElement.() -> Unit): HTMLLabelElement =
    Label(this, classes, block)

fun Label(
    parent: Element?,
    classes: String = "",
    block: HTMLLabelElement.() -> Unit
): HTMLLabelElement {
    val label = parent.createElement<HTMLLabelElement>("label", classes)
    label.block()
    return label
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
