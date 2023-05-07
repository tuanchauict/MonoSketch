/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import mono.actionmanager.RetainableActionType
import mono.html.modal.tooltip
import mono.ui.compose.ext.Svg
import mono.ui.compose.ext.SvgPath
import mono.ui.compose.ext.classes
import mono.ui.compose.ext.fill
import mono.ui.compose.ext.size
import mono.ui.compose.ext.viewBox
import org.jetbrains.compose.web.dom.Div

@Composable
internal fun MouseActionGroup(
    selectedActionState: State<RetainableActionType>,
    onActionSelected: (RetainableActionType) -> Unit
) {
    Div(
        attrs = {
            classes("main-mouse-actions")
        }
    ) {
        for (action in MouseActionType.values()) {
            MouseActionButton(
                action,
                action.retainableActionType == selectedActionState.value,
                onActionSelected
            )
        }
    }
}

@Composable
private fun MouseActionButton(
    actionType: MouseActionType,
    isSelected: Boolean,
    onActionSelected: (RetainableActionType) -> Unit
) {
    Div(
        attrs = {
            classes("action-button-container")

            tooltip(actionType.title)

            onClick { onActionSelected(actionType.retainableActionType) }
        }
    ) {
        Div(
            attrs = {
                classes(
                    "action-button" to true,
                    "selected" to isSelected
                )
            }
        ) {

            Svg(
                attrs = {
                    size(21, 21)
                    viewBox(24, 24)
                    fill("currentColor")
                }
            ) {
                SvgPath(actionType.iconPath)
            }
        }


    }
}

private enum class MouseActionType(
    val retainableActionType: RetainableActionType,
    val title: String,
    val iconPath: String
) {
    SELECTION(
        RetainableActionType.IDLE,
        title = "Select (V)",
        iconPath = "M7.436 20.61L7.275 3.914l12.296 11.29-7.165.235-4.97 5.168z"
    ),
    ADD_RECTANGLE(
        RetainableActionType.ADD_RECTANGLE,
        title = "Rectangle (R)",
        iconPath = "M22 19H2V5h20v14zM4 7v10h16V7z"
    ),
    ADD_TEXT(
        RetainableActionType.ADD_TEXT,
        title = "Text (T)",
        iconPath = "M5.635 21v-2h12.731v2zm3.27-4v-1.12h2.005V4.12H7.425l-.39.44v2.58h-1.4" +
            "V3h12.731v4.14h-1.4V4.56l-.39-.44h-3.485v11.76h2.005V17z"
    ),
    ADD_LINE(
        RetainableActionType.ADD_LINE,
        title = "Line (L)",
        iconPath = "M18 15v-2H6v2H0V9h6v2h12V9h6v6z"
    )
}
