@file:Suppress("FunctionName", "ktlint:filename")

package mono.html.toolbar.view.shapetool

import mono.actionmanager.OneTimeActionType
import mono.html.Div
import mono.html.Span
import mono.html.bindClass
import mono.html.toolbar.view.components.CloudItemFactory
import mono.html.toolbar.view.components.DashPattern
import mono.html.toolbar.view.components.OptionCloud
import mono.html.toolbar.view.shapetool.AppearanceVisibility.DashVisible
import mono.html.toolbar.view.shapetool.AppearanceVisibility.GridVisible
import mono.html.toolbar.view.utils.CssClass
import mono.html.toolbar.view.utils.bindClass
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.filterNotNull
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
                appearanceDataController.borderToolStateLiveData
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
                    appearanceDataController.lineStrokeDashPatternLiveData
                        .map { it as? DashVisible }
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
            rootView.bindClass(CssClass.HIDE, !it)
        }
    }

    private fun Element.Tool(
        type: ToolType,
        options: List<AppearanceOptionItem>,
        liveData: LiveData<AppearanceVisibility>,
        block: Element.() -> Unit = {}
    ) {
        val gridVisibleLiveData = liveData.map { it as? GridVisible }

        Div("tool-appearance") {
            Span(classes = "tool-title", type.title)
            Options(type, options, gridVisibleLiveData)
            block()

            gridVisibleLiveData.observe(lifecycleOwner) {
                bindClass(CssClass.HIDE, it == null)
            }
        }
    }

    private fun Element.Options(
        type: ToolType,
        options: List<AppearanceOptionItem>,
        visibilityLiveData: LiveData<GridVisible?>
    ) {
        val factory = CloudItemFactory(options.size + 1) { index ->
            bindClass("dash-border", index == 0)
            val text = if (index > 0) options[index - 1].name else "ø"
            Span(classes = "monofont", text)
        }
        val cloud = OptionCloud(factory) {
            val selectedIndex = it - 1

            appearanceDataController.setOneTimeAction(
                type.toActionType(
                    isChecked = selectedIndex >= 0,
                    selectedId = options.getOrNull(selectedIndex)?.id
                )
            )
        }
        visibilityLiveData.filterNotNull().observe(lifecycleOwner) {
            val selectedIndex = if (it.isChecked) it.selectedPosition + 1 else 0
            cloud.setSelectedItem(selectedIndex)
        }
    }

    private fun Element.DashPattern(
        liveData: LiveData<DashVisible?>,
        onChange: (Int?, Int?, Int?) -> Unit
    ) {
        val dashPattern = DashPattern(onChange)
        liveData.map { it?.dashPattern }.filterNotNull().observe(lifecycleOwner) {
            println(it)
            dashPattern.set(it.dash, it.gap, it.offset)
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
