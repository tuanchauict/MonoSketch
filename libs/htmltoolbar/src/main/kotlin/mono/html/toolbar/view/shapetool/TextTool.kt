@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import mono.actionmanager.OneTimeActionType
import mono.html.Div
import mono.html.Span
import mono.html.SvgPath
import mono.html.appendElement
import mono.html.setOnClickListener
import mono.html.toolbar.view.SvgIcon
import mono.html.toolbar.view.isSelected
import mono.html.toolbar.view.isVisible
import mono.html.toolbar.view.shapetool.Class.ADD_RIGHT_SPACE
import mono.html.toolbar.view.shapetool.Class.CLICKABLE
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import mono.html.toolbar.view.shapetool.Class.MEDIUM
import mono.html.toolbar.view.shapetool.Class.QUARTER
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.distinctUntilChange
import mono.livedata.map
import mono.shape.extra.style.TextAlign
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

internal class TextSectionViewController(
    lifecycleOwner: LifecycleOwner,
    container: Element,
    liveData: LiveData<TextAlignVisibility>,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {
    private val horizontalIcons = listOf(
        TextAlignmentIconType.HORIZONTAL_LEFT,
        TextAlignmentIconType.HORIZONTAL_MIDDLE,
        TextAlignmentIconType.HORIZONTAL_RIGHT
    ).map { Icon(it, setOneTimeAction) }

    private val verticalIcons = listOf(
        TextAlignmentIconType.VERTICAL_TOP,
        TextAlignmentIconType.VERTICAL_MIDDLE,
        TextAlignmentIconType.VERTICAL_BOTTOM
    ).map { Icon(it, setOneTimeAction) }

    private val rootView = container.Section("TEXT") {
        Tool(true) {
            TextTool("Alignment") {
                appendElement(horizontalIcons)
            }
            TextTool("Position") {
                appendElement(verticalIcons)
            }
        }
    }

    val visibilityStateLiveData: LiveData<Boolean>

    init {
        val textAlignLiveData = liveData
            .map { (it as? TextAlignVisibility.Visible)?.textAlign }
            .distinctUntilChange()
        visibilityStateLiveData = textAlignLiveData
            .map { it != null }
            .distinctUntilChange()

        textAlignLiveData.observe(lifecycleOwner, listener = ::setCurrentTextAlign)
    }

    private fun setCurrentTextAlign(textAlign: TextAlign?) {
        rootView.isVisible = textAlign != null

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

    sealed class TextAlignVisibility {
        object Hide : TextAlignVisibility()

        data class Visible(val textAlign: TextAlign) : TextAlignVisibility()
    }
}

private fun Element.TextTool(name: String, iconBlock: Element.() -> Unit) {
    Row(isVerticalCenter = true, isMoreBottomSpaceRequired = true) {
        Div(classes(COLUMN, ADD_RIGHT_SPACE, QUARTER)) {
            innerText = name
        }
        Div(classes(COLUMN)) {
            Row {
                iconBlock()
            }
        }
    }
}

private fun Icon(
    iconType: TextAlignmentIconType,
    setOneTimeAction: (OneTimeActionType) -> Unit
): HTMLElement = Span(null, classes = classes(ICON_BUTTON, MEDIUM, ADD_RIGHT_SPACE, CLICKABLE)) {
    SvgIcon(16, 16, iconType.viewPortSize, iconType.viewPortSize) {
        SvgPath(iconType.iconPath)
    }

    setOnClickListener {
        setOneTimeAction(iconType.toTextAlignment())
    }
}

/* ktlint-disable max-line-length */
private enum class TextAlignmentIconType(
    val iconPath: String,
    val viewPortSize: Int = 16,
    val horizontalAlign: TextAlign.HorizontalAlign? = null,
    val verticalAlign: TextAlign.VerticalAlign? = null
) {
    HORIZONTAL_LEFT(
        "M2 12.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm0-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm0-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm0-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z",
        horizontalAlign = TextAlign.HorizontalAlign.LEFT
    ),
    HORIZONTAL_MIDDLE(
        "M4 12.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-2-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm2-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-2-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z",
        horizontalAlign = TextAlign.HorizontalAlign.MIDDLE
    ),
    HORIZONTAL_RIGHT(
        "M6 12.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-4-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm4-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-4-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z",
        horizontalAlign = TextAlign.HorizontalAlign.RIGHT
    ),
    VERTICAL_TOP(
        "M8 11h3v10h2V11h3l-4-4-4 4zM4 3v2h16V3H4z",
        viewPortSize = 24,
        verticalAlign = TextAlign.VerticalAlign.TOP
    ),
    VERTICAL_MIDDLE(
        "M8 19h3v4h2v-4h3l-4-4-4 4zm8-14h-3V1h-2v4H8l4 4 4-4zM4 11v2h16v-2H4z",
        viewPortSize = 24,
        verticalAlign = TextAlign.VerticalAlign.MIDDLE
    ),
    VERTICAL_BOTTOM(
        "M16 13h-3V3h-2v10H8l4 4 4-4zM4 19v2h16v-2H4z",
        viewPortSize = 24,
        verticalAlign = TextAlign.VerticalAlign.BOTTOM
    );

    fun toTextAlignment(): OneTimeActionType.TextAlignment =
        OneTimeActionType.TextAlignment(horizontalAlign, verticalAlign)
}
/* ktlint-enable max-line-length */
