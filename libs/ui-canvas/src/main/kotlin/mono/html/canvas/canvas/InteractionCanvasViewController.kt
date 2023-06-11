/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.canvas.canvas

import kotlin.math.PI
import kotlin.math.abs
import mono.common.exhaustive
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.map
import mono.shapebound.InteractionBound
import mono.shapebound.InteractionPoint
import mono.shapebound.LineInteractionBound
import mono.shapebound.ScalableInteractionBound
import mono.ui.theme.ThemeColor
import org.w3c.dom.HIGH
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.ImageSmoothingQuality
import org.w3c.dom.Path2D

/**
 * A canvas view controller to render the interaction indicators.
 */
internal class InteractionCanvasViewController(
    lifecycleOwner: LifecycleOwner,
    canvas: HTMLCanvasElement,
    drawingInfoLiveData: LiveData<DrawingInfoController.DrawingInfo>,
    mousePointerLiveData: LiveData<MousePointer>
) : BaseCanvasViewController(canvas) {

    var interactionBounds: List<InteractionBound> = emptyList()

    private var isMouseMoving: Boolean = false

    init {
        drawingInfoLiveData.observe(lifecycleOwner, listener = ::setDrawingInfo)
        mousePointerLiveData.map { it is MousePointer.Drag }.observe(lifecycleOwner) {
            isMouseMoving = it
        }
        context.imageSmoothingQuality = ImageSmoothingQuality.HIGH
    }

    override fun drawInternal() {
        for (bound in interactionBounds) {
            when (bound) {
                is ScalableInteractionBound -> drawScalableInteractionBound(bound)
                is LineInteractionBound -> drawLineInteractionBound(bound)
            }.exhaustive
        }
    }

    private fun drawScalableInteractionBound(bound: ScalableInteractionBound) {
        val leftPx = drawingInfo.toXPx(bound.left)
        val rightPx = drawingInfo.toXPx(bound.right)
        val topPx = drawingInfo.toYPx(bound.top)
        val bottomPx = drawingInfo.toYPx(bound.bottom)
        val path = Path2D().apply {
            moveTo(leftPx, topPx)
            lineTo(rightPx, topPx)
            lineTo(rightPx, bottomPx)
            lineTo(leftPx, bottomPx)
            closePath()
        }
        context.strokeStyle = ThemeColor.SelectionBoundStroke.colorCode
        context.lineWidth = 1.0
        context.stroke(path)

        if (isMouseMoving) {
            return
        }

        bound.interactionPoints.draw(ThemeColor.SelectionDotStroke, ThemeColor.SelectionDotFill)
    }

    private fun drawLineInteractionBound(bound: LineInteractionBound) {
        if (isMouseMoving) {
            return
        }

        bound.interactionPoints.draw(
            ThemeColor.SelectionDotStroke,
            ThemeColor.SelectionDotFill
        )
        // Draw a small dot inside the start dot to make it simpler to distinguish start and end
        // dots.
        bound.interactionPoints.take(1)
            .draw(ThemeColor.SelectionDotStroke, ThemeColor.SelectionDotStroke, 1.0)
    }

    private fun List<InteractionPoint>.draw(
        strokeColor: ThemeColor,
        fillColor: ThemeColor,
        dotRadius: Double = DOT_RADIUS
    ) {
        val dotPath = Path2D()
        context.beginPath()
        for (point in this) {
            dotPath.addDot(drawingInfo.toXPx(point.left), drawingInfo.toYPx(point.top), dotRadius)
        }
        context.strokeStyle = strokeColor.colorCode
        context.lineWidth = 2.0
        context.fillStyle = fillColor.colorCode
        context.imageSmoothingEnabled = true
        context.stroke(dotPath)
        context.fill(dotPath)
        context.imageSmoothingEnabled = false
    }

    private fun Path2D.addDot(xPx: Double, yPx: Double, dotRadius: Double) {
        moveTo(xPx, yPx)
        arc(xPx, yPx, dotRadius, 0.0, FULL_CIRCLE_ARG)
    }

    fun getInteractionPoint(pointPx: Point): InteractionPoint? {
        for (bound in interactionBounds.reversed()) {
            val closePoint = bound.interactionPoints.lastOrNull { it.isAround(pointPx) }
            if (closePoint != null) {
                return closePoint
            }
        }

        return null
    }

    private fun InteractionPoint.isAround(pointPx: Point): Boolean {
        val leftPx = drawingInfo.toXPx(left)
        val topPx = drawingInfo.toYPx(top)
        return abs(leftPx - pointPx.left) < 6 && abs(topPx - pointPx.top) < 6
    }

    companion object {
        private const val DOT_RADIUS = 3.2
        private const val FULL_CIRCLE_ARG = 2 * PI
    }
}
