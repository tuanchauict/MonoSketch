@file:Suppress("FunctionName")

package mono.html.toolbar.view

import mono.html.Div
import mono.html.SvgPath
import mono.html.modal.DropDownMenu
import mono.html.modal.DropDownMenu.Item.Text
import mono.html.px
import mono.html.setAttributes
import mono.html.setOnClickListener
import mono.html.styleOf
import mono.html.toolbar.OneTimeActionType
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

/**
 * A function to create right toolbar UI.
 */
internal fun Element.RightToolbar(
    onActionSelected: (OneTimeActionType) -> Unit
) {
    val dropdownMenuId = "right-toolbar-dropdown-menu"
    Div("toolbar right") {
        Div("dropdown") {
            Icon(dropdownMenuId) {
                val items = listOf(
                    Text("Save As...", OneTimeActionType.SaveShapesAs),
                    Text("Open File...", OneTimeActionType.OpenShapes),
                    Text("Export Text", OneTimeActionType.ExportSelectedShapes),
                    DropDownMenu.Item.Divider,
                    Text("Keyboard shortcuts", OneTimeActionType.ShowKeyboardShortcuts)
                )
                DropDownMenu("main-menu", items) {
                    val textItem = it as Text
                    onActionSelected(textItem.key as OneTimeActionType)
                }
            }
        }
    }
}

private fun HTMLDivElement.Icon(dropdownMenuId: String, onClickAction: () -> Unit) {
    Div(classes = "btn btn-outline-secondary btn-sm toolbar-btn shadow-none") {
        id = dropdownMenuId
        setAttributes(
            "onfocus" to "this.blur()",
            "style" to styleOf("padding" to "0 3px")
        )

        SvgIcon(16, 16) {
            setAttributes("style" to styleOf("margin-bottom" to 3.px))
            /* ktlint-disable max-line-length */
            SvgPath("M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z")
            /* ktlint-enable max-line-length */
        }

        setOnClickListener { onClickAction() }
    }
}
