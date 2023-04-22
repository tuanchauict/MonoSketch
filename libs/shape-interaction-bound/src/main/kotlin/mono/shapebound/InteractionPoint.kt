/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shapebound

import mono.common.MouseCursor
import mono.graphics.geo.PointF
import mono.graphics.geo.Rect
import mono.shape.shape.Line
import kotlin.math.floor

/**
 * A sealed class for defining all possible interaction point types for a shape and common apis.
 *
 * [left] and [top] are the center position of the interaction point, with board-related unit.
 */
sealed class InteractionPoint(
    val shapeId: String,
    val left: Double,
    val top: Double,
    val mouseCursor: MouseCursor
)

/**
 * A sealed class for defining all possible scale interaction point types for a shape.
 */
sealed class ScaleInteractionPoint(
    shapeId: String,
    left: Double,
    top: Double,
    mouseCursor: MouseCursor
) : InteractionPoint(shapeId, left, top, mouseCursor) {
    abstract fun createNewShapeBound(currentBound: Rect, newPoint: PointF): Rect

    class TopLeft(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NWSE) {

        override fun createNewShapeBound(currentBound: Rect, newPoint: PointF): Rect = Rect.byLTRB(
            adjustUpperBound(newPoint.left),
            adjustUpperBound(newPoint.top),
            currentBound.right,
            currentBound.bottom
        )
    }

    class TopMiddle(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NS) {

        override fun createNewShapeBound(currentBound: Rect, newPoint: PointF): Rect = Rect.byLTRB(
            currentBound.left,
            adjustUpperBound(newPoint.top),
            currentBound.right,
            currentBound.bottom
        )
    }

    class TopRight(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NESW) {

        override fun createNewShapeBound(currentBound: Rect, newPoint: PointF): Rect = Rect.byLTRB(
            currentBound.left,
            adjustUpperBound(newPoint.top),
            adjustLowerBound(newPoint.left),
            currentBound.bottom
        )
    }

    class MiddleLeft(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_EW) {

        override fun createNewShapeBound(currentBound: Rect, newPoint: PointF): Rect = Rect.byLTRB(
            adjustUpperBound(newPoint.left),
            currentBound.top,
            currentBound.right,
            currentBound.bottom
        )
    }

    class MiddleRight(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_EW) {

        override fun createNewShapeBound(currentBound: Rect, newPoint: PointF): Rect = Rect.byLTRB(
            currentBound.left,
            currentBound.top,
            adjustLowerBound(newPoint.left),
            currentBound.bottom
        )
    }

    class BottomLeft(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NESW) {

        override fun createNewShapeBound(currentBound: Rect, newPoint: PointF): Rect = Rect.byLTRB(
            adjustUpperBound(newPoint.left),
            currentBound.top,
            currentBound.right,
            adjustLowerBound(newPoint.top)
        )
    }

    class BottomMiddle(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NS) {

        override fun createNewShapeBound(currentBound: Rect, newPoint: PointF): Rect = Rect.byLTRB(
            currentBound.left,
            currentBound.top,
            currentBound.right,
            adjustLowerBound(newPoint.top)
        )
    }

    class BottomRight(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NWSE) {

        override fun createNewShapeBound(currentBound: Rect, newPoint: PointF): Rect {
            return Rect.byLTRB(
                currentBound.left,
                currentBound.top,
                adjustLowerBound(newPoint.left),
                adjustLowerBound(newPoint.top)
            )
        }
    }

    companion object {
        /**
         * Adjust the value of interaction bound to ensure the mouse cursor is located at the
         * interaction point for left and top edges.
         */
        private fun adjustUpperBound(value: Double): Int = floor(value + 0.8).toInt()

        /**
         * Adjust the value of interaction bound to ensure the mouse cursor is located at the
         * interaction point for bottom and right edges.
         */
        private fun adjustLowerBound(value: Double): Int = floor(value - 0.8).toInt()
    }
}

/**
 * A sealed class to define all possible interaction points on Line shapes.
 */
sealed class LineInteractionPoint(
    shapeId: String,
    left: Double,
    top: Double,
    mouseCursor: MouseCursor
) : InteractionPoint(shapeId, left, top, mouseCursor) {

    class Anchor(
        shapeId: String,
        val anchor: Line.Anchor,
        left: Double,
        top: Double
    ) : LineInteractionPoint(shapeId, left, top, MouseCursor.MOVE)

    class Edge(
        shapeId: String,
        val edgeId: Int,
        left: Double,
        top: Double,
        isHorizontal: Boolean
    ) : LineInteractionPoint(
        shapeId,
        left,
        top,
        if (isHorizontal) MouseCursor.RESIZE_ROW else MouseCursor.RESIZE_COL
    )
}
