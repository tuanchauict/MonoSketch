/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.modal

import kotlinx.browser.document
import mono.common.Cancelable
import mono.common.post
import mono.common.setTimeout
import mono.html.Div
import mono.html.Span
import mono.html.SvgIcon
import mono.html.px
import mono.html.setOnMouseOutListener
import mono.html.setOnMouseOverListener
import mono.html.style
import org.w3c.dom.DOMRect
import org.w3c.dom.Element

/**
 * Registers a tooltip for the targeting element.
 * The tooltip will be displayed along with of the anchor element based on the relative [position]
 * after 600ms hovering.
 */
fun Element.tooltip(text: String, position: TooltipPosition = TooltipPosition.BOTTOM) {
    val tooltip = Tooltip(this, text, position)
    setOnMouseOverListener {
        tooltip.show()
    }
    setOnMouseOutListener {
        tooltip.hide()
    }
}

enum class TooltipPosition {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM
}

private class Tooltip(
    private val anchor: Element,
    private val text: String,
    private val position: TooltipPosition
) {
    private var arrowView: Element? = null
    private var bodyView: Element? = null
    private var showTask: Cancelable? = null

    fun show() {
        if (showTask != null) {
            return
        }
        showTask = setTimeout(600, ::showInternal)
    }

    fun hide() {
        showTask?.cancel()
        showTask = null
        arrowView?.remove()
        arrowView = null
        bodyView?.remove()
        bodyView = null
    }

    private fun showInternal() {
        if (arrowView != null) {
            return
        }
        val body = document.body ?: return
        createTooltip(body)
    }

    private fun createTooltip(body: Element) {
        val anchorPositionRect = anchor.getBoundingClientRect()

        val arrow = body.Div("mono-tooltip tooltip-arrow") {
            ArrowIcon()
        }

        arrowView = arrow
        bodyView = body.Span("mono-tooltip tooltip-body", text) {
            style("visibility" to "hidden")
        }
        post {
            arrow.adjustArrowPosition(anchorPositionRect)
            bodyView?.adjustTooltipBodyPosition(
                body,
                anchorPositionRect,
                arrow.getBoundingClientRect()
            )
        }
    }

    private fun Element.adjustArrowPosition(anchorPositionRect: DOMRect) {
        when (position) {
            TooltipPosition.LEFT -> style(
                "left" to (anchorPositionRect.left - clientWidth).px,
                "top" to (anchorPositionRect.centerYPx - halfHeightPx).px
            )

            TooltipPosition.RIGHT -> style(
                "left" to anchorPositionRect.right.px,
                "top" to (anchorPositionRect.centerYPx - halfHeightPx).px
            )

            TooltipPosition.TOP -> style(
                "left" to (anchorPositionRect.centerXPx - halfWidthPx).px,
                "top" to (anchorPositionRect.top - clientHeight).px
            )

            TooltipPosition.BOTTOM -> style(
                "left" to (anchorPositionRect.centerXPx - halfWidthPx).px,
                "top" to anchorPositionRect.bottom.px
            )
        }
    }

    private fun Element.adjustTooltipBodyPosition(
        body: Element,
        anchorPositionRect: DOMRect,
        arrowPositionRect: DOMRect
    ) {
        val leftMost = body.clientWidth - clientWidth - 4
        when (position) {
            TooltipPosition.LEFT -> {
                style(
                    "left" to (arrowPositionRect.left - clientWidth).px,
                    "top" to (anchorPositionRect.centerYPx - halfHeightPx).px
                )
            }

            TooltipPosition.RIGHT -> {
                style(
                    "left" to arrowPositionRect.right.px,
                    "top" to (anchorPositionRect.centerYPx - halfHeightPx).px
                )
            }

            TooltipPosition.TOP -> {
                val leftPx = (anchorPositionRect.centerXPx - halfWidthPx)
                style(
                    "top" to (arrowPositionRect.top - clientHeight + 0.5).px,
                    "left" to leftPx.coerceIn(0.0, leftMost.toDouble()).px
                )
            }

            TooltipPosition.BOTTOM -> {
                val leftPx = (anchorPositionRect.centerXPx - halfWidthPx)
                style(
                    "top" to arrowPositionRect.bottom.px,
                    "left" to leftPx.coerceIn(4.0, leftMost.toDouble()).px
                )
            }
        }
    }

    private fun Element.ArrowIcon() {
        when (position) {
            TooltipPosition.LEFT ->
                SvgIcon(5, 10, "M10 10L0 20V0L10 10Z")

            TooltipPosition.RIGHT ->
                SvgIcon(5, 10, "M0 10L10 0V20L0 10Z")

            TooltipPosition.TOP ->
                SvgIcon(10, 5, "M10 10L0 0H20L10 10Z")

            TooltipPosition.BOTTOM ->
                SvgIcon(10, 5, "M10 0L20 10H0L10 0Z")
        }
    }

    private val DOMRect.centerXPx: Double
        get() = (left + right) / 2
    private val DOMRect.centerYPx: Double
        get() = (top + bottom) / 2
    private val Element.halfWidthPx: Double
        get() = clientWidth.toDouble() / 2
    private val Element.halfHeightPx: Double
        get() = clientHeight.toDouble() / 2
}
