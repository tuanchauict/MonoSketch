package mono.html.canvas.canvas

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
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D
import kotlin.math.PI
import kotlin.math.abs

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
        mousePointerLiveData.map { it is MousePointer.Move }.observe(lifecycleOwner) {
            isMouseMoving = it
        }
    }

    override fun drawInternal() {
        if (isMouseMoving) {
            return
        }
        context.strokeStyle = "#6b6b6b"
        context.fillStyle = "#6b6b6b"

        for (bound in interactionBounds) {
            when (bound) {
                is ScalableInteractionBound -> drawScalableInteractionBound(bound)
                is LineInteractionBound -> drawLineInteractionBound(bound)
            }.exhaustive
        }
    }

    private fun drawScalableInteractionBound(bound: ScalableInteractionBound) {
        val path = Path2D().apply {
            moveTo(drawingInfo.toXPx(bound.left), drawingInfo.toYPx(bound.top))
            lineTo(drawingInfo.toXPx(bound.right), drawingInfo.toYPx(bound.top))
            lineTo(drawingInfo.toXPx(bound.right), drawingInfo.toYPx(bound.bottom))
            lineTo(drawingInfo.toXPx(bound.left), drawingInfo.toYPx(bound.bottom))
            closePath()
        }
        context.strokeStyle = "#64b5f6"
        context.lineWidth = 1.0
        context.stroke(path)

        val dotPath = Path2D()
        context.beginPath()
        for (point in bound.interactionPoints) {
            dotPath.addDot(drawingInfo.toXPx(point.left), drawingInfo.toYPx(point.top))
        }
        context.strokeStyle = "#64b5f6"
        context.lineWidth = 2.0
        context.fillStyle = "#FFFFFF"
        context.stroke(dotPath)
        context.fill(dotPath)
    }

    private fun drawLineInteractionBound(bound: LineInteractionBound) {
        context.strokeStyle = "#6b6b6b"
        context.lineWidth = 2.5

        val dotPath = Path2D()
        context.beginPath()
        for (point in bound.interactionPoints) {
            dotPath.addDot(drawingInfo.toXPx(point.left), drawingInfo.toYPx(point.top))
        }
        context.strokeStyle = "#64b5f6"
        context.lineWidth = 2.0
        context.fillStyle = "#FFFFFF"
        context.stroke(dotPath)
        context.fill(dotPath)
    }

    private fun Path2D.addDot(xPx: Double, yPx: Double) {
        moveTo(xPx, yPx)
        arc(xPx, yPx, DOT_RADIUS, 0.0, FULL_CIRCLE_ARG)
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
