/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.Composable
import mono.actionmanager.OneTimeActionType
import mono.common.Characters
import mono.html.modal.TooltipPosition
import mono.html.modal.tooltip
import mono.html.toolbar.view.shapetool2.components.NumberTextField
import mono.html.toolbar.view.shapetool2.components.Section
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle
import mono.shape.extra.style.StraightStrokeDashPattern
import mono.ui.compose.components.Icons
import mono.ui.compose.ext.classes
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
internal fun AppearanceToolView(
    viewModel: ShapeToolViewModel,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {
    if (!viewModel.appearanceVisibilityState.value) {
        return
    }
    Section("APPEARANCE") {
        Tool(
            title = "Fill",
            isAvailable = viewModel.shapeFillTypeState.value != null
        ) {
            OptionsCloud(
                viewModel.fillOptions,
                '×',
                viewModel.shapeFillTypeState.value,
                OneTimeActionType::ChangeShapeFillExtra,
                setOneTimeAction
            )
        }

        Tool(
            "Border",
            isAvailable = viewModel.shapeBorderTypeState.value != null
        ) {
            Div(attrs = { classes("flex", "flex-row", "items-center", "justify-start", "gap-4") }) {
                OptionsCloud(
                    viewModel.strokeOptions,
                    Characters.NBSP,
                    viewModel.shapeBorderTypeState.value,
                    OneTimeActionType::ChangeShapeBorderExtra,
                    setOneTimeAction
                )

                RoundedCorner(
                    viewModel.shapeBorderTypeState.value?.selectedId,
                    viewModel.shapeBorderRoundedCornerState.value
                ) { setOneTimeAction(OneTimeActionType.ChangeShapeBorderCornerExtra(it)) }
            }
            DashPattern(
                viewModel.shapeBorderDashTypeState.value,
                OneTimeActionType::ChangeShapeBorderDashPatternExtra,
                setOneTimeAction
            )
        }

        Tool(
            "Stroke",
            isAvailable = viewModel.lineStrokeTypeState.value != null
        ) {
            Div(attrs = { classes("flex", "flex-row", "items-center", "justify-start", "gap-4") }) {
                OptionsCloud(
                    viewModel.strokeOptions,
                    Characters.NBSP,
                    viewModel.lineStrokeTypeState.value,
                    OneTimeActionType::ChangeLineStrokeExtra,
                    setOneTimeAction
                )
                RoundedCorner(
                    viewModel.lineStrokeTypeState.value?.selectedId,
                    viewModel.lineStrokeRoundedCornerState.value
                ) { setOneTimeAction(OneTimeActionType.ChangeLineStrokeCornerExtra(it)) }
            }

            DashPattern(
                viewModel.lineStrokeDashTypeState.value,
                OneTimeActionType::ChangeLineStrokeDashPatternExtra,
                setOneTimeAction
            )
        }

        Tool(
            "Start head",
            isAvailable = viewModel.lineStartHeadState.value != null
        ) {
            OptionsCloud(
                viewModel.headOptions,
                Characters.NBSP,
                viewModel.lineStartHeadState.value,
                OneTimeActionType::ChangeLineStartAnchorExtra,
                setOneTimeAction
            )
        }

        Tool(
            "End head",
            isAvailable = viewModel.lineEndHeadState.value != null
        ) {
            OptionsCloud(
                viewModel.headOptions,
                Characters.NBSP,
                viewModel.lineEndHeadState.value,
                OneTimeActionType::ChangeLineEndAnchorExtra,
                setOneTimeAction
            )
        }
    }
}

@Composable
private fun Tool(
    title: String,
    isAvailable: Boolean,
    content: ContentBuilder<HTMLDivElement>
) {
    if (!isAvailable) {
        return
    }
    Div(
        attrs = { classes("flex", "flex-col", "mb-4") }
    ) {
        Span(
            attrs = {
                classes(
                    "text-xs",
                    "select-none",
                    "mb-1",
                    "text-[var(--shapetool-tool-title-color)]"
                )
            }
        ) {
            Text(title)
        }
        content()
    }
}

@Composable
private fun OptionsCloud(
    options: List<AppearanceOptionItem>,
    disabledStateText: Char,
    selectionState: CloudItemSelectionState?,
    oneTimeActionFactory: (Boolean, String?) -> OneTimeActionType,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {
    if (selectionState == null) {
        return
    }
    Div(
        attrs = { classes("flex", "flex-wrap", "gap-[7px]") }
    ) {
        Option(
            disabledStateText.toString(),
            isDashBorder = disabledStateText != Characters.NBSP,
            isSelected = !selectionState.isChecked
        ) {
            setOneTimeAction(oneTimeActionFactory(false, null))
        }
        for (option in options) {
            Option(
                option.name,
                selectionState.isChecked && selectionState.selectedId == option.id
            ) {
                setOneTimeAction(oneTimeActionFactory(true, option.id))
            }
        }
    }
}

@Composable
private fun Option(
    text: String,
    isSelected: Boolean,
    isDashBorder: Boolean = false,
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
                // Conditional: dash border
                "border-dashed" to isDashBorder,
                "hover:outline-dashed" to isDashBorder,
                // Conditional: selected
                "border-transparent" to isSelected,
                "outline" to isSelected,
                "outline-[1.5px]" to isSelected,
                "outline-[var(--comp-option-cloud-border-selected-color)]" to isSelected,
                "text-[var(--comp-option-cloud-border-selected-color)]" to isSelected,
                "outline-dashed" to (isSelected && isDashBorder)
            )

            onClick { onSelect() }
        }
    ) {
        Span(
            attrs = { classes("font-mono", "text-lg") }
        ) {
            Text(text)
        }
    }
}

