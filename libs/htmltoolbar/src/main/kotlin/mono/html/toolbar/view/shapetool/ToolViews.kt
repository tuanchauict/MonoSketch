@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import mono.html.Div
import mono.html.toolbar.view.isEnabled
import mono.html.toolbar.view.isVisible
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
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

internal open class ToolViewController(private val rootView: HTMLDivElement) {
    open fun setVisible(isVisible: Boolean) {
        rootView.isVisible = isVisible
    }

    open fun setEnabled(isEnabled: Boolean) {
        rootView.isEnabled = isEnabled
    }
}

internal fun Element.Section(
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
    block: HTMLDivElement.() -> Unit
): HTMLDivElement =
    Div(classes(TOOL, ADD_BOTTOM_SPACE x hasMoreBottomSpace)) {
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
