@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import mono.html.CheckBox
import mono.html.Div
import mono.html.Span
import mono.html.appendElement
import mono.html.setOnChangeListener
import mono.html.setOnClickListener
import mono.html.toolbar.OneTimeActionType
import mono.html.toolbar.view.isEnabled
import mono.html.toolbar.view.isSelected
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.ToolType
import mono.html.toolbar.view.shapetool.Class.ADD_RIGHT_SPACE
import mono.html.toolbar.view.shapetool.Class.CLICKABLE
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import mono.html.toolbar.view.shapetool.Class.INPUT_CHECK_BOX
import mono.html.toolbar.view.shapetool.Class.SMALL
import mono.html.toolbar.view.shapetool.Class.TOOL_TITLE
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

internal abstract class AppearanceSectionViewController(
    rootView: HTMLDivElement
) : ToolViewController(rootView) {

    abstract fun setVisibility(toolStates: Map<ToolType, Visibility>)

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
            override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType =
                OneTimeActionType.ChangeLineStartAnchorExtra(isChecked, selectedId)
        },
        END_HEAD("End head") {
            override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType =
                OneTimeActionType.ChangeLineEndAnchorExtra(isChecked, selectedId)
        };

        abstract fun toActionType(
            isChecked: Boolean? = null,
            selectedId: String? = null
        ): OneTimeActionType
    }

    sealed class Visibility {
        object Hide : Visibility()

        data class Visible(val isChecked: Boolean, val selectedPosition: Int) : Visibility()
    }
}

private class AppearanceSectionViewControllerImpl(
    rootView: HTMLDivElement,
    private val toolTypeToViewControllerMap: Map<ToolType, AppearanceToolViewController>
) : AppearanceSectionViewController(rootView) {

    override fun setVisibility(toolStates: Map<ToolType, Visibility>) {
        val isVisible = toolStates.entries.any { it.value is Visibility.Visible }
        setVisible(isVisible)
        for ((type, controller) in toolTypeToViewControllerMap.entries) {
            val state = toolStates[type] as? Visibility.Visible
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

internal fun Element.AppearanceSection(
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

private fun Element.GridTextIconOptions(
    type: ToolType,
    options: List<OptionItem>,
    setOneTimeAction: (OneTimeActionType) -> Unit
): AppearanceToolViewController {
    val checkBox = CheckBox(null, classes = classes(INPUT_CHECK_BOX, CLICKABLE)) {
        setOnChangeListener {
            setOneTimeAction(type.toActionType(checked))
        }
    }
    val optionElements = options.map { option ->
        Span(null, classes(ICON_BUTTON, SMALL, CLICKABLE), option.name) {
            setOnClickListener {
                setOneTimeAction(type.toActionType(selectedId = option.id))
            }
        }
    }
    val rootView = Tool(hasMoreBottomSpace = true) {
        Row {
            Column(hasMoreRightSpace = true) {
                Row {
                    appendElement(checkBox)
                }
            }
            Column {
                Row {
                    Span(classes(TOOL_TITLE, CLICKABLE), type.title) {
                        setOnClickListener {
                            checkBox.checked = !checkBox.checked
                            checkBox.dispatchEvent(Event("change"))
                        }
                    }
                }
                Row(isMonoFont = true, isGrid = true) {
                    appendElement(optionElements)
                }
            }
        }
    }
    return AppearanceToolViewController(type, rootView, checkBox, optionElements)
}

private fun Element.Column(hasMoreRightSpace: Boolean = false, block: Element.() -> Unit) {
    val columnClasses = classes(COLUMN, ADD_RIGHT_SPACE x hasMoreRightSpace)
    Div(columnClasses) {
        block()
    }
}
