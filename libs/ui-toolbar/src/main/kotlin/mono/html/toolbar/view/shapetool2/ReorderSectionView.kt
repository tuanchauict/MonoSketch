/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.Composable
import mono.actionmanager.OneTimeActionType
import mono.html.modal.TooltipPosition
import mono.html.modal.tooltip
import mono.html.toolbar.view.components.Section
import mono.shape.command.ChangeOrder
import mono.ui.compose.ext.Svg
import mono.ui.compose.ext.SvgPath
import mono.ui.compose.ext.fill
import mono.ui.compose.ext.size
import mono.ui.compose.ext.viewBox
import org.jetbrains.compose.web.dom.Div

@Composable
internal fun ReorderSectionView(
    isVisible: Boolean,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {
    if (!isVisible) {
        return
    }
    Section(hasBorderTop = false) {
        Div(attrs = { classes("tool-reorder") }) {
            for (icon in ReorderIconType.values()) {
                Icon(icon, setOneTimeAction)
            }
        }
    }
}

@Composable
private fun Icon(iconType: ReorderIconType, setOneTimeAction: (OneTimeActionType) -> Unit) {
    Div(
        attrs = {
            classes("icon")
            tooltip(iconType.title, TooltipPosition.TOP)

            onClick { setOneTimeAction(OneTimeActionType.ReorderShape(iconType.changeOrderType)) }
        }
    ) {
        Svg(
            attrs = {
                size(18, 18)
                viewBox(18, 18)
                fill("currentColor")
            }
        ) {
            SvgPath(iconType.iconPath)
        }
    }
}

private enum class ReorderIconType(
    val changeOrderType: ChangeOrder.ChangeOrderType,
    val title: String,
    val iconPath: String
) {
    FRONT(
        ChangeOrder.ChangeOrderType.FRONT,
        "Bring to Front",
        "M18,18h-9V15h-6V9h-3V0h9V3h6V9h3v9h0Zm-4-4V4H4V14h10Z"
    ),
    UPWARD(
        ChangeOrder.ChangeOrderType.FORWARD,
        "Bring Forward",
        "M18,18h-12v-5h-6v-13h13v6h5v12h0Zm-17-6h11v-11h-11Z"
    ),
    BACKWARD(
        ChangeOrder.ChangeOrderType.BACKWARD,
        "Send Backward",
        "M6,18V13h-6V0h13V6h5V18Zm-5-6h5V6h6V1H1Z"
    ),
    BACK(
        ChangeOrder.ChangeOrderType.BACK,
        "Send to Back",
        "M9,18V15h-6V9h-3V0h9V3h6V9h3v9Zm-5-4h5V9h-5Zm10-5V4h-5V9Z"
    )
}
