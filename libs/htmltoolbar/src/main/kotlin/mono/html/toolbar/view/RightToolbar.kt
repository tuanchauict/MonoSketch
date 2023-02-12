@file:Suppress("FunctionName")

package mono.html.toolbar.view

import mono.actionmanager.OneTimeActionType
import mono.html.Div
import mono.html.SvgPath
import mono.html.modal.DropDownMenu
import mono.html.modal.DropDownMenu.Item.Text
import mono.html.px
import mono.html.setAttributes
import mono.html.setOnClickListener
import mono.html.style
import mono.html.styleOf
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

/**
 * A function to create right toolbar UI.
 */
internal fun Element.RightToolbar(
    onActionSelected: (OneTimeActionType) -> Unit
) {
    Div("nav-toolbar nav-right") {
        DropDownMenuIcon {
            val items = listOf(
                Text("Save As...", OneTimeActionType.SaveShapesAs),
                Text("Open File...", OneTimeActionType.OpenShapes),
                Text("Export Text", OneTimeActionType.ExportSelectedShapes),
                DropDownMenu.Item.Divider,
                Text("Keyboard shortcuts", OneTimeActionType.ShowKeyboardShortcuts)
            )
            DropDownMenu("main-dropdown-menu", items) {
                val textItem = it as Text
                onActionSelected(textItem.key as OneTimeActionType)
            }
        }
    }
}

private fun HTMLDivElement.DropDownMenuIcon(onClickAction: () -> Unit) {
    Div {
        style(
            "cursor" to "pointer",
            "padding" to "4px 8px 4px 4px"
        )
        setAttributes("onfocus" to "this.blur()")

        SvgIcon(16) {
            setAttributes("style" to styleOf("margin-bottom" to 3.px))
            /* ktlint-disable max-line-length */
            SvgPath("M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z")
            /* ktlint-enable max-line-length */
        }

        setOnClickListener { onClickAction() }
    }
}
