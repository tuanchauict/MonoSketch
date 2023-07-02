/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.geo

/**
 * A sealed class which indicates mouse event pointer types.
 */
sealed interface MousePointer {
    val boardCoordinate: Point

    object Idle : MousePointer {
        override val boardCoordinate: Point
            get() = Point.ZERO
    }

    data class Move(
        override val boardCoordinate: Point,
        val pointPx: Point
    ) : MousePointer

    data class Down(
        override val boardCoordinate: Point,
        val pointPx: Point,
        val isWithShiftKey: Boolean
    ) : MousePointer

    data class Drag(
        val mouseDownPoint: Point,
        override val boardCoordinate: Point,
        val boardCoordinateF: PointF,
        val isWithShiftKey: Boolean
    ) : MousePointer

    data class Up(
        val mouseDownPoint: Point,
        override val boardCoordinate: Point,
        val boardCoordinateF: PointF,
        val isWithShiftKey: Boolean
    ) : MousePointer

    data class Click(
        override val boardCoordinate: Point,
        val isWithShiftKey: Boolean
    ) : MousePointer

    data class DoubleClick(override val boardCoordinate: Point) : MousePointer
}
