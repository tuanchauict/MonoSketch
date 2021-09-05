@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import mono.html.Div
import mono.html.Input
import mono.html.InputType
import mono.html.Span
import mono.html.appendElement
import mono.html.setAttributes
import mono.html.setOnChangeListener
import mono.html.setOnClickListener
import mono.html.toolbar.OneTimeActionType
import mono.html.toolbar.view.isEnabled
import mono.html.toolbar.view.isSelected
import mono.html.toolbar.view.isVisible
import mono.html.toolbar.view.shapetool.AppearanceVisibility.DashVisible
import mono.html.toolbar.view.shapetool.AppearanceVisibility.GridVisible
import mono.html.toolbar.view.shapetool.Class.ADD_RIGHT_SPACE
import mono.html.toolbar.view.shapetool.Class.CENTER_VERTICAL
import mono.html.toolbar.view.shapetool.Class.CLICKABLE
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.GRAY_TEXT
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import mono.html.toolbar.view.shapetool.Class.INLINE_TITLE
import mono.html.toolbar.view.shapetool.Class.INPUT_CHECK_BOX
import mono.html.toolbar.view.shapetool.Class.INPUT_TEXT
import mono.html.toolbar.view.shapetool.Class.ROW
import mono.html.toolbar.view.shapetool.Class.SMALL
import mono.html.toolbar.view.shapetool.Class.TOOL_TITLE
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.map
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

internal class AppearanceSectionViewController(
    lifecycleOwner: LifecycleOwner,
    container: Element,
    appearanceDataController: AppearanceDataController
) {
    private val rootView = container.Section("APPEARANCE")

    init {
        GridTextIconOptions(
            rootView,
            ToolType.FILL,
            appearanceDataController.fillOptions,
            appearanceDataController::setOneTimeAction
        ).observe(lifecycleOwner, appearanceDataController.fillToolStateLiveData)

        GridTextIconOptions(
            rootView,
            ToolType.BORDER,
            appearanceDataController.strokeOptions,
            appearanceDataController::setOneTimeAction
        ).observe(lifecycleOwner, appearanceDataController.borderToolStateLiveData)

        DashPatternTool(rootView) { dash, gap, offset ->
            appearanceDataController.setOneTimeAction(
                OneTimeActionType.ChangeShapeBorderDashPatternExtra(dash, gap, offset)
            )
        }.observe(lifecycleOwner, appearanceDataController.borderDashPatternLiveData)

        GridTextIconOptions(
            rootView,
            ToolType.STROKE,
            appearanceDataController.strokeOptions,
            appearanceDataController::setOneTimeAction
        ).observe(lifecycleOwner, appearanceDataController.lineStrokeToolStateLiveData)

        DashPatternTool(rootView) { dash, gap, offset ->
            appearanceDataController.setOneTimeAction(
                OneTimeActionType.ChangeLineStrokeDashPatternExtra(dash, gap, offset)
            )
        }.observe(lifecycleOwner, appearanceDataController.lineStrokeDashPatternLiveData)

        GridTextIconOptions(
            rootView,
            ToolType.START_HEAD,
            appearanceDataController.headOptions,
            appearanceDataController::setOneTimeAction
        ).observe(lifecycleOwner, appearanceDataController.lineStartHeadToolStateLiveData)

        GridTextIconOptions(
            rootView,
            ToolType.END_HEAD,
            appearanceDataController.headOptions,
            appearanceDataController::setOneTimeAction
        ).observe(lifecycleOwner, appearanceDataController.lineEndHeadToolStateLiveData)

        appearanceDataController.hasAnyVisibleToolLiveData.observe(lifecycleOwner) {
            rootView.isVisible = it
        }
    }
}

