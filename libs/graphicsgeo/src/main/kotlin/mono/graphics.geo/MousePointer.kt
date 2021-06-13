package mono.graphics.geo

/**
 * A sealed class which indicates mouse event pointer types.
 */
sealed class MousePointer {
    object Idle : MousePointer()

    data class Down(
        val point: Point,
        val pointPx: Point,
        val isWithShiftKey: Boolean
    ) : MousePointer()

    data class Drag(
        val mouseDownPoint: Point,
        val point: Point,
        val isWithShiftKey: Boolean
    ) : MousePointer()

    data class Up(
        val mouseDownPoint: Point,
        val point: Point,
        val isWithShiftKey: Boolean
    ) : MousePointer()

    data class Click(val point: Point, val isWithShiftKey: Boolean) : MousePointer()
}
