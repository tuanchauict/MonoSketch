package mono.graphics.geo

/**
 * A sealed class which indicates mouse event pointer types.
 */
sealed class MousePointer {
    object Idle : MousePointer()
    data class Down(val point: Point) : MousePointer()
    data class Move(val mouseDownPoint: Point, val point: Point) : MousePointer()
    data class Up(val mouseDownPoint: Point, val point: Point) : MousePointer()
    data class Click(val point: Point) : MousePointer()
}