private enum class ToolType(val title: String) {
    FILL("Fill") {
        override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType =
            OneTimeActionType.ChangeShapeFillExtra(isChecked, selectedId)
    },
    BORDER("Border") {
        override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType =
            OneTimeActionType.ChangeShapeBorderExtra(isChecked, selectedId)
    },
    STROKE("Stroke") {
        override fun toActionType(isChecked: Boolean?, selectedId: String?): OneTimeActionType =
            OneTimeActionType.ChangeLineStrokeExtra(isChecked, selectedId)
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

private class AppearanceToolViewController(
    private val rootView: HTMLDivElement,
    private val checkBox: HTMLInputElement,
    private val options: List<HTMLElement>
) {
    fun observe(
        lifecycleOwner: LifecycleOwner,
        liveData: LiveData<AppearanceVisibility>
    ) {
        liveData.map { it as? GridVisible }
            .observe(lifecycleOwner, listener = ::setState)
    }

    private fun setState(state: GridVisible?) {
        rootView.isVisible = state != null
        if (state == null) {
            return
        }
        checkBox.checked = state.isChecked
        options.forEachIndexed { index, optionView ->
            optionView.isEnabled = state.isChecked
            optionView.isSelected = index == state.selectedPosition
        }
    }
}

private class DashPatternViewController(
    private val rootView: HTMLDivElement,
    private val dashInput: HTMLInputElement,
    private val gapInput: HTMLInputElement,
    private val offsetInput: HTMLInputElement
) {
    fun observe(lifecycleOwner: LifecycleOwner, liveData: LiveData<AppearanceVisibility>) {
        liveData.observe(lifecycleOwner, listener = ::setState)
    }

    private fun setState(visibility: AppearanceVisibility) {
        val dashVisible = visibility as? DashVisible
        rootView.isVisible = dashVisible != null

        val dashPattern = dashVisible?.dashPattern ?: return
        dashInput.value = dashPattern.segment.toString()
        gapInput.value = dashPattern.gap.toString()
        offsetInput.value = dashPattern.offset.toString()
    }
}

private fun GridTextIconOptions(
    container: Element,
    type: ToolType,
    options: List<AppearanceOptionItem>,
    setOneTimeAction: (OneTimeActionType) -> Unit
): AppearanceToolViewController {
    val checkBox =
        Input(parent = null, InputType.CHECK_BOX, classes = classes(INPUT_CHECK_BOX, CLICKABLE)) {
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
    val rootView = container.Tool(hasMoreBottomSpace = true) {
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
    return AppearanceToolViewController(rootView, checkBox, optionElements)
}

private fun DashPatternTool(
    container: Element,
    onChange: (Int?, Int?, Int?) -> Unit
): DashPatternViewController {
    val dashInput = DashPatternInput(1) { onChange(it, null, null) }
    val gapInput = DashPatternInput(0) { onChange(null, it, null) }
    val offsetInput = DashPatternInput(null) { onChange(null, null, it) }

    val rootView = container.Tool(hasMoreBottomSpace = true, hasCheckBox = false) {
        Row(isMoreBottomSpaceRequired = true) {
            Column {
                Row {
                    DashPatternRow("Dash", dashInput)
                    DashPatternRow("Gap", gapInput)
                    DashPatternRow("Shift", offsetInput)
                }
            }
        }
    }
    return DashPatternViewController(rootView, dashInput, gapInput, offsetInput)
}

fun Element.DashPatternRow(name: String, inputBox: HTMLInputElement) {
    Div(classes(COLUMN)) {
        setAttributes("style" to "margin-right: 12px")
        Div(classes(ROW, CENTER_VERTICAL)) {
            Span(classes(INLINE_TITLE, GRAY_TEXT), text = name)
            appendElement(inputBox)
        }
    }
}

fun DashPatternInput(minValue: Int?, onChange: (Int) -> Unit): HTMLInputElement =
    Input(null, InputType.NUMBER, classes(INPUT_TEXT, SMALL)) {
        if (minValue != null) {
            setAttributes("min" to minValue)
        }
        setOnChangeListener { onChange(value.toInt()) }
    }

private fun Element.Column(hasMoreRightSpace: Boolean = false, block: Element.() -> Unit) {
    val columnClasses = classes(COLUMN, ADD_RIGHT_SPACE x hasMoreRightSpace)
    Div(columnClasses) {
        block()
    }
}
