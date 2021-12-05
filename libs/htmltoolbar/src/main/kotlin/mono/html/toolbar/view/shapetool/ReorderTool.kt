@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import mono.html.Div
import mono.html.SvgPath
import mono.html.setOnClickListener
import mono.html.appendElement
import mono.html.modal.TooltipPosition
import mono.html.modal.tooltip
import mono.html.px
import mono.html.style
import mono.html.toolbar.OneTimeActionType
import mono.html.toolbar.OneTimeActionType.ReorderShape
import mono.html.toolbar.view.SvgIcon
import mono.html.toolbar.view.isEnabled
import mono.html.toolbar.view.isVisible
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.distinctUntilChange
import mono.livedata.map
import mono.shape.command.ChangeOrder
import mono.shape.shape.AbstractShape
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

/**
 * View controller for shape tool' reorder section.
 */
internal class ReorderSectionViewController(
    lifecycleOwner: LifecycleOwner,
    container: Element,
    singleShapeLiveData: LiveData<AbstractShape?>,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {

    val visibilityStateLiveData: LiveData<Boolean>

    init {
        val icons = ReorderIconType.values().map { type ->
            Icon(type) { setOneTimeAction(ReorderShape(it.changeOrderType)) }
        }

        val section = container.Section("", isSmallSpace = true) {
            Tool {
                Row(isCenterEvenSpace = true) {
                    style("padding-bottom" to 2.px)
                    appendElement(icons)
                }
            }
        }

        visibilityStateLiveData = singleShapeLiveData
            .map { it != null }
            .distinctUntilChange()

        visibilityStateLiveData.observe(lifecycleOwner) {
            section.isVisible = it
            for (icon in icons) {
                icon.isEnabled = it
            }
        }
    }
}

private fun Icon(
    iconType: ReorderIconType,
    onClick: (ReorderIconType) -> Unit
): HTMLDivElement =
    Div(classes = classes(ICON_BUTTON)) {
        style(
            "padding-left" to 7.px,
            "padding-top" to 7.px
        )

        tooltip(iconType.title, TooltipPosition.TOP)

        SvgIcon(18) {
            for (path in iconType.iconPaths) {
                SvgPath(path)
            }
        }

        setOnClickListener {
            val target = it.currentTarget as HTMLElement
            if (target.isEnabled) {
                onClick(iconType)
            }
        }
    }

private enum class ReorderIconType(
    val changeOrderType: ChangeOrder.ChangeOrderType,
    val title: String,
    val iconPaths: List<String>
) {
    FRONT(
        ChangeOrder.ChangeOrderType.FRONT,
        "Bring to Front",
        listOf("M18,18h-9V15h-6V9h-3V0h9V3h6V9h3v9h0Zm-4-4V4H4V14h10Z")
    ),
    UPWARD(
        ChangeOrder.ChangeOrderType.FORWARD,
        "Bring Forward",
        listOf("M18,18h-12v-5h-6v-13h13v6h5v12h0Zm-17-6h11v-11h-11Z")
    ),
    BACKWARD(
        ChangeOrder.ChangeOrderType.BACKWARD,
        "Send Backward",
        listOf("M6,18V13h-6V0h13V6h5V18Zm-5-6h5V6h6V1H1Z")
    ),
    BACK(
        ChangeOrder.ChangeOrderType.BACK,
        "Send to Back",
        listOf("M9,18V15h-6V9h-3V0h9V3h6V9h3v9Zm-5-4h5V9h-5Zm10-5V4h-5V9Z")
    )
}