@Composable
private fun DashPattern(
    dashPattern: StraightStrokeDashPattern?,
    oneTimeActionFactory: (Int?, Int?, Int?) -> OneTimeActionType,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {
    if (dashPattern == null) {
        return
    }

    Div(
        attrs = { classes("mt-2", "ml-2", "flex", "justify-between") }
    ) {
        DashInput("Dash", dashPattern.dash, 1) {
            setOneTimeAction(oneTimeActionFactory(it, null, null))
        }
        DashInput("Gap", dashPattern.gap, 0) {
            setOneTimeAction(oneTimeActionFactory(null, it, null))
        }
        DashInput("Shift", dashPattern.offset, null) {
            setOneTimeAction(oneTimeActionFactory(null, null, it))
        }
    }
}

@Composable
private fun DashInput(name: String, value: Int, minValue: Int?, onValueChange: (Int?) -> Unit) {
    Div(attrs = { classes("flex", "items-center", "w-[30%]") }) {
        NumberTextField(name, value, minValue, isChildBound = true, onValueChange = onValueChange)
    }
}

@Composable
private fun RoundedCorner(
    selectedStrokeId: String?,
    isRounded: Boolean?,
    onValueChange: (Boolean) -> Unit
) {
    if (isRounded == null || !PredefinedStraightStrokeStyle.isCornerRoundable(selectedStrokeId)) {
        return
    }
    Div(attrs = {
        classes("w-px", "h-4/5", "bg-[var(--shapetool-section-divider-color)]")
    })

    Div {
        Div(
            attrs = {
                classes(
                    // Base layout from cloud-item
                    "flex",
                    "justify-center",
                    "items-center",
                    "rounded",
                    "cursor-pointer",
                    // Corner-specific styles
                    "border-none",
                    "p-0.5",
                    "opacity-75",
                    // Hover state
                    "hover:opacity-100",
                    "hover:outline",
                    "hover:outline-1",
                    "hover:outline-[var(--text-input-border-color)]",
                    // Conditional: selected
                    "opacity-100" to isRounded,
                    "outline-none" to isRounded
                )
                tooltip("Rounded corner", position = TooltipPosition.TOP)

                onClick {
                    onValueChange(!isRounded)
                }
            }
        ) {
            Icons.RoundedCorner(iconSize = 26)
        }
    }
}
