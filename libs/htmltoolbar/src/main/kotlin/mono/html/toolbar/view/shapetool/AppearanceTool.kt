@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.InputType
import kotlinx.html.js.div
import kotlinx.html.js.input
import kotlinx.html.js.span
import mono.html.toolbar.view.Tag
import mono.html.toolbar.view.isEnabled
import mono.html.toolbar.view.isSelected
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.ToolType
import mono.html.toolbar.view.shapetool.Class.ADD_RIGHT_SPACE
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import mono.html.toolbar.view.shapetool.Class.INPUT_CHECK_BOX
import mono.html.toolbar.view.shapetool.Class.SMALL
import mono.html.toolbar.view.shapetool.Class.TOOL_TITLE
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

internal abstract class AppearanceSectionViewController(
    rootView: HTMLDivElement
) : ToolViewController(rootView) {

    abstract fun setVisibility(toolStates: Map<ToolType, ToolState>)

    enum class ToolType(val title: String) {
        FILL("Fill"),
        BORDER("Border"),
        STROKE("Stroke"),
        START_HEAD("Start head"),
        END_HEAD("End head")
    }

    data class ToolState(val isChecked: Boolean, val selectedPosition: Int)
}

private class AppearanceSectionViewControllerImpl(
    rootView: HTMLDivElement,
    private val toolTypeToViewControllerMap: Map<ToolType, AppearanceToolViewController>
) : AppearanceSectionViewController(rootView) {

    override fun setVisibility(toolStates: Map<ToolType, ToolState>) {
        setVisible(toolStates.isNotEmpty())
        for ((type, controller) in toolTypeToViewControllerMap.entries) {
            val state = toolStates[type]
            controller.setVisible(state != null)

            if (state != null) {
                controller.setState(state.isChecked, state.selectedPosition)
            }
        }
    }
}

private class AppearanceToolViewController(
    val toolType: ToolType,
    rootView: HTMLDivElement,
    private val checkBox: HTMLInputElement,
    private val options: List<HTMLElement>
) : ToolViewController(rootView) {

    fun setState(isChecked: Boolean, selectedPosition: Int) {
        checkBox.checked = isChecked
        options.forEachIndexed { index, optionView ->
            optionView.isEnabled = isChecked
            optionView.isSelected = index == selectedPosition
        }
    }
}

internal fun Tag.AppearanceSection(
    fillOptions: List<String> = listOf(" ", "█", "▒", "░", "▚"),
    borderOptions: List<String> = listOf("─", "━", "═"),
    headOptions: List<String> = listOf("▶", "■", "○", "◎", "●")
): AppearanceSectionViewController {
    val tools = mutableListOf<AppearanceToolViewController>()

    val rootView = Section("APPEARANCE") {
        tools += GridTextIconOptions(ToolType.FILL, fillOptions)
        tools += GridTextIconOptions(ToolType.BORDER, borderOptions)
        tools += GridTextIconOptions(ToolType.STROKE, borderOptions)
        tools += GridTextIconOptions(ToolType.START_HEAD, headOptions)
        tools += GridTextIconOptions(ToolType.END_HEAD, headOptions)
    }
    return AppearanceSectionViewControllerImpl(rootView, tools.associateBy { it.toolType })
}

private fun Tag.GridTextIconOptions(
    type: ToolType,
    options: List<String>
): AppearanceToolViewController {
    var checkBox: HTMLInputElement? = null
    var optionElements: List<HTMLElement>? = null
    val rootView = Tool(hasMoreBottomSpace = true) {
        Row {
            checkBox = CheckColumn()
            optionElements = OptionsColumn(type.title, options)
        }
    }
    return AppearanceToolViewController(type, rootView, checkBox!!, optionElements!!)
}

private fun Tag.CheckColumn(): HTMLInputElement {
    var checkBox: HTMLInputElement? = null
    Column(hasMoreRightSpace = true) {
        Row {
            checkBox = input(InputType.checkBox, classes = classes(INPUT_CHECK_BOX))
        }
    }
    return checkBox!!
}

private fun Tag.OptionsColumn(
    title: String,
    options: List<String>
): List<HTMLElement> {
    val optionElements = mutableListOf<HTMLElement>()
    Column {
        Row {
            span(classes(TOOL_TITLE)) { +title }
        }
        Row(isMonoFont = true, isGrid = true) {
            for (option in options) {
                optionElements += span(classes(ICON_BUTTON, SMALL)) { +option }
            }
        }
    }
    return optionElements
}

private fun Tag.Column(hasMoreRightSpace: Boolean = false, block: Tag.() -> Unit) {
    val columnClasses = classes(COLUMN, ADD_RIGHT_SPACE x hasMoreRightSpace)
    div(columnClasses) {
        block()
    }
}
