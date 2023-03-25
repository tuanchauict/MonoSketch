package mono.html.canvas.canvas

import mono.graphics.geo.Rect
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.ui.theme.ThemeColor
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D

/**
 * A canvas view controller to render the selection rectangle bound indicator.
 */
internal class SelectionCanvasViewController(
    lifecycleOwner: LifecycleOwner,
    canvas: HTMLCanvasElement,
    drawingInfoLiveData: LiveData<DrawingInfoController.DrawingInfo>
) : BaseCanvasViewController(canvas) {

    var selectingBound: Rect? = null

    init {
        drawingInfoLiveData.observe(lifecycleOwner, listener = ::setDrawingInfo)
    }

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
        context.strokeStyle = ThemeColor.SelectionAreaStroke.colorCode
        context.lineWidth = 1.0
        context.setLineDash(DASH_PATTERN)
        context.stroke(path)
    }

    companion object {
        private val DASH_PATTERN = arrayOf(8.0, 6.0)
    }
}
