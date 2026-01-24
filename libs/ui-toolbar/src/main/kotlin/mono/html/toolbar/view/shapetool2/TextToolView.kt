/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.Composable
import mono.actionmanager.OneTimeActionType
import mono.html.toolbar.view.shapetool2.TextAlignmentIconType.HORIZONTAL_LEFT
import mono.html.toolbar.view.shapetool2.TextAlignmentIconType.HORIZONTAL_MIDDLE
import mono.html.toolbar.view.shapetool2.TextAlignmentIconType.HORIZONTAL_RIGHT
import mono.html.toolbar.view.shapetool2.TextAlignmentIconType.VERTICAL_BOTTOM
import mono.html.toolbar.view.shapetool2.TextAlignmentIconType.VERTICAL_MIDDLE
import mono.html.toolbar.view.shapetool2.TextAlignmentIconType.VERTICAL_TOP
import mono.html.toolbar.view.shapetool2.components.Section
import mono.shape.extra.style.TextAlign
import mono.ui.compose.ext.Svg
import mono.ui.compose.ext.SvgPath
import mono.ui.compose.ext.classes
import mono.ui.compose.ext.fill
import mono.ui.compose.ext.size
import mono.ui.compose.ext.viewBox
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun TextToolView(
    textAlign: TextAlign?,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {
    if (textAlign == null) {
        return
    }
    Section("TEXT") {
        Div(
            attrs = { classes("flex", "flex-col") }
        ) {
            val selectedAlignment = when (textAlign.horizontalAlign) {
                TextAlign.HorizontalAlign.LEFT -> HORIZONTAL_LEFT
                TextAlign.HorizontalAlign.MIDDLE -> HORIZONTAL_MIDDLE
                TextAlign.HorizontalAlign.RIGHT -> HORIZONTAL_RIGHT
            }
            Group(
                "Alignment",
                listOf(HORIZONTAL_LEFT, HORIZONTAL_MIDDLE, HORIZONTAL_RIGHT),
                selectedAlignment,
                setOneTimeAction
            )

            val selectedPosition = when (textAlign.verticalAlign) {
                TextAlign.VerticalAlign.TOP -> VERTICAL_TOP
                TextAlign.VerticalAlign.MIDDLE -> VERTICAL_MIDDLE
                TextAlign.VerticalAlign.BOTTOM -> VERTICAL_BOTTOM
            }
            Group(
                "Position",
                listOf(VERTICAL_TOP, VERTICAL_MIDDLE, VERTICAL_BOTTOM),
                selectedPosition,
                setOneTimeAction
            )
        }
    }
}

@Composable
private fun Group(
    label: String,
    icons: List<TextAlignmentIconType>,
    selectedIcon: TextAlignmentIconType,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {
    Div(attrs = { classes("flex", "items-center", "pb-2") }) {
        Span(
            attrs = {
                classes(
                    "w-[60px]",
                    "text-xs",
                    "select-none",
                    "text-[var(--shapetool-tool-title-color)]"
                )
            }
        ) {
            Text(label)
        }

        Div(attrs = { classes("flex", "flex-wrap", "gap-[7px]") }) {
            for (icon in icons) {
                Icon(icon.iconPath, icon == selectedIcon) {
                    setOneTimeAction(icon.toTextAlignment())
                }
            }
        }
    }
}

@Composable
private fun Icon(
    path: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Div(
        attrs = {
            classes(
                "flex",
                "justify-center",
                "items-center",
                "rounded",
                "cursor-pointer",
                "p-[3px]",
                // Base border
                "border",
                "border-[var(--comp-option-cloud-border-color)]",
                // Hover state
                "hover:border-transparent",
                "hover:outline",
                "hover:outline-[1.5px]",
                "hover:outline-[var(--comp-option-cloud-border-color)]",
                // Conditional: selected
                "border-transparent" to isSelected,
                "outline" to isSelected,
                "outline-[1.5px]" to isSelected,
                "outline-[var(--comp-option-cloud-border-selected-color)]" to isSelected,
                "text-[var(--comp-option-cloud-border-selected-color)]" to isSelected
            )

            onClick { onSelect() }
        }
    ) {
        Svg(
            attrs = {
                size(20, 14)
                viewBox(20, 14)
                fill("currentColor")
            }
        ) {
            SvgPath(path)
        }
    }
}

private enum class TextAlignmentIconType(
    val iconPath: String,
    val horizontalAlign: TextAlign.HorizontalAlign? = null,
    val verticalAlign: TextAlign.VerticalAlign? = null
) {
    HORIZONTAL_LEFT(
        "M3 2h14v2H3zM3 6h8v2H3zM3 10h10v2H3z",
        horizontalAlign = TextAlign.HorizontalAlign.LEFT
    ),
    HORIZONTAL_MIDDLE(
        "M3 2h14v2H3zM6 6h8v2H6zM5 10h10v2H5z",
        horizontalAlign = TextAlign.HorizontalAlign.MIDDLE
    ),
    HORIZONTAL_RIGHT(
        "M3 2h14v2H3zM9 6h8v2H9zM7 10h10v2H7z",
        horizontalAlign = TextAlign.HorizontalAlign.RIGHT
    ),
    VERTICAL_TOP(
        "M3 0h14v2H3zM3 4h14v2H3z",
        verticalAlign = TextAlign.VerticalAlign.TOP
    ),
    VERTICAL_MIDDLE(
        "M3 4h14v2H3zM3 8h14v2H3z",
        verticalAlign = TextAlign.VerticalAlign.MIDDLE
    ),
    VERTICAL_BOTTOM(
        "M3 8h14v2H3zM3 12h14v2H3z",
        verticalAlign = TextAlign.VerticalAlign.BOTTOM
    );

    fun toTextAlignment(): OneTimeActionType.TextAlignment =
        OneTimeActionType.TextAlignment(horizontalAlign, verticalAlign)
}
