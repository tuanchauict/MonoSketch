/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.canvas.mouse

import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.html.canvas.canvas.DrawingInfoController
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
import mono.ui.appstate.state.ScrollMode
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import kotlin.js.Date

/**
 * A class to attach and observe mouse events happening on the canvas.
 * All events will be combined into a single live data [mousePointerLiveData].
 */
internal class MouseEventObserver(
    lifecycleOwner: LifecycleOwner,
    container: HTMLDivElement,
    drawingInfoLiveData: LiveData<DrawingInfoController.DrawingInfo>,
    shiftKeyStateLiveData: LiveData<Boolean>,
    private val scrollModeLiveData: LiveData<ScrollMode>
) {
    private val mousePointerMutableLiveData = MutableLiveData<MousePointer>(MousePointer.Idle)
    val mousePointerLiveData: LiveData<MousePointer> =
        mousePointerMutableLiveData.distinctUntilChange()

    private var drawingInfo: DrawingInfoController.DrawingInfo =
        drawingInfoLiveData.value

    private var mouseWheelDeltaX: Float = 0F
    private var mouseWheelDeltaY: Float = 0F
    private val drawingOffsetPointPxMutableLiveData = MutableLiveData(Point.ZERO)
    val drawingOffsetPointPxLiveData: LiveData<Point> = drawingOffsetPointPxMutableLiveData

    private val mouseDoubleClickDetector = MouseDoubleClickDetector()

    init {
        container.onmousedown = ::setMouseDownPointer
        container.onmouseup = ::setMouseUpPointer
        container.onmousemove = ::setMouseMovePointer
        container.onwheel = ::setMouseWheel

        drawingInfoLiveData.observe(lifecycleOwner) {
            drawingInfo = it
        }
        shiftKeyStateLiveData.observe(lifecycleOwner) {
            val currentMouseValue = mousePointerLiveData.value
            if (currentMouseValue is MousePointer.Drag) {
                mousePointerMutableLiveData.value = currentMouseValue.copy(isWithShiftKey = it)
            }
        }
    }

    fun forceUpdateOffset(offsetPx: Point) {
        drawingOffsetPointPxMutableLiveData.value = offsetPx
    }

    private fun setMouseDownPointer(event: MouseEvent) {
        if (mousePointerLiveData.value == MousePointer.Idle ||
            mousePointerLiveData.value is MousePointer.Move
        ) {
            mousePointerMutableLiveData.value =
                MousePointer.Down(event.toPoint(), event.toPointPx(), event.shiftKey)
        }
    }

    private fun setMouseUpPointer(event: MouseEvent) {
        val isDoubleClick = mouseDoubleClickDetector.onMouseUp()
        val currentValue = mousePointerLiveData.value
        val clickPoint = event.toPoint()

        mousePointerMutableLiveData.value = when (currentValue) {
            is MousePointer.Down ->
                MousePointer.Up(currentValue.point, clickPoint, event.shiftKey)

            is MousePointer.Drag ->
                MousePointer.Up(currentValue.mouseDownPoint, clickPoint, event.shiftKey)

            is MousePointer.Move,
            is MousePointer.Up,
            is MousePointer.Click,
            is MousePointer.DoubleClick,
            MousePointer.Idle -> MousePointer.Idle
        }

        if (currentValue is MousePointer.Down) {
            mousePointerMutableLiveData.value = if (isDoubleClick) {
                MousePointer.DoubleClick(event.toPoint())
            } else {
                MousePointer.Click(clickPoint, event.shiftKey)
            }
        }
        mousePointerMutableLiveData.value = MousePointer.Idle
    }

    private fun setMouseMovePointer(event: MouseEvent) {
        mouseDoubleClickDetector.reset()
        val newPointer = when (val mousePointer = mousePointerLiveData.value) {
            is MousePointer.Down ->
                MousePointer.Drag(mousePointer.point, event.toPoint(), event.shiftKey)
                    .takeIf { it.point != mousePointer.point }

            is MousePointer.Drag ->
                MousePointer.Drag(mousePointer.mouseDownPoint, event.toPoint(), event.shiftKey)

            is MousePointer.Move,
            is MousePointer.Up,
            is MousePointer.Click,
            is MousePointer.DoubleClick,
            MousePointer.Idle -> MousePointer.Move(event.toPoint(), event.toPointPx())
        } ?: return
        mousePointerMutableLiveData.value = newPointer
    }

    private fun setMouseWheel(event: WheelEvent) {
        event.preventDefault()
        event.stopPropagation()

        val scrollHorizontalThresholdPx = SCROLL_THRESHOLD_PIXEL
        val scrollVerticalThresholdPx = SCROLL_THRESHOLD_PIXEL

        val (scrollDeltaX, scrollDeltaY) = event.getScrollDelta()

        val wheelDeltaLeft = scrollDeltaX * SCROLL_SPEED_RATIO / scrollHorizontalThresholdPx
        val wheelDeltaTop = scrollDeltaY * SCROLL_SPEED_RATIO / scrollVerticalThresholdPx
        val accumulateWheelDeltaLeft = mouseWheelDeltaX + wheelDeltaLeft
        val accumulateWheelDeltaTop = mouseWheelDeltaY + wheelDeltaTop

        val usableDeltaLeft = accumulateWheelDeltaLeft.toInt()
        val usableDeltaTop = accumulateWheelDeltaTop.toInt()

        if (usableDeltaLeft != 0 || usableDeltaTop != 0) {
            val offsetLeft =
                drawingInfo.offsetPx.left - usableDeltaLeft * scrollHorizontalThresholdPx.toInt()
            val offsetTop =
                drawingInfo.offsetPx.top - usableDeltaTop * scrollVerticalThresholdPx.toInt()
            drawingOffsetPointPxMutableLiveData.value = Point(offsetLeft, offsetTop)
        }

        mouseWheelDeltaX = accumulateWheelDeltaLeft - usableDeltaLeft
        mouseWheelDeltaY = accumulateWheelDeltaTop - usableDeltaTop
    }

    /**
     * Returns scroll delta x and y of the wheel event after adjusting with meta keys:
     * - When Alt/Option key is pressed, swap delta x and delta y (this is for those who use mouse)
     * - When Shift key is pressed, scroll mode other than [ScrollMode.BOTH] will be flipped
     *   (vertical -> horizontal, horizontal -> vertical)
     * - Returns the delta x and delta y of the wheel event according to adjusted scroll mode (with
     *   Shift key) and delta value (with Alt/Option key):
     *   - Vertical: delta x = 0
     *   - Horizontal: delta y = 0
     *   - Both: delta x and delta y = delta value
     */
    private fun WheelEvent.getScrollDelta(): Pair<Float, Float> {
        val deltaX = deltaX.toFloat()
        val deltaY = deltaY.toFloat()
        val scrollDeltaX = if (altKey) deltaY else deltaX
        val scrollDeltaY = if (altKey) deltaX else deltaY

        val scrollMode = when (scrollModeLiveData.value) {
            ScrollMode.BOTH -> ScrollMode.BOTH
            ScrollMode.VERTICAL -> if (shiftKey) ScrollMode.HORIZONTAL else ScrollMode.VERTICAL
            ScrollMode.HORIZONTAL -> if (shiftKey) ScrollMode.VERTICAL else ScrollMode.HORIZONTAL
        }
        return when (scrollMode) {
            ScrollMode.BOTH -> scrollDeltaX to scrollDeltaY
            ScrollMode.VERTICAL -> 0.0F to scrollDeltaY
            ScrollMode.HORIZONTAL -> scrollDeltaX to 0.0F
        }
    }

    private fun MouseEvent.toPoint(): Point =
        Point(
            drawingInfo.toBoardColumn(offsetX.toInt()),
            drawingInfo.toBoardRow(offsetY.toInt())
        )

    private fun MouseEvent.toPointPx(): Point = Point(offsetX.toInt(), offsetY.toInt())

    /**
     * A class for detecting double click.
     * If two mouse ups happen within 500ms, it's a double click.
     */
    private class MouseDoubleClickDetector {
        private var lastMouseUpMillis: Long = 0
        private var mouseUpCount: Int = 0

        fun onMouseUp(): Boolean {
            val isDoubleClick = Date.now().toLong() - lastMouseUpMillis < 500 && mouseUpCount > 0
            lastMouseUpMillis = Date.now().toLong()
            mouseUpCount += 1

            return isDoubleClick
        }

        fun reset() {
            lastMouseUpMillis = 0L
            mouseUpCount = 0
        }
    }

    companion object {
        private const val SCROLL_SPEED_RATIO = 1 / 1.3F
        private const val SCROLL_THRESHOLD_PIXEL = 1F
    }
}
