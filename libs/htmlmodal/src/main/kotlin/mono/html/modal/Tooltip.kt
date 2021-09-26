package mono.html.modal

import kotlinx.browser.document
import mono.common.Cancelable
import mono.common.setTimeout
import mono.html.Span
import mono.html.px
import mono.html.style
import org.w3c.dom.DOMRect
import org.w3c.dom.Element

/**
 * Registers a tooltip for the targeting element.
 * The tooltip will be displayed along with of the anchor element based on the relative [position]
 * after 400ms hovering.
 */
fun Element.tooltip(text: String, position: TooltipPosition = TooltipPosition.BOTTOM) {
    val tooltip = Tooltip(this, text, position)
    addEventListener("mouseover", { tooltip.show() })
    addEventListener("mouseout", { tooltip.hide() })
}

enum class TooltipPosition(val clazz: String) {
    LEFT("left"),
    RIGHT("right"),
    TOP("top"),
    BOTTOM("bottom")
}

private class Tooltip(
    private val anchor: Element,
    private val text: String,
    private val position: TooltipPosition
) {
    private var view: Element? = null
    private var showTask: Cancelable? = null

    fun show() {
        if (showTask != null) {
            return
        }
        showTask = setTimeout(400, ::showInternal)
    }

    private fun showInternal() {
        view?.remove()

        val boundingRect = anchor.getBoundingClientRect()
        val tooltipArrowLeft = boundingRect.tooltipArrowLeft
        val tooltipArrowTop = boundingRect.tooltipArrowTop
        view = document.body?.Span("mono-tooltip ${position.clazz}", text) {
            val tooltipLeft = when (position) {
                TooltipPosition.LEFT -> tooltipArrowLeft - clientWidth
                TooltipPosition.RIGHT -> tooltipArrowLeft
                TooltipPosition.TOP,
                TooltipPosition.BOTTOM -> tooltipArrowLeft - clientWidth / 2
            }
            val tooltipTop = when (position) {
                TooltipPosition.LEFT,
                TooltipPosition.RIGHT -> tooltipArrowTop - clientHeight / 2
                TooltipPosition.TOP -> tooltipArrowTop - clientHeight
                TooltipPosition.BOTTOM -> tooltipArrowTop
            }
            style(
                "left" to tooltipLeft.px,
                "top" to tooltipTop.px
            )
        }
    }

    private val DOMRect.tooltipArrowLeft: Double
        get() = when (position) {
            TooltipPosition.LEFT -> left
            TooltipPosition.RIGHT -> right
            TooltipPosition.TOP,
            TooltipPosition.BOTTOM -> (left + right) / 2
        }

    private val DOMRect.tooltipArrowTop: Double
        get() = when (position) {
            TooltipPosition.LEFT,
            TooltipPosition.RIGHT -> (top + bottom) / 2
            TooltipPosition.TOP -> top
            TooltipPosition.BOTTOM -> bottom
        }

    fun hide() {
        showTask?.cancel()
        showTask = null
        view?.remove()
        view = null
    }
}
