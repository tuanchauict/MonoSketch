@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import mono.html.Div
import mono.html.Span
import mono.html.bindClass
import mono.html.toolbar.view.shapetool.Class.ADD_BOTTOM_SPACE
import mono.html.toolbar.view.shapetool.Class.CENTER_EVEN_SPACE
import mono.html.toolbar.view.shapetool.Class.CENTER_VERTICAL
import mono.html.toolbar.view.shapetool.Class.GRID
import mono.html.toolbar.view.shapetool.Class.MONOFONT
import mono.html.toolbar.view.shapetool.Class.ROW
import mono.html.toolbar.view.shapetool.Class.SECTION
import mono.html.toolbar.view.shapetool.Class.SECTION_TITLE
import mono.html.toolbar.view.shapetool.Class.SMALL_SPACE
import mono.html.toolbar.view.shapetool.Class.TOOL
import mono.html.toolbar.view.shapetool.Class.TOOL_NO_CHECK_BOX
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

internal fun Element.Section(
    title: String = "",
    hasBorderTop: Boolean = true,
    block: HTMLDivElement.() -> Unit
): HTMLDivElement = Div("section") {
    bindClass("notitle", title.isEmpty())
    bindClass("border-top", hasBorderTop)
    if (title.isNotEmpty()) {
        Div("section-title") {
            Span(text = title)
        }
    }

    Div("section-body") {

        block()
    }
}

internal fun Element.SectionObsolete(
    title: String,
    isSmallSpace: Boolean = false,
    block: HTMLDivElement.() -> Unit = {}
): HTMLDivElement =
    Div(classes(SECTION, SMALL_SPACE x isSmallSpace)) {
        if (title.isNotEmpty()) {
            Div(classes(SECTION_TITLE)) {
                innerText = title
            }
        }
        block()
    }

internal fun Element.Tool(
    hasMoreBottomSpace: Boolean = false,
    hasCheckBox: Boolean = true,
    block: HTMLDivElement.() -> Unit
): HTMLDivElement =
    Div(classes(TOOL, ADD_BOTTOM_SPACE x hasMoreBottomSpace, TOOL_NO_CHECK_BOX x !hasCheckBox)) {
        block()
    }

internal fun Element.Row(
    isMoreBottomSpaceRequired: Boolean = false,
    isCenterEvenSpace: Boolean = false,
    isVerticalCenter: Boolean = false,
    isMonoFont: Boolean = false,
    isGrid: Boolean = false,
    block: HTMLDivElement.() -> Unit
): HTMLDivElement {
    val classes = classes(
        ROW,
        ADD_BOTTOM_SPACE x isMoreBottomSpaceRequired,
        CENTER_EVEN_SPACE x isCenterEvenSpace,
        CENTER_VERTICAL x isVerticalCenter,
        GRID x isGrid,
        MONOFONT x isMonoFont
    )
    return Div(classes) {
        block()
    }
}
