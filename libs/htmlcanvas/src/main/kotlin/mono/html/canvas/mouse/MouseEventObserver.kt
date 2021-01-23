package mono.html.canvas.mouse

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
        mousePointerMutableLiveData.value = MousePointer.Down(event.clientX, event.clientY)
    }

    private fun setMouseUpPointer(event: MouseEvent) {
        mousePointerMutableLiveData.value = when (mousePointerMutableLiveData.value) {
            is MousePointer.Down,
            is MousePointer.Move -> MousePointer.Up(event.clientY, event.clientY)
            is MousePointer.Up,
            MousePointer.Idle -> MousePointer.Idle
        }
        mousePointerMutableLiveData.value = MousePointer.Idle
    }

    private fun setMouseMovePointer(event: MouseEvent) {
        mousePointerMutableLiveData.value = when (mousePointerMutableLiveData.value) {
            is MousePointer.Down,
            is MousePointer.Move -> MousePointer.Move(event.clientX, event.clientY)
            is MousePointer.Up,
            MousePointer.Idle -> MousePointer.Idle
        }
    }
}

sealed class MousePointer {
    object Idle : MousePointer()
    data class Down(val left: Int, val top: Int) : MousePointer()
    data class Move(val left: Int, val top: Int) : MousePointer()
    data class Up(val left: Int, val top: Int) : MousePointer()
}
