@file:Suppress("FunctionName")

package mono.html.toolbar.view

import mono.html.A
import mono.html.Div
import mono.html.Li
import mono.html.SvgPath
import mono.html.Ul
import mono.html.px
import mono.html.styleOf
import mono.html.setAttributes
import mono.html.setOnClickListener
import mono.html.toolbar.OneTimeActionType
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

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
internal fun Element.RightToolbar(
    onActionSelected: (RightAction) -> Unit
) {
    val dropdownMenuId = "right-toolbar-dropdown-menu"
    Div("toolbar right") {
        Div("dropdown") {
            Icon(dropdownMenuId)
            Items(dropdownMenuId, onActionSelected)
        }
    }
}

private fun HTMLDivElement.Items(
    dropdownMenuId: String,
    onActionSelected: (RightAction) -> Unit
) {
    Ul("dropdown-menu dropdown-menu-light") {
        setAttributes("aria-labelledby" to dropdownMenuId)

        for (action in RightAction.values()) {
            RightToolbarMenuItem(action.title) {
                onActionSelected(action)
            }
        }
    }
}

private fun HTMLDivElement.Icon(dropdownMenuId: String) {
    Div(classes = "btn btn-outline-secondary btn-sm toolbar-btn shadow-none") {
        id = dropdownMenuId
        setAttributes(
            "data-bs-toggle" to "dropdown",
            "aria-expanded" to "false",
            // Avoid input being focused which voids key event commands.
            "onfocus" to "this.blur()",
            "style" to styleOf("padding" to "0 3px")
        )

        SvgIcon(16, 16) {
            setAttributes("style" to styleOf("margin-bottom" to 3.px))
            /* ktlint-disable max-line-length */
            SvgPath("M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z")
            /* ktlint-enable max-line-length */
        }
    }
}

private fun Element.RightToolbarMenuItem(
    title: String,
    onClickAction: () -> Unit
) {
    Li {
        A("dropdown-item", title) {
            setOnClickListener { onClickAction() }
        }
    }
}
