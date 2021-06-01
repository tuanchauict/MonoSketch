package mono.graphics.geo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Point(
    @SerialName("l")
    val left: Int,
    @SerialName("t")
    val top: Int
) {
    val row: Int get() = top
    val column: Int get() = left

    operator fun minus(base: Point): Point = Point(left - base.left, top - base.top)

    operator fun plus(base: Point): Point = Point(left + base.left, top + base.top)

    companion object {
        val INVALID = Point(Int.MIN_VALUE, Int.MIN_VALUE)
        val ZERO = Point(0, 0)
    }
}

@Serializable
data class DirectedPoint(
    @SerialName("d")
    val direction: Direction,
    @SerialName("l")
    val left: Int,
    @SerialName("t")
    val top: Int
) {
    val point: Point
        get() = Point(left, top)

    constructor(direction: Direction, point: Point) : this(direction, point.left, point.top)

    operator fun plus(base: Point): DirectedPoint =
        copy(left = left + base.left, top = top + base.top)

    enum class Direction {
        HORIZONTAL, VERTICAL
    }
}
