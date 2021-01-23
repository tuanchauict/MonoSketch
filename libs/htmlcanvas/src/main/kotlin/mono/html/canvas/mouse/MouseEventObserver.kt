package mono.html.canvas.mouse

import mono.graphics.geo.Point
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.MouseEvent

/**
 * A class to attach and observe mouse events happening on the canvas.
 * All events will be combined into a single live data [mousePointerLiveData].
 */
class MouseEventObserver(container: HTMLDivElement) {
    private val mousePointerMutableLiveData: MutableLiveData<MousePointer> =
        MutableLiveData(MousePointer.Idle)
    val mousePointerLiveData: LiveData<MousePointer> = mousePointerMutableLiveData

    init {
        container.onmousedown = ::setMouseDownPointer
        container.onmouseup = ::setMouseUpPointer
        container.onmousemove = ::setMouseMovePointer
    }

    private fun setMouseDownPointer(event: MouseEvent) {
        mousePointerMutableLiveData.value = MousePointer.Down(event.toPoint())
    }

    private fun setMouseUpPointer(event: MouseEvent) {
        val currentValue = mousePointerLiveData.value
        val clickPoint = event.toPoint()
        mousePointerMutableLiveData.value = when (mousePointerMutableLiveData.value) {
            is MousePointer.Down,
            is MousePointer.Move -> MousePointer.Up(clickPoint)
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
        mousePointerMutableLiveData.value = when (mousePointerMutableLiveData.value) {
            is MousePointer.Down,
            is MousePointer.Move -> MousePointer.Move(event.toPoint())
            is MousePointer.Up,
            is MousePointer.Click,
            MousePointer.Idle -> MousePointer.Idle
        }
    }

    private fun MouseEvent.toPoint(): Point = Point(clientX, clientY)
}

/**
 * A sealed class which indicates mouse event pointer types.
 */
sealed class MousePointer {
    object Idle : MousePointer()
    data class Down(val point: Point) : MousePointer()
    data class Move(val point: Point) : MousePointer()
    data class Up(val point: Point) : MousePointer()
    data class Click(val point: Point) : MousePointer()
}
