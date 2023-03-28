@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import mono.html.Div
import mono.html.Span
import mono.html.bindClass
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

internal fun Element.Section(
    title: String = "",
    hasBorderTop: Boolean = true,
    block: HTMLDivElement.(sectionElement: Element) -> Unit
): HTMLDivElement = Div("section") {
    val sectionElement = this
    bindClass("notitle", title.isEmpty())
    bindClass("border-top", hasBorderTop)
    if (title.isNotEmpty()) {
        Div("section-title") {
            Span(text = title)
        }
    }

    Div("section-body") {
        block(sectionElement)
    }
}
