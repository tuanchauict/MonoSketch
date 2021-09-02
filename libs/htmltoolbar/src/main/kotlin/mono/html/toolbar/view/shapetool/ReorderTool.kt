@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import mono.html.Div
import mono.html.addOnClickListener
import mono.html.appendElement
import mono.html.toolbar.OneTimeActionType
import mono.html.toolbar.OneTimeActionType.ReorderShape
import mono.html.toolbar.view.SvgIcon
import mono.html.toolbar.view.SvgPath
import mono.html.toolbar.view.isEnabled
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
    init {
        val icons = ReorderIconType.values().map { type ->
            Icon(type) { setOneTimeAction(ReorderShape(it.changeOrderType)) }
        }
        container.Section("", isSmallSpace = true) {
            Tool {
                Row(isCenterEvenSpace = true) {
                    appendElement(*icons.toTypedArray())
                }
            }
        }

        singleShapeLiveData
            .map { it != null }
            .distinctUntilChange()
            .observe(lifecycleOwner) {
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
        addOnClickListener {
            val target = it.currentTarget as HTMLElement
            if (target.isEnabled) {
                onClick(iconType)
            }
        }
        // TODO: Remove this append
        append {
            SvgIcon(16, 16) {
                for (path in iconType.iconPaths) {
                    SvgPath(path)
                }
            }
        }
    }

/* ktlint-disable max-line-length */
private enum class ReorderIconType(
    val changeOrderType: ChangeOrder.ChangeOrderType,
    val iconPaths: List<String>
) {
    FRONT(
        ChangeOrder.ChangeOrderType.FRONT,
        listOf(
            "M7.646 2.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 3.707 2.354 9.354a.5.5 0 1 1-.708-.708l6-6z",
            "M7.646 6.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 7.707l-5.646 5.647a.5.5 0 0 1-.708-.708l6-6z"
        )
    ),
    UPWARD(
        ChangeOrder.ChangeOrderType.FORWARD,
        listOf(
            "M7.646 4.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 5.707l-5.646 5.647a.5.5 0 0 1-.708-.708l6-6z"
        )
    ),
    BACKWARD(
        ChangeOrder.ChangeOrderType.BACKWARD,
        listOf(
            "M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
        )
    ),
    BACK(
        ChangeOrder.ChangeOrderType.BACK,
        listOf(
            "M1.646 6.646a.5.5 0 0 1 .708 0L8 12.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z",
            "M1.646 2.646a.5.5 0 0 1 .708 0L8 8.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
        )
    )
}
/* ktlint-enable max-line-length */
