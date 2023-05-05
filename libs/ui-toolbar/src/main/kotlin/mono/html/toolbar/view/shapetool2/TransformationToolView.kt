/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.Composable
import mono.actionmanager.OneTimeActionType
import mono.graphics.geo.Rect
import mono.html.toolbar.view.shapetool2.components.NumberTextField
import mono.html.toolbar.view.shapetool2.components.Section
import org.jetbrains.compose.web.dom.Div

@Composable
internal fun TransformationToolView(
    bound: Rect?,
    isResizeable: Boolean,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {
    if (bound == null) {
        return
    }

    Section("TRANSFORM") {
        Div(
            attrs = {
                classes("transform-grid")
            }
        ) {
            NumberCell("X", bound.left) {
                setOneTimeAction(OneTimeActionType.ChangeShapeBound(newLeft = it))
            }
            NumberCell("Y", bound.top) {
                setOneTimeAction(OneTimeActionType.ChangeShapeBound(newTop = it))
            }
            NumberCell("W", bound.width, 1, isResizeable) {
                setOneTimeAction(OneTimeActionType.ChangeShapeBound(newWidth = it))
            }
            NumberCell("H", bound.height, 1, isResizeable) {
                setOneTimeAction(OneTimeActionType.ChangeShapeBound(newHeight = it))
            }
        }
    }
}

@Composable
private fun NumberCell(
    title: String,
    value: Int,
    minValue: Int? = null,
    isEnabled: Boolean = true,
    onValueChange: (Int?) -> Unit
) {
    Div(attrs = { classes("cell") }) {
        NumberTextField(
            title,
            value,
            minValue,
            isEnabled = isEnabled,
            onValueChange = onValueChange
        )
    }
}
