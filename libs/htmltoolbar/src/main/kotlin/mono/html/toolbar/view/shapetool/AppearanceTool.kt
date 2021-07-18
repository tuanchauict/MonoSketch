@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.InputType
import kotlinx.html.js.div
import kotlinx.html.js.input
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.span
import mono.common.nullToFalse
import mono.html.toolbar.OneTimeActionType
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
        FILL("Fill") {
            override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType =
                OneTimeActionType.ChangeShapeFillExtra(isChecked, selectedId)
        },
        BORDER("Border") {
            override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType =
                OneTimeActionType.ChangeShapeBorderExtra(isChecked, selectedId)
        },
        STROKE("Stroke") {
            override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType {
                TODO("Not yet implemented")
            }
        },
        START_HEAD("Start head") {
            override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType {
                TODO("Not yet implemented")
            }
        },
        END_HEAD("End head") {
            override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType {
                TODO("Not yet implemented")
            }
        };

        abstract fun toActionType(
            isChecked: Boolean? = null,
            selectedId: String? = null
        ): OneTimeActionType
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

internal data class OptionItem(val id: String, val name: String)

internal fun Tag.AppearanceSection(
    fillOptions: List<OptionItem>,
    borderOptions: List<OptionItem>,
    headOptions: List<OptionItem>,
    setOneTimeAction: (OneTimeActionType) -> Unit
): AppearanceSectionViewController {
    val tools = mutableListOf<AppearanceToolViewController>()

    val rootView = Section("APPEARANCE") {
        tools += GridTextIconOptions(ToolType.FILL, fillOptions, setOneTimeAction)
        tools += GridTextIconOptions(ToolType.BORDER, borderOptions, setOneTimeAction)
        tools += GridTextIconOptions(ToolType.STROKE, borderOptions, setOneTimeAction)
        tools += GridTextIconOptions(ToolType.START_HEAD, headOptions, setOneTimeAction)
        tools += GridTextIconOptions(ToolType.END_HEAD, headOptions, setOneTimeAction)
    }
    return AppearanceSectionViewControllerImpl(rootView, tools.associateBy { it.toolType })
}

private fun Tag.GridTextIconOptions(
    type: ToolType,
    options: List<OptionItem>,
    setOneTimeAction: (OneTimeActionType) -> Unit
): AppearanceToolViewController {
    var checkBox: HTMLInputElement? = null
    var optionElements: List<HTMLElement>? = null
    val rootView = Tool(hasMoreBottomSpace = true) {
        Row {
            checkBox = CheckColumn {
                setOneTimeAction(type.toActionType(it))
            }
            optionElements = OptionsColumn(type.title, options) {
                setOneTimeAction(type.toActionType(selectedId = it.id))
            }
        }
    }
    return AppearanceToolViewController(type, rootView, checkBox!!, optionElements!!)
}

private fun Tag.CheckColumn(onCheckChange: (Boolean) -> Unit): HTMLInputElement {
    var checkBox: HTMLInputElement? = null
    Column(hasMoreRightSpace = true) {
        Row {
            checkBox = input(InputType.checkBox, classes = classes(INPUT_CHECK_BOX)) {
                onChangeFunction = {
                    onCheckChange(checkBox?.checked.nullToFalse())
                }
            }
        }
    }
    return checkBox!!
}

private fun Tag.OptionsColumn(
    title: String,
    options: List<OptionItem>,
    onOptionSelected: (OptionItem) -> Unit
): List<HTMLElement> {
    val optionElements = mutableListOf<HTMLElement>()
    Column {
        Row {
            span(classes(TOOL_TITLE)) { +title }
        }
        Row(isMonoFont = true, isGrid = true) {
            for (option in options) {
                optionElements += span(classes(ICON_BUTTON, SMALL)) {
                    +option.name

                    onClickFunction = {
                        onOptionSelected(option)
                    }
                }
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
