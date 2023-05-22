/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html

import kotlinx.browser.document
import kotlinx.dom.addClass
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLLabelElement
import org.w3c.dom.HTMLPreElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.HTMLTableColElement
import org.w3c.dom.HTMLTableElement
import org.w3c.dom.HTMLTableRowElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.HTMLUListElement

fun Element.Div(
    classes: String = "",
    block: HTMLDivElement.() -> Unit = {}
): HTMLDivElement = Div(this, classes, block)

fun Div(
    parent: Element? = null,
    classes: String = "",
    block: HTMLDivElement.() -> Unit = {}
): HTMLDivElement = parent.createElement("div", classes, block)

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
): HTMLHeadingElement = parent.createElement(level.value, classes) {
    innerText = text
    block()
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
): HTMLPreElement = parent.createElement("pre", classes) {
    innerText = text
    block()
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
): HTMLSpanElement = parent.createElement("span", classes) {
    innerText = text
    block()
}

fun Element.Ul(classes: String = "", block: HTMLUListElement.() -> Unit = {}): HTMLUListElement =
    Ul(this, classes, block)

fun Ul(
    parent: Element?,
    classes: String = "",
    block: HTMLUListElement.() -> Unit = {}
): HTMLUListElement = parent.createElement("ul", classes, block)

fun Element.Li(classes: String = "", block: HTMLLIElement.() -> Unit = {}): HTMLLIElement =
    Li(this, classes, block)

fun Li(
    parent: Element?,
    classes: String = "",
    block: HTMLLIElement.() -> Unit = {}
): HTMLLIElement = parent.createElement("li", classes, block)

fun Element.A(
    classes: String = "",
    text: String = "",
    href: String = "",
    block: HTMLAnchorElement.() -> Unit
): HTMLAnchorElement = A(this, classes, text, href, block)

fun A(
    parent: Element?,
    classes: String = "",
    text: String = "",
    href: String = "",
    block: HTMLAnchorElement.() -> Unit
): HTMLAnchorElement = parent.createElement("a", classes) {
    innerText = text
    if (href.isNotEmpty()) {
        setAttributes("href" to href)
    }
    block()
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
): HTMLTextAreaElement = parent.createElement("textarea", classes) {
    textContent = content
    block()
}

fun Element.Input(
    inputType: InputType,
    classes: String = "",
    block: HTMLInputElement.() -> Unit = {}
): HTMLInputElement = Input(this, inputType, classes, block)

fun Input(
    parent: Element?,
    inputType: InputType,
    classes: String = "",
    block: HTMLInputElement.() -> Unit = {}
): HTMLInputElement = parent.createElement("input", classes) {
    type = inputType.value
    block()
}

enum class InputType(val value: String) {
    TEXT("text"),
    NUMBER("number"),
    FILE("file"),
    RADIO("radio"),
    CHECK_BOX("checkbox")
}

fun Element.Label(classes: String = "", block: HTMLLabelElement.() -> Unit): HTMLLabelElement =
    Label(this, classes, block)

fun Label(
    parent: Element?,
    classes: String = "",
    block: HTMLLabelElement.() -> Unit
): HTMLLabelElement = parent.createElement("label", classes, block)

fun Element.Canvas(
    classes: String = "",
    block: HTMLCanvasElement.() -> Unit = {}
): HTMLCanvasElement = Canvas(this, classes, block)

fun Canvas(
    parent: Element?,
    classes: String = "",
    block: HTMLCanvasElement.() -> Unit = {}
): HTMLCanvasElement = parent.createElement("canvas", classes, block)

fun Element.Table(
    classes: String = "",
    block: HTMLTableElement.() -> Unit
): HTMLTableElement = Table(this, classes, block)

fun Table(
    parent: Element?,
    classes: String = "",
    block: HTMLTableElement.() -> Unit
): HTMLTableElement = parent.createElement("table", classes, block)

fun Element.Row(
    classes: String = "",
    block: HTMLTableRowElement.() -> Unit
): HTMLTableRowElement = Row(this, classes, block)

fun Row(
    parent: Element?,
    classes: String,
    block: HTMLTableRowElement.() -> Unit
): HTMLTableRowElement = parent.createElement("tr", classes, block)

fun Element.Cell(
    classes: String = "",
    text: String = "",
    block: HTMLTableColElement.() -> Unit = {}
): HTMLTableColElement = Cell(this, classes, text, block)

fun Cell(
    parent: Element?,
    classes: String = "",
    text: String = "",
    block: HTMLTableColElement.() -> Unit = {}
): HTMLTableColElement = parent.createElement("td", classes) {
    innerText = text
    block()
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

private fun <T : Element> Element?.createElement(
    type: String,
    classes: String,
    block: T.() -> Unit = {}
): T {
    @Suppress("UNCHECKED_CAST")
    val element = document.createElement(type) as T
    element.addClass(classes)
    this?.append(element)
    element.block()
    return element
}

private fun Element?.createSvgElement(type: String, classes: String): Element {
    val element = document.createElementNS("http://www.w3.org/2000/svg", type)
    element.setAttribute("class", classes)
    this?.append(element)
    return element
}
