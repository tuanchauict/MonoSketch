package mono.html.canvas.canvas

import mono.graphics.geo.EdgeRelatedPosition
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.canvas.CanvasViewController
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D
import kotlin.math.abs

/**
 * A canvas view controller to render the selection rectangle bound indicator.
 */
internal class SelectionCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {

    var selectingBound: Rect? = null
    var boundType: CanvasViewController.BoundType = CanvasViewController.BoundType.NINE_DOTS

    override fun drawInternal() {
        val bound = getBoundRectPx() ?: return

        val path = Path2D().apply {
            moveTo(bound.left, bound.top)
            lineTo(bound.right, bound.top)
            lineTo(bound.right, bound.bottom)
            lineTo(bound.left, bound.bottom)
            closePath()
        }
        context.strokeStyle = boundType.boundStyleColor
        context.lineWidth = 1.0
        val lineDash = if (boundType.isDash) arrayOf(8.0, 6.0) else emptyArray()
        context.setLineDash(lineDash)
        context.fillStyle = boundType.boundStyleColor
        context.stroke(path)

        if (boundType == CanvasViewController.BoundType.NINE_DOTS) {
            drawDots(bound.left, bound.top, bound.right, bound.bottom)
        }
    }

    private fun drawDots(
        leftPx: Double,
        topPx: Double,
        rightPx: Double,
        bottomPx: Double
    ) {
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

    fun getDotIndex(pointPx: Point): EdgeRelatedPosition? {
        val rect = getBoundRectPx() ?: return null
        return when {
            pointPx.isAround(rect.left, rect.top) -> EdgeRelatedPosition.LEFT_TOP
            pointPx.isAround(rect.horizontalMiddle, rect.top) -> EdgeRelatedPosition.MIDDLE_TOP
            pointPx.isAround(rect.right, rect.top) -> EdgeRelatedPosition.RIGHT_TOP

            pointPx.isAround(rect.left, rect.verticalMiddle) -> EdgeRelatedPosition.LEFT_MIDDLE
            pointPx.isAround(rect.right, rect.verticalMiddle) -> EdgeRelatedPosition.RIGHT_MIDDLE

            pointPx.isAround(rect.left, rect.bottom) -> EdgeRelatedPosition.LEFT_BOTTOM
            pointPx.isAround(rect.horizontalMiddle, rect.bottom) -> EdgeRelatedPosition.MIDDLE_BOTTOM
            pointPx.isAround(rect.right, rect.bottom) -> EdgeRelatedPosition.RIGHT_BOTTOM

            else -> null
        }
    }

    private fun Point.isAround(xPx: Double, yPx: Double): Boolean {
        return abs(left - xPx) < 6 && abs(top - yPx) < 6
    }

    private fun getBoundRectPx(): RectD? {
        val bound = selectingBound ?: return null
        return RectD(
            drawingInfo.toXPx(bound.left.toDouble()),
            drawingInfo.toYPx(bound.top.toDouble()),
            drawingInfo.toXPx(bound.right + 1.0),
            drawingInfo.toYPx(bound.bottom + 1.0),
        )
    }

    private data class RectD(
        val left: Double,
        val top: Double,
        val right: Double,
        val bottom: Double
    ) {
        val horizontalMiddle: Double = (left + right) / 2
        val verticalMiddle: Double = (top + bottom) / 2
    }
}
