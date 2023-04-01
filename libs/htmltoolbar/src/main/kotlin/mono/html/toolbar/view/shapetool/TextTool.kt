@file:Suppress("FunctionName", "ktlint:filename")

package mono.html.toolbar.view.shapetool

import mono.actionmanager.OneTimeActionType
import mono.html.Div
import mono.html.Span
import mono.html.SvgPath
import mono.html.appendElement
import mono.html.setOnClickListener
import mono.html.toolbar.view.utils.CssClass
import mono.html.toolbar.view.utils.SvgIcon
import mono.html.toolbar.view.utils.bindClass
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
        Div("tool-text") {
            Div("option-group") {
                Span("tool-title", text = "Alignment")
                appendElement(horizontalIcons)
            }
            Div("option-group") {
                Span("tool-title", "Position")
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
        rootView.bindClass(CssClass.HIDE, textAlign == null)

        if (textAlign == null) {
            return
        }
        horizontalIcons.forEachIndexed { index, icon ->
            icon.bindClass(CssClass.SELECTED, index == textAlign.horizontalAlign.ordinal)
        }
        verticalIcons.forEachIndexed { index, icon ->
            icon.bindClass(CssClass.SELECTED, index == textAlign.verticalAlign.ordinal)
        }
    }

    sealed class TextAlignVisibility {
        object Hide : TextAlignVisibility()

        data class Visible(val textAlign: TextAlign) : TextAlignVisibility()
    }
}

private fun Icon(
    iconType: TextAlignmentIconType,
    setOneTimeAction: (OneTimeActionType) -> Unit
): HTMLElement = Span(null, classes = "option") {
    SvgIcon(20, 14) {
        SvgPath(iconType.iconPath)
    }

    setOnClickListener {
        setOneTimeAction(iconType.toTextAlignment())
    }
}

private enum class TextAlignmentIconType(
    val iconPath: String,
    val horizontalAlign: TextAlign.HorizontalAlign? = null,
    val verticalAlign: TextAlign.VerticalAlign? = null
) {
    HORIZONTAL_LEFT(
        "M3 2h14v2H3zM3 6h8v2H3zM3 10h10v2H3z",
        horizontalAlign = TextAlign.HorizontalAlign.LEFT
    ),
    HORIZONTAL_MIDDLE(
        "M3 2h14v2H3zM6 6h8v2H6zM5 10h10v2H5z",
        horizontalAlign = TextAlign.HorizontalAlign.MIDDLE
    ),
    HORIZONTAL_RIGHT(
        "M3 2h14v2H3zM9 6h8v2H9zM7 10h10v2H7z",
        horizontalAlign = TextAlign.HorizontalAlign.RIGHT
    ),
    VERTICAL_TOP(
        "M3 0h14v2H3zM3 4h14v2H3z",
        verticalAlign = TextAlign.VerticalAlign.TOP
    ),
    VERTICAL_MIDDLE(
        "M3 4h14v2H3zM3 8h14v2H3z",
        verticalAlign = TextAlign.VerticalAlign.MIDDLE
    ),
    VERTICAL_BOTTOM(
        "M3 8h14v2H3zM3 12h14v2H3z",
        verticalAlign = TextAlign.VerticalAlign.BOTTOM
    );

    fun toTextAlignment(): OneTimeActionType.TextAlignment =
        OneTimeActionType.TextAlignment(horizontalAlign, verticalAlign)
}
