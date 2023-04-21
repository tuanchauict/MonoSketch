/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.geo

/**
 * A sealed class which indicates mouse event pointer types.
 */
sealed interface MousePointer {
    object Idle : MousePointer

    data class Move(
        val boardCoordinate: Point,
        val pointPx: Point
    ) : MousePointer

    data class Down(
        val boardCoordinate: Point,
        val pointPx: Point,
        val isWithShiftKey: Boolean
    ) : MousePointer

    data class Drag(
        val mouseDownPoint: Point,
        val boardCoordinate: Point,
        val boardCoordinateF: PointF,
        val isWithShiftKey: Boolean
    ) : MousePointer

    data class Up(
        val mouseDownPoint: Point,
        val boardCoordinate: Point,
        val boardCoordinateF: PointF,
        val isWithShiftKey: Boolean
    ) : MousePointer

    data class Click(val boardCoordinate: Point, val isWithShiftKey: Boolean) : MousePointer

    data class DoubleClick(val boardCoordinate: Point) : MousePointer
}
