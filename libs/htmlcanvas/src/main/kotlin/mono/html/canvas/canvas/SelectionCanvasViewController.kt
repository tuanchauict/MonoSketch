package mono.html.canvas.canvas

import mono.graphics.geo.Rect
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D

/**
 * A canvas view controller to render the selection rectangle bound indicator.
 */
internal class SelectionCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {

    var selectingBound: Rect? = null

    override fun drawInternal() {
        val bound = selectingBound ?: return
        val leftPx = drawingInfo.toXPx(bound.left.toDouble())
        val topPx = drawingInfo.toYPx(bound.top.toDouble())
        val rightPx = drawingInfo.toXPx(bound.right + 1.0)
        val bottomPx = drawingInfo.toYPx(bound.bottom + 1.0)

        val path = Path2D().apply {
            moveTo(leftPx, topPx)
            lineTo(rightPx, topPx)
            lineTo(rightPx, bottomPx)
            lineTo(leftPx, bottomPx)
            closePath()
        }
        context.strokeStyle = BOUND_COLOR
        context.lineWidth = 1.0
        context.setLineDash(DASH_PATTERN)
        context.stroke(path)
    }

    companion object {
        private const val BOUND_COLOR = "#858585"
        private val DASH_PATTERN = arrayOf(8.0, 6.0)
    }
}
