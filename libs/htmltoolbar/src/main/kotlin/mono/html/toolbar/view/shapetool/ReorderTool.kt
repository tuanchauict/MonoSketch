@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import mono.html.toolbar.OneTimeActionType
import mono.html.toolbar.view.SvgIcon
import mono.html.toolbar.view.SvgPath
import mono.html.toolbar.view.Tag
import mono.html.toolbar.view.isEnabled
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

/**
 * View controller for shape tool' reorder section.
 */
private class ReorderSectionViewController(
    rootDiv: HTMLDivElement,
    private val icons: List<HTMLDivElement>
) : ToolViewController(rootDiv) {
    override fun setEnabled(isEnabled: Boolean) {
        for (icon in icons) {
            icon.isEnabled = isEnabled
        }
    }
}

internal fun Tag.ReorderSection(
    setOneTimeAction: (OneTimeActionType) -> Unit
): ToolViewController {
    val icons = mutableListOf<HTMLDivElement>()
    val div = Section("", hasDivider = true, isSmallSpace = true) {
        Tool {
            Row(isCenterEvenSpace = true) {
                for (type in IconType.values()) {
                    icons += Icon(type) { setOneTimeAction(it.actionType) }
                }
            }
        }
    }

    return ReorderSectionViewController(div, icons)
}

private fun Tag.Icon(iconType: IconType, onClick: (IconType) -> Unit): HTMLDivElement =
    div(classes(ICON_BUTTON)) {
        onClickFunction = {
            val target = it.currentTarget as HTMLElement
            if (target.isEnabled) {
                onClick(iconType)
            }
        }

        SvgIcon(16, 16) {
            for (path in iconType.iconPaths) {
                SvgPath(path)
            }
        }
    }

/* ktlint-disable max-line-length */
private enum class IconType(val actionType: OneTimeActionType, val iconPaths: List<String>) {
    FRONT(
        OneTimeActionType.REORDER_SELECTED_SHAPE_FRONT,
        listOf(
            "M7.646 2.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 3.707 2.354 9.354a.5.5 0 1 1-.708-.708l6-6z",
            "M7.646 6.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 7.707l-5.646 5.647a.5.5 0 0 1-.708-.708l6-6z"
        )
    ),
    UPWARD(
        OneTimeActionType.REORDER_SELECTED_SHAPE_FORWARD,
        listOf(
            "M7.646 4.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 5.707l-5.646 5.647a.5.5 0 0 1-.708-.708l6-6z"
        )
    ),
    BACKWARD(
        OneTimeActionType.REORDER_SELECTED_SHAPE_BACKWARD,
        listOf(
            "M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
        )
    ),
    BACK(
        OneTimeActionType.REORDER_SELECTED_SHAPE_BACK,
        listOf(
            "M1.646 6.646a.5.5 0 0 1 .708 0L8 12.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z",
            "M1.646 2.646a.5.5 0 0 1 .708 0L8 8.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
        )
    )
}
/* ktlint-enable max-line-length */
