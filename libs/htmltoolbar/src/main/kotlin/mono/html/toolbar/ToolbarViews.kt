@file:Suppress("FunctionName")

package mono.html.toolbar

import kotlinx.html.TagConsumer
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.li
import kotlinx.html.ul
import org.w3c.dom.HTMLElement

/**
 * A function to create and manage right toolbar UI.
 */
internal fun TagConsumer<HTMLElement>.RightToolbar(exportSelectedShapes: () -> Unit) {
    val dropdownMenuId = "right-toolbar-dropdown-menu"
    div("toolbar right") {
        div("dropdown") {
            button(classes = "btn btn-outline-primary btn-sm toolbar-btn shadow-none") {
                id = dropdownMenuId
                attributes["data-bs-toggle"] = "dropdown"
                attributes["aria-expanded"] = "false"
                +"•••"
            }
            ul("dropdown-menu dropdown-menu-light") {
                attributes["aria-labelledby"] = dropdownMenuId
                RightToolbarMenuItem("Export", exportSelectedShapes)
            }
        }
    }
}

internal fun TagConsumer<HTMLElement>.RightToolbarMenuItem(title: String, onClickAction: () -> Unit) {
    li {
        a(classes = "dropdown-item") {
            onClickFunction = { onClickAction() }
            +title
        }
    }
}
