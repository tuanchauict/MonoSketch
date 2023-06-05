/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.Composable
import mono.actionmanager.OneTimeActionType
import mono.common.Characters
import mono.html.toolbar.view.shapetool2.components.NumberTextField
import mono.html.toolbar.view.shapetool2.components.Section
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle
import mono.shape.extra.style.StraightStrokeDashPattern
import mono.ui.compose.ext.classes
import org.jetbrains.compose.web.dom.CheckboxInput
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
            OptionsCloud(
                viewModel.strokeOptions,
                Characters.NBSP,
                viewModel.shapeBorderTypeState.value,
                OneTimeActionType::ChangeShapeBorderExtra,
                setOneTimeAction
            )
            DashPattern(
                viewModel.shapeBorderDashTypeState.value,
                OneTimeActionType::ChangeShapeBorderDashPatternExtra,
                setOneTimeAction
            )
            RoundedCorner(
                viewModel.shapeBorderTypeState.value?.selectedId,
                viewModel.shapeBorderRoundedCornerState.value
            ) { setOneTimeAction(OneTimeActionType.ChangeShapeBorderCornerExtra(it)) }
        }

        Tool(
            "Stroke",
            isAvailable = viewModel.lineStrokeTypeState.value != null
        ) {
            OptionsCloud(
                viewModel.strokeOptions,
                Characters.NBSP,
                viewModel.lineStrokeTypeState.value,
                OneTimeActionType::ChangeLineStrokeExtra,
                setOneTimeAction
            )

            DashPattern(
                viewModel.lineStrokeDashTypeState.value,
                OneTimeActionType::ChangeLineStrokeDashPatternExtra,
                setOneTimeAction
            )

            RoundedCorner(
                viewModel.lineStrokeTypeState.value?.selectedId,
                viewModel.lineStrokeRoundedCornerState.value
            ) { setOneTimeAction(OneTimeActionType.ChangeLineStrokeCornerExtra(it)) }
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
        attrs = { classes("tool-appearance") }
    ) {
        Span(attrs = { classes("tool-title") }) {
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
        attrs = { classes("comp-option-cloud-layout") }
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
                "cloud-item" to true,
                "dash-border" to isDashBorder,
                "selected" to isSelected
            )

            onClick { onSelect() }
        }
    ) {
        Span(
            attrs = { classes("monofont") }
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
        attrs = { classes("comp-dash-layout") }
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
    Div(attrs = { classes("pattern") }) {
        NumberTextField(name, value, minValue, isChildBound = true, onValueChange = onValueChange)
    }
}

// TODO: This is temporary UI.
@Composable
private fun RoundedCorner(
    selectedStrokeId: String?,
    isRounded: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    println("Rounded corner: $selectedStrokeId $isRounded")
    if (!PredefinedStraightStrokeStyle.isCornerRoundable(selectedStrokeId)) {
        return
    }
    Div(
        attrs = {
            onClick {
                onValueChange(!isRounded)
            }
        }
    ) {
        CheckboxInput(isRounded)
        Text("Rounded corner")
    }
}
