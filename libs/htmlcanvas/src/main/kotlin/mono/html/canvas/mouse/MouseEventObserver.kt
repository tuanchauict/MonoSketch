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

    init {
        containerPosition = container.getPosition()

        container.onmousedown = ::setMouseDownPointer
        container.onmouseup = ::setMouseUpPointer
        container.onmousemove = ::setMouseMovePointer

        drawingInfoLiveData.observe(lifecycleOwner) {
            drawingInfo = it
            containerPosition = container.getPosition()
        }
    }

    private fun setMouseDownPointer(event: MouseEvent) {
        if (mousePointerLiveData.value == MousePointer.Idle) {
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

    private fun MouseEvent.toPoint(): Point =
        Point(
            drawingInfo.toBoardColumn(clientX - containerPosition.left),
            drawingInfo.toBoardRow(clientY - containerPosition.top)
        )

    private fun MouseEvent.toPointPx(): Point =
        Point(
            clientX - drawingInfo.offsetPx.left - containerPosition.left,
            clientY - drawingInfo.offsetPx.top - containerPosition.top
        )

    private fun HTMLDivElement.getPosition(): Point {
        val containerBounding = getBoundingClientRect()
        return Point(containerBounding.left.toInt(), containerBounding.top.toInt())
    }
}
