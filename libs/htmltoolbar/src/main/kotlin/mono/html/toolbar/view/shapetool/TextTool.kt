@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.span
import mono.html.toolbar.OneTimeActionType
import mono.html.toolbar.view.SvgIcon
import mono.html.toolbar.view.SvgPath
import mono.html.toolbar.view.Tag
import mono.html.toolbar.view.addOrRemove
import mono.html.toolbar.view.isSelected
import mono.html.toolbar.view.shapetool.Class.ADD_RIGHT_SPACE
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import mono.html.toolbar.view.shapetool.Class.MEDIUM
import mono.html.toolbar.view.shapetool.Class.QUARTER
import mono.html.toolbar.view.shapetool.Class.VISIBLE
import mono.shape.shape.extra.TextExtra
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

internal abstract class TextSectionViewController(
    rootView: HTMLDivElement
) : ToolViewController(rootView) {

    abstract fun setCurrentTextAlign(textAlign: TextExtra.TextAlign?)
}

private class TextSectionViewControllerImpl(
    rootView: HTMLDivElement,
    private val horizontalIcons: List<HTMLElement>,
    private val verticalIcons: List<HTMLElement>
) : TextSectionViewController(rootView) {
    override fun setVisible(isVisible: Boolean) {
        rootView.addOrRemove(VISIBLE, isVisible)
    }

    override fun setCurrentTextAlign(textAlign: TextExtra.TextAlign?) {
        setVisible(textAlign != null)
        if (textAlign == null) {
            return
        }
        horizontalIcons.forEachIndexed { index, icon ->
            icon.isSelected = index == textAlign.horizontalAlign.ordinal
        }
        verticalIcons.forEachIndexed { index, icon ->
            icon.isSelected = index == textAlign.verticalAlign.ordinal
        }
    }
}

internal fun Tag.TextSection(
    setOneTimeAction: (OneTimeActionType) -> Unit
): TextSectionViewController {
    val horizontalIcons = mutableListOf<HTMLElement>()
    val verticalIcons = mutableListOf<HTMLElement>()
    val rootView = Section("TEXT") {
        Tool(true) {
            TextTool("Alignment") {

                horizontalIcons +=
                    Icon(
                        TextAlignmentIconType.HORIZONTAL_LEFT,
                        setOneTimeAction = setOneTimeAction
                    )
                horizontalIcons +=
                    Icon(
                        TextAlignmentIconType.HORIZONTAL_MIDDLE,
                        setOneTimeAction = setOneTimeAction
                    )
                horizontalIcons +=
                    Icon(
                        TextAlignmentIconType.HORIZONTAL_RIGHT,
                        setOneTimeAction = setOneTimeAction
                    )
            }

            TextTool("Position") {
                verticalIcons +=
                    Icon(
                        TextAlignmentIconType.VERTICAL_TOP,
                        setOneTimeAction = setOneTimeAction
                    )
                verticalIcons +=
                    Icon(
                        TextAlignmentIconType.VERTICAL_MIDDLE,
                        setOneTimeAction = setOneTimeAction
                    )
                verticalIcons +=
                    Icon(
                        TextAlignmentIconType.VERTICAL_BOTTOM,
                        setOneTimeAction = setOneTimeAction
                    )
            }
        }
    }

    return TextSectionViewControllerImpl(rootView, horizontalIcons, verticalIcons)
}

private fun Tag.TextTool(name: String, iconBlock: Tag.() -> Unit) {
    Row(isVerticalCenter = true, isMoreBottomSpaceRequired = true) {
        div(classes(COLUMN, ADD_RIGHT_SPACE, QUARTER)) {
            +name
        }
        div(classes(COLUMN)) {
            Row {
                iconBlock()
            }
        }
    }
}

private fun Tag.Icon(
    iconType: TextAlignmentIconType,
    setOneTimeAction: (OneTimeActionType) -> Unit
): HTMLElement = span(classes(ICON_BUTTON, MEDIUM, ADD_RIGHT_SPACE)) {
    SvgIcon(16, 16, iconType.viewPortSize, iconType.viewPortSize) {
        SvgPath(iconType.iconPath)

        onClickFunction = {
            println(iconType.toTextAlignment())
            setOneTimeAction(iconType.toTextAlignment())
        }
    }
}

/* ktlint-disable max-line-length */
private enum class TextAlignmentIconType(
    val iconPath: String,
    val viewPortSize: Int = 16,
    val horizontalAlign: TextExtra.HorizontalAlign? = null,
    val verticalAlign: TextExtra.VerticalAlign? = null
) {
    HORIZONTAL_LEFT(
        "M2 12.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm0-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm0-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm0-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z",
        horizontalAlign = TextExtra.HorizontalAlign.LEFT
    ),
    HORIZONTAL_MIDDLE(
        "M4 12.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-2-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm2-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-2-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z",
        horizontalAlign = TextExtra.HorizontalAlign.MIDDLE
    ),
    HORIZONTAL_RIGHT(
        "M6 12.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-4-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm4-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-4-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z",
        horizontalAlign = TextExtra.HorizontalAlign.RIGHT
    ),
    VERTICAL_TOP(
        "M8 11h3v10h2V11h3l-4-4-4 4zM4 3v2h16V3H4z",
        viewPortSize = 24,
        verticalAlign = TextExtra.VerticalAlign.TOP
    ),
    VERTICAL_MIDDLE(
        "M8 19h3v4h2v-4h3l-4-4-4 4zm8-14h-3V1h-2v4H8l4 4 4-4zM4 11v2h16v-2H4z",
        viewPortSize = 24,
        verticalAlign = TextExtra.VerticalAlign.MIDDLE
    ),
    VERTICAL_BOTTOM(
        "M16 13h-3V3h-2v10H8l4 4 4-4zM4 19v2h16v-2H4z",
        viewPortSize = 24,
        verticalAlign = TextExtra.VerticalAlign.BOTTOM
    );

    fun toTextAlignment(): OneTimeActionType.TextAlignment =
        OneTimeActionType.TextAlignment(horizontalAlign, verticalAlign)
}
/* ktlint-enable max-line-length */
