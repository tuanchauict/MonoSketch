@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.InputType
import kotlinx.html.js.div
import kotlinx.html.js.input
import kotlinx.html.js.span
import mono.html.toolbar.view.Tag
import mono.html.toolbar.view.shapetool.Class.ADD_RIGHT_SPACE
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.DISABLED
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import mono.html.toolbar.view.shapetool.Class.INPUT_CHECK_BOX
import mono.html.toolbar.view.shapetool.Class.SELECTED
import mono.html.toolbar.view.shapetool.Class.SMALL
import mono.html.toolbar.view.shapetool.Class.TOOL_TITLE

internal fun Tag.AppearanceSection() {
    Section("APPEARANCE") {
        GridTextIconOptions(
            "Fill",
            isSelected = false,
            selectedPosition = 0,
            listOf(" ", "█", "▒", "░", "▚")
        )
        GridTextIconOptions(
            "Border",
            isSelected = true,
            selectedPosition = 0,
            listOf("─", "━", "═", "█", "▒", "░")
        )
        GridTextIconOptions(
            "Start head",
            isSelected = true,
            selectedPosition = 0,
            listOf("▶", "■", "○", "◎", "●")
        )
        GridTextIconOptions(
            "End head",
            isSelected = true,
            selectedPosition = 0,
            listOf("▶", "■", "○", "◎", "●")
        )
    }
}

private fun Tag.GridTextIconOptions(
    title: String,
    isSelected: Boolean,
    selectedPosition: Int,
    options: List<String>
) {
    Tool(hasMoreBottomSpace = true) {
        Row {
            CheckColumn(isSelected)
            OptionsColumn(title, isSelected, selectedPosition, options)
        }
    }
}

private fun Tag.CheckColumn(isChecked: Boolean) {
    Column(hasMoreRightSpace = true) {
        Row {
            input(InputType.checkBox, classes = classes(INPUT_CHECK_BOX)) {
                checked = isChecked
            }
        }
    }
}

private fun Tag.OptionsColumn(
    title: String,
    isEnabled: Boolean,
    selectedPosition: Int,
    options: List<String>
) {
    Column {
        Row {
            span(classes(TOOL_TITLE)) { +title }
        }
        Row(isMonoFont = true, isGrid = true) {
            options.forEachIndexed { index, option ->
                TextButton(option, selectedPosition == index, isEnabled)
            }
        }
    }
}

private fun Tag.Column(hasMoreRightSpace: Boolean = false, block: Tag.() -> Unit) {
    val columnClasses = classes(COLUMN, ADD_RIGHT_SPACE x hasMoreRightSpace)
    div(columnClasses) {
        block()
    }
}

private fun Tag.TextButton(text: String, isSelected: Boolean, isEnabled: Boolean) {
    val buttonClasses =
        classes(
            ICON_BUTTON,
            SMALL,
            DISABLED x !isEnabled,
            SELECTED x (isSelected && isEnabled)
        )
    span(buttonClasses) { +text }
}
