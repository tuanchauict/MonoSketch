/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool2.components

import androidx.compose.runtime.Composable
import mono.ui.compose.ext.classes
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun NumberTextField(
    label: String,
    defaultValue: Int,
    minValue: Int?,
    isChildBound: Boolean = false,
    isEnabled: Boolean = true,
    onValueChange: (Int?) -> Unit
) {
    Div(
        attrs = { classes("text-input-layout" to true, "child-bounds" to isChildBound) }
    ) {
        Span(
            attrs = {
                classes("text-input-label")
            }
        ) {
            Text(label)
        }

        Input(InputType.Number) {
            classes("text-input")
            defaultValue(defaultValue)
            if (minValue != null) {
                min(minValue.toString())
            }
            if (!isEnabled) {
                disabled()
            }
            onChange { onValueChange(it.value?.toInt()) }
        }
    }
}
