package mono.graphics.geo

data class Point(val left: Int, val top: Int) {
    val row: Int get() = top
    val column: Int get() = left

    operator fun minus(base: Point): Point = Point(left - base.left, top - base.top)

    operator fun plus(base: Point): Point = Point(left + base.left, top + base.top)

    companion object {
        val INVALID = Point(Int.MIN_VALUE, Int.MIN_VALUE)
        val ZERO = Point(0, 0)
    }
}

data class PointF(val left: Float, val top: Float) {
    val row: Float get() = top

    val column: Float get() = left
}

data class DirectedPoint(val direction: Direction, val left: Int, val top: Int) {

    val point: Point
        get() = Point(left, top)

    enum class Direction {
        HORIZONTAL, VERTICAL
    }
}
