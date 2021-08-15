@file:Suppress("FunctionName")

package mono.html.toolbar.view

import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.li
import kotlinx.html.style
import kotlinx.html.ul
import mono.html.ext.Tag
import mono.html.ext.px
import mono.html.ext.styleOf
import mono.html.toolbar.OneTimeActionType

/**
 * An enum for right actions on drop down menu
 */
enum class RightAction(val title: String, val actionType: OneTimeActionType) {
    SAVE_AS("Save As...", OneTimeActionType.SaveShapesAs),
    OPEN_FILE("Open File...", OneTimeActionType.OpenShapes),
    EXPORT("Export Text", OneTimeActionType.ExportSelectedShapes)
}

/**
 * A function to create right toolbar UI.
 */
internal fun Tag.RightToolbar(
    onActionSelected: (RightAction) -> Unit
) {
    val dropdownMenuId = "right-toolbar-dropdown-menu"
    div("toolbar right") {
        div("dropdown") {
            button(classes = "btn btn-outline-secondary btn-sm toolbar-btn shadow-none") {
                id = dropdownMenuId
                attributes["data-bs-toggle"] = "dropdown"
                attributes["aria-expanded"] = "false"
                // Avoid input being focused which voids key event commands.
                attributes["onfocus"] = "this.blur()"

                style = styleOf("padding" to "0 3px")

                SvgIcon(16, 16) {
                    style = styleOf("margin-bottom" to 3.px)
                    /* ktlint-disable max-line-length */
                    SvgPath("M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z")
                    /* ktlint-enable max-line-length */
                }
            }
            ul("dropdown-menu dropdown-menu-light") {
                attributes["aria-labelledby"] = dropdownMenuId

                for (action in RightAction.values()) {
                    RightToolbarMenuItem(action.title) {
                        onActionSelected(action)
                    }
                }
            }
        }
    }
}

private fun Tag.RightToolbarMenuItem(
    title: String,
    onClickAction: () -> Unit
) {
    li {
        a(classes = "dropdown-item") {
            onClickFunction = { onClickAction() }
            +title
        }
    }
}
