package mono.html.ui.toolbar

import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.li
import kotlinx.html.ul
import org.w3c.dom.HTMLDivElement

/**
 * A class to create and manage right toolbar UI.
 */
class RightToolbarView(container: HTMLDivElement) {
    init {
        val dropdownMenuId = "right-toolbar-dropdown-menu"
        container.append {
            div("dropdown") {
                button(classes = "btn btn-outline-primary btn-sm toolbar-btn shadow-none") {
                    id = dropdownMenuId
                    attributes["data-bs-toggle"] = "dropdown"
                    attributes["aria-expanded"] = "false"
                    +"•••"
                }
                ul("dropdown-menu dropdown-menu-light") {
                    attributes["aria-labelledby"] = dropdownMenuId

                    li {
                        a(classes = "dropdown-item") {
                            onClickFunction = { exportSelectedShapes() }
                            +"Export"
                        }
                    }
                }
            }
        }
    }

    private fun exportSelectedShapes() {
        TODO("Export selected shape")
    }
}
