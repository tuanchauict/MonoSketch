/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.canvas.canvas

import kotlinx.browser.window
import mono.graphics.geo.Size
import mono.html.px
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.CanvasTextBaseline
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.LEFT
import org.w3c.dom.Path2D
import org.w3c.dom.TOP
import kotlin.math.max

// TODO: Pass drawing info livedata into this class and let it observes the change.
internal abstract class BaseCanvasViewController(private val canvas: HTMLCanvasElement) {
    protected val context: CanvasRenderingContext2D =
        canvas.getContext("2d") as CanvasRenderingContext2D

    internal var drawingInfo: DrawingInfoController.DrawingInfo

    init {
        drawingInfo =
            DrawingInfoController.DrawingInfo(canvasSizePx = Size(canvas.width, canvas.height))
    }

    protected fun setDrawingInfo(drawingInfo: DrawingInfoController.DrawingInfo) {
        val canvasSizePx = drawingInfo.canvasSizePx

        // Update canvas information causes canvas clearRect() which requires redraw
        val isSizeChange = this.drawingInfo.canvasSizePx != canvasSizePx
        if (isSizeChange) {
            val dpr = max(window.devicePixelRatio, 2.0)
            canvas.width = (canvasSizePx.width * dpr).toInt()
            canvas.height = (canvasSizePx.height * dpr).toInt()
            canvas.style.width = canvasSizePx.width.px
            canvas.style.height = canvasSizePx.height.px
            context.scale(dpr, dpr)
        }

        this.drawingInfo = drawingInfo

        if (isSizeChange) {
            draw()
        }
    }

    internal fun draw() {
        context.clearRect(
            x = 0.0,
            y = 0.0,
            w = drawingInfo.canvasSizePx.width.toDouble(),
            h = drawingInfo.canvasSizePx.height.toDouble()
        )

        context.font = drawingInfo.font
        context.textAlign = CanvasTextAlign.LEFT
        context.textBaseline = CanvasTextBaseline.TOP
        context.imageSmoothingEnabled = true
        drawInternal()
    }

    protected abstract fun drawInternal()

    protected fun drawText(text: String, row: Int, column: Int) {
        val yPx = drawingInfo.toYPx(row.toDouble())
        val xPx = drawingInfo.toXPx(column.toDouble())
        context.fillText(
            text,
            xPx + drawingInfo.cellCharOffsetPx.width,
            yPx + drawingInfo.cellCharOffsetPx.height
        )
    }

    protected fun Path2D.addHLine(xPx: Double, yPx: Double, widthPx: Double) {
        moveTo(xPx, yPx)
        lineTo(xPx + widthPx, yPx)
    }

    protected fun Path2D.addVLine(xPx: Double, yPx: Double, heightPx: Double) {
        moveTo(xPx, yPx)
        lineTo(xPx, yPx + heightPx)
    }
}
