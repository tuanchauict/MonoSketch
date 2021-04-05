package mono.html.canvas.canvas

import mono.shapebound.InteractionBound
import mono.shapebound.ScalableInteractionBound
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D

/**
 * A canvas view controller to render the interaction indicators.
 */
internal class InteractionCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {

    var interactionBounds: List<InteractionBound> = emptyList()

    override fun drawInternal() {
        context.strokeStyle = "#6b6b6b"
        context.fillStyle = "#6b6b6b"

        for (bound in interactionBounds) {
            when (bound) {
                is ScalableInteractionBound -> drawScalableInteractionBound(bound)
            }
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
        context.stroke(path)

        for (point in bound.interactionPoints) {
            drawDot(drawingInfo.toXPx(point.left), drawingInfo.toYPx(point.top))
        }
    }

    private fun drawDot(xPx: Double, yPx: Double) {
        val dotSizePx = 6.0
        context.fillRect(xPx - dotSizePx / 2, yPx - dotSizePx / 2, dotSizePx, dotSizePx)
    }
}
