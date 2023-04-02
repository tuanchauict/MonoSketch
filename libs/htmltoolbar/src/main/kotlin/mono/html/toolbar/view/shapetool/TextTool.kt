@file:Suppress("FunctionName", "ktlint:filename")

package mono.html.toolbar.view.shapetool

import mono.actionmanager.OneTimeActionType
import mono.html.Div
import mono.html.Span
import mono.html.SvgPath
import mono.html.toolbar.view.components.CloudItemFactory
import mono.html.toolbar.view.components.CloudViewBinder
import mono.html.toolbar.view.components.OptionCloud
import mono.html.toolbar.view.shapetool.TextAlignmentIconType.HORIZONTAL_LEFT
import mono.html.toolbar.view.shapetool.TextAlignmentIconType.HORIZONTAL_MIDDLE
import mono.html.toolbar.view.shapetool.TextAlignmentIconType.HORIZONTAL_RIGHT
import mono.html.toolbar.view.shapetool.TextAlignmentIconType.VERTICAL_BOTTOM
import mono.html.toolbar.view.shapetool.TextAlignmentIconType.VERTICAL_MIDDLE
import mono.html.toolbar.view.shapetool.TextAlignmentIconType.VERTICAL_TOP
import mono.html.toolbar.view.utils.CssClass
import mono.html.toolbar.view.utils.SvgIcon
import mono.html.toolbar.view.utils.bindClass
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.distinctUntilChange
import mono.livedata.map
import mono.shape.extra.style.TextAlign
import org.w3c.dom.Element

internal class TextSectionViewController(
    lifecycleOwner: LifecycleOwner,
    container: Element,
    liveData: LiveData<TextAlignVisibility>,
    private val setOneTimeAction: (OneTimeActionType) -> Unit
) {
    val visibilityStateLiveData: LiveData<Boolean>

    init {
        val textAlignLiveData = liveData
            .map { (it as? TextAlignVisibility.Visible)?.textAlign }
            .distinctUntilChange()
        visibilityStateLiveData = textAlignLiveData
            .map { it != null }
            .distinctUntilChange()

        container.Section("TEXT") { section ->
            val toolContainer = Div("tool-text")
            val horizontalAlignBinder = toolContainer.Group(
                "Alignment",
                listOf(HORIZONTAL_LEFT, HORIZONTAL_MIDDLE, HORIZONTAL_RIGHT)
            )

            val verticalAlignBinder = toolContainer.Group(
                "Position",
                listOf(VERTICAL_TOP, VERTICAL_MIDDLE, VERTICAL_BOTTOM)
            )

            textAlignLiveData.observe(lifecycleOwner) {
                section.bindClass(CssClass.HIDE, it == null)
                if (it != null) {
                    horizontalAlignBinder.setSelectedItem(it.horizontalAlign.ordinal)
                    verticalAlignBinder.setSelectedItem(it.verticalAlign.ordinal)
                }
            }
        }
    }

    private fun Element.Group(label: String, icons: List<TextAlignmentIconType>): CloudViewBinder {
        val alignmentDiv = Div("row") {
            Span("tool-title", text = label)
        }
        return alignmentDiv.Icons(icons) {
            setOneTimeAction(it.toTextAlignment())
        }
    }

    sealed class TextAlignVisibility {
        object Hide : TextAlignVisibility()

        data class Visible(val textAlign: TextAlign) : TextAlignVisibility()
    }
}

private fun Element.Icons(
    icons: List<TextAlignmentIconType>,
    onSelect: (TextAlignmentIconType) -> Unit
): CloudViewBinder {
    val factory = CloudItemFactory(icons.size) {
        SvgIcon(20, 14) {
            SvgPath(icons[it].iconPath)
        }
    }
    return OptionCloud(factory) {
        onSelect(icons[it])
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
