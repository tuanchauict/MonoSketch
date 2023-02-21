@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import mono.actionmanager.OneTimeActionType
import mono.common.nullToFalse
import mono.html.Div
import mono.html.Input
import mono.html.InputType
import mono.html.Span
import mono.html.bindClass
import mono.html.setAttributes
import mono.html.setOnChangeListener
import mono.html.setOnClickListener
import mono.html.toolbar.view.shapetool.AppearanceVisibility.DashVisible
import mono.html.toolbar.view.shapetool.AppearanceVisibility.GridVisible
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.map
import org.w3c.dom.Element

internal class AppearanceSectionViewController(
    private val lifecycleOwner: LifecycleOwner,
    container: Element,
    private val appearanceDataController: AppearanceDataController
) {
    val visibilityStateLiveData: LiveData<Boolean> =
        appearanceDataController.hasAnyVisibleToolLiveData

    init {
        val rootView = container.Section("APPEARANCE") {
            Tool(
                ToolType.FILL,
                appearanceDataController.fillOptions,
                appearanceDataController.fillToolStateLiveData
            )

            Tool(
                ToolType.BORDER,
                appearanceDataController.strokeOptions,
                appearanceDataController.borderToolStateLiveData,
            ) {
                DashPattern(
                    appearanceDataController.borderDashPatternLiveData.map { (it as? DashVisible) }
                ) { dash, gap, offset ->
                    appearanceDataController.setOneTimeAction(
                        OneTimeActionType.ChangeShapeBorderDashPatternExtra(dash, gap, offset)
                    )
                }
            }

            Tool(
                ToolType.STROKE,
                appearanceDataController.strokeOptions,
                appearanceDataController.lineStrokeToolStateLiveData
            ) {
                DashPattern(
                    appearanceDataController.lineStrokeDashPatternLiveData.map { it as? DashVisible }
                ) { dash, gap, offset ->
                    appearanceDataController.setOneTimeAction(
                        OneTimeActionType.ChangeLineStrokeDashPatternExtra(dash, gap, offset)
                    )
                }
            }

            Tool(
                ToolType.START_HEAD,
                appearanceDataController.headOptions,
                appearanceDataController.lineStartHeadToolStateLiveData
            )

            Tool(
                ToolType.END_HEAD,
                appearanceDataController.headOptions,
                appearanceDataController.lineEndHeadToolStateLiveData
            )
        }

        appearanceDataController.hasAnyVisibleToolLiveData.observe(lifecycleOwner) {
            rootView.bindClass("hide", !it)
        }
    }

    private fun Element.Tool(
        type: ToolType,
        options: List<AppearanceOptionItem>,
        liveData: LiveData<AppearanceVisibility>,
        block: Element.() -> Unit = {}
    ) {
        val gridVisibleLiveData = liveData.map { it as? GridVisible }
        CheckableTool(type, gridVisibleLiveData) {
            Options(type, options, gridVisibleLiveData)
            block()
        }
    }

    private fun Element.CheckableTool(
        type: ToolType,
        liveData: LiveData<GridVisible?>,
        block: Element.() -> Unit
    ) {
        Div("tool-appearance") {
            Div("checkbox-column") {
                ToolCheckBox(type, liveData.map { it?.isChecked.nullToFalse() })
            }

            Div("tool-column") {
                Span(classes = "tool-title", type.title)

                block()
            }

            liveData.observe(lifecycleOwner) {
                bindClass("hide", it == null)
            }
        }
    }

    private fun Element.ToolCheckBox(
        type: ToolType,
        isCheckedLiveData: LiveData<Boolean>,
    ) {
        Input(InputType.CHECK_BOX, classes = "") {
            setOnChangeListener {
                appearanceDataController.setOneTimeAction(type.toActionType(checked))
            }

            isCheckedLiveData.observe(lifecycleOwner) { isChecked -> this.checked = isChecked }
        }
    }

    private fun Element.Options(
        type: ToolType,
        options: List<AppearanceOptionItem>,
        visibilityLiveData: LiveData<GridVisible?>
    ) {
        Div(classes = "option-group monofont") {
            val optionViews = options.map { option ->
                Span(classes = "option", option.name) {
                    setOnClickListener {
                        appearanceDataController.setOneTimeAction(
                            type.toActionType(selectedId = option.id)
                        )
                    }
                }
            }

            visibilityLiveData.observe(lifecycleOwner) { state ->
                if (state == null) {
                    return@observe
                }

                bindClass("disabled", !state.isChecked)

                optionViews.forEachIndexed { index, optionView ->
                    optionView.bindClass("selected", index == state.selectedPosition)
                }
            }
        }
    }

    private fun Element.DashPattern(
        liveData: LiveData<DashVisible?>,
        onChange: (Int?, Int?, Int?) -> Unit
    ) {
        Div("dash-pattern") {
            Div("pattern") {
                Span(text = "Dash")
                DashPatternInput(
                    minValue = 1,
                    liveData.map { it?.dashPattern?.dash }
                ) { onChange(it, null, null) }
            }
            Div("pattern") {
                Span(text = "Gap")
                DashPatternInput(
                    minValue = 0,
                    liveData.map { it?.dashPattern?.gap }
                ) { onChange(null, it, null) }
            }
            Div("pattern") {
                Span(text = "Shift")
                DashPatternInput(
                    minValue = null,
                    liveData.map { it?.dashPattern?.offset }
                ) { onChange(null, null, it) }
            }
        }
    }

    private fun Element.DashPatternInput(
        minValue: Int?,
        liveData: LiveData<Int?>,
        onChange: (Int) -> Unit
    ) {
        Input(InputType.NUMBER, "tool-input-text") {
            if (minValue != null) {
                setAttributes("min" to minValue)
            }
            setOnChangeListener { onChange(value.toInt()) }

            liveData.observe(lifecycleOwner) {
                if (it != null) {
                    value = it.toString()
                }
            }
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
