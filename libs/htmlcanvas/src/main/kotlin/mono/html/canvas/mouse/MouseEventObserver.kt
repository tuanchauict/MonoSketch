package mono.html.canvas.mouse

import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.MouseEvent

/**
 * A class to attach and observe mouse events happening on the canvas.
 * All events will be combined into a single live data [mousePointerLiveData].
 */
class MouseEventObserver(
    container: HTMLDivElement,
    private val toBoardLeft: (xPx: Int) -> Int,
    private val toBoardTop: (yPx: Int) -> Int
) {
    private val mousePointerMutableLiveData: MutableLiveData<MousePointer> =
        MutableLiveData(MousePointer.Idle)
    val mousePointerLiveData: LiveData<MousePointer> = mousePointerMutableLiveData

    init {
        container.onmousedown = ::setMouseDownPointer
        container.onmouseup = ::setMouseUpPointer
        container.onmousemove = ::setMouseMovePointer
    }

    private fun setMouseDownPointer(event: MouseEvent) {
        if (mousePointerLiveData.value == MousePointer.Idle) {
            mousePointerMutableLiveData.value = MousePointer.Down(event.toPoint())
        }
    }

    private fun setMouseUpPointer(event: MouseEvent) {
        val currentValue = mousePointerLiveData.value
        val clickPoint = event.toPoint()
        mousePointerMutableLiveData.value = when (currentValue) {
            is MousePointer.Down -> MousePointer.Up(currentValue.point, clickPoint)
            is MousePointer.Move -> MousePointer.Up(currentValue.mouseDownPoint, clickPoint)
            is MousePointer.Up,
            is MousePointer.Click,
            MousePointer.Idle -> MousePointer.Idle
        }
        if (currentValue is MousePointer.Down) {
            mousePointerMutableLiveData.value = MousePointer.Click(clickPoint)
        }
        mousePointerMutableLiveData.value = MousePointer.Idle
    }

    private fun setMouseMovePointer(event: MouseEvent) {
        val mousePointer = mousePointerLiveData.value
        mousePointerMutableLiveData.value = when (mousePointer) {
            is MousePointer.Down -> MousePointer.Move(mousePointer.point, event.toPoint())
            is MousePointer.Move -> MousePointer.Move(mousePointer.mouseDownPoint, event.toPoint())
            is MousePointer.Up,
            is MousePointer.Click,
            MousePointer.Idle -> MousePointer.Idle
        }
    }

    private fun MouseEvent.toPoint(): Point = Point(toBoardLeft(clientX), toBoardTop(clientY))
}
