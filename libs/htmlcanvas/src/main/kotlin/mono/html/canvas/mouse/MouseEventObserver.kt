package mono.html.canvas.mouse

import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.html.canvas.canvas.DrawingInfoController
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent

/**
 * A class to attach and observe mouse events happening on the canvas.
 * All events will be combined into a single live data [mousePointerLiveData].
 */
internal class MouseEventObserver(
    lifecycleOwner: LifecycleOwner,
    container: HTMLDivElement,
    drawingInfoLiveData: LiveData<DrawingInfoController.DrawingInfo>
) {
    private var containerPosition: Point

    private val mousePointerMutableLiveData: MutableLiveData<MousePointer> =
        MutableLiveData(MousePointer.Idle)
    val mousePointerLiveData: LiveData<MousePointer> =
        mousePointerMutableLiveData.distinctUntilChange()

    private var drawingInfo: DrawingInfoController.DrawingInfo =
        drawingInfoLiveData.value

    private var mouseWheelDeltaX: Float = 0F
    private var mouseWheelDeltaY: Float = 0F
    private val drawingOffsetPointPxMutableLiveData: MutableLiveData<Point> =
        MutableLiveData(Point.ZERO)
    val drawingOffsetPointPxLiveData: LiveData<Point> = drawingOffsetPointPxMutableLiveData

    init {
        containerPosition = container.getPosition()

        container.onmousedown = ::setMouseDownPointer
        container.onmouseup = ::setMouseUpPointer
        container.onmousemove = ::setMouseMovePointer
        container.onwheel = ::setMouseWheel

        drawingInfoLiveData.observe(lifecycleOwner) {
            drawingInfo = it
            containerPosition = container.getPosition()
        }
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
            MousePointer.Idle -> MousePointer.Idle
        }
        if (currentValue is MousePointer.Down) {
            mousePointerMutableLiveData.value = MousePointer.Click(clickPoint, event.shiftKey)
        }
        mousePointerMutableLiveData.value = MousePointer.Idle
    }

    private fun setMouseMovePointer(event: MouseEvent) {
        val newPointer = when (val mousePointer = mousePointerLiveData.value) {
            is MousePointer.Down ->
                MousePointer.Drag(mousePointer.point, event.toPoint(), event.shiftKey)
                    .takeIf { it.point != mousePointer.point }

            is MousePointer.Drag ->
                MousePointer.Drag(mousePointer.mouseDownPoint, event.toPoint(), event.shiftKey)

            is MousePointer.Move,
            is MousePointer.Up,
            is MousePointer.Click,
            MousePointer.Idle -> MousePointer.Move(event.toPoint(), event.toPointPx())
        } ?: return
        mousePointerMutableLiveData.value = newPointer
    }

    private fun setMouseWheel(event: WheelEvent) {
        event.preventDefault()
        event.stopPropagation()

        val scrollHorizontalThresholdPx = SCROLL_THRESHOLD_PIXEL
        val scrollVerticalThresholdPx = SCROLL_THRESHOLD_PIXEL

        val wheelDeltaLeft =
            event.deltaX.toFloat() * SCROLL_SPEED_RATIO / scrollHorizontalThresholdPx
        val wheelDeltaTop = event.deltaY.toFloat() * SCROLL_SPEED_RATIO / scrollVerticalThresholdPx
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

    private fun MouseEvent.toPoint(): Point =
        Point(
            drawingInfo.toBoardColumn(clientX - containerPosition.left),
            drawingInfo.toBoardRow(clientY - containerPosition.top)
        )

    private fun MouseEvent.toPointPx(): Point =
        Point(
            clientX - containerPosition.left,
            clientY - containerPosition.top
        )

    private fun HTMLDivElement.getPosition(): Point {
        val containerBounding = getBoundingClientRect()
        return Point(containerBounding.left.toInt(), containerBounding.top.toInt())
    }

    companion object {
        private const val SCROLL_SPEED_RATIO = 1 / 1.5F
        private const val SCROLL_THRESHOLD_PIXEL = 1F
    }
}
