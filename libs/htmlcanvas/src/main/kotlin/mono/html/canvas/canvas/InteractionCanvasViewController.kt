package mono.html.canvas.canvas

import mono.graphics.geo.Rect
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D

/**
 * A canvas view controller to render the selection rectangle bound and interaction indicators.
 * TODO: Update [selectedShapesBoundingRect] value
 */
internal class InteractionCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {

    var selectedShapesBoundingRect: Rect? = Rect.byLTWH(50, 5, 10, 10)

    override fun drawInternal() {
        val bound = selectedShapesBoundingRect ?: return
        val offsetPx = drawingInfo.cellSizePx.width
        val leftPx = drawingInfo.toXPx(bound.left) - offsetPx
        val topPx = drawingInfo.toYPx(bound.top) - offsetPx
        val rightPx = drawingInfo.toXPx(bound.right) + offsetPx
        val bottomPx = drawingInfo.toYPx(bound.bottom) + offsetPx

        val path = Path2D().apply {
            moveTo(leftPx, topPx)
            lineTo(rightPx, topPx)
            lineTo(rightPx, bottomPx)
            lineTo(leftPx, bottomPx)
            closePath()
        }
        context.strokeStyle = "#6b6b6b"
        context.fillStyle = "#6b6b6b"
        context.stroke(path)

        drawDot(leftPx, topPx)
        drawDot(leftPx, (topPx + bottomPx) / 2.0)
        drawDot(leftPx, bottomPx)
        drawDot(rightPx, topPx)
        drawDot(rightPx, (topPx + bottomPx) / 2.0)
        drawDot(rightPx, bottomPx)
        drawDot((leftPx + rightPx) / 2.0, topPx)
        drawDot((leftPx + rightPx) / 2.0, bottomPx)
    }

    private fun drawDot(xPx: Double, yPx: Double) {
        val dotSizePx = 6.0
        context.fillRect(xPx - dotSizePx / 2, yPx - dotSizePx / 2, dotSizePx, dotSizePx)
    }
}
