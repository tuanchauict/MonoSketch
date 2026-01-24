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
        attrs = {
            classes(
                // Layout
                "flex",
                "items-center",
                "py-px",
                "pr-0.5",
                "border",
                "border-transparent",
                "rounded-sm",
                // Padding-left: 0 for child-bound (dash pattern), 8px otherwise
                "pl-0" to isChildBound,
                "pl-2" to !isChildBound,
                // Parent-bound styles (hover/focus on container)
                "hover:outline" to !isChildBound,
                "hover:outline-1" to !isChildBound,
                "hover:outline-[var(--text-input-border-color)]" to !isChildBound,
                "focus-within:outline" to !isChildBound,
                "focus-within:outline-2" to !isChildBound,
                "focus-within:outline-[var(--text-input-border-focus-color)]" to !isChildBound
            )
        }
    ) {
        Span(
            attrs = {
                classes(
                    "text-xs",
                    "font-light",
                    "select-none",
                    "text-[var(--text-input-label-color)]"
                )
            }
        ) {
            Text(label)
        }

        Input(InputType.Number) {
            classes(
                "h-6",
                "w-full",
                "border-none",
                "text-[var(--text-input-value-color)]",
                "bg-transparent",
                "text-xs",
                "focus:outline-none",
                "active:outline-none",
                // Margin-left: 6px for child-bound (dash pattern), 12px otherwise
                "ml-1.5" to isChildBound,
                "ml-3" to !isChildBound,
                // Child-bound styles (hover/focus on input itself)
                "px-1" to isChildBound,
                "rounded-sm" to isChildBound,
                "hover:outline" to isChildBound,
                "hover:outline-1" to isChildBound,
                "hover:outline-[var(--text-input-border-color)]" to isChildBound,
                "focus:outline" to isChildBound,
                "focus:outline-2" to isChildBound,
                "focus:outline-[var(--text-input-border-focus-color)]" to isChildBound,
                // Hide number input arrows
                "[appearance:textfield]",
                "[&::-webkit-outer-spin-button]:appearance-none",
                "[&::-webkit-inner-spin-button]:appearance-none",
                "[&::-webkit-outer-spin-button]:m-0",
                "[&::-webkit-inner-spin-button]:m-0"
            )
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
