package mono.shapebound

import mono.common.MouseCursor
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.shape.Line

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
    abstract fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect

    class TopLeft(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NWSE) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(newPoint.left, newPoint.top, currentBound.right, currentBound.bottom)
    }

    class TopMiddle(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NS) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, newPoint.top, currentBound.right, currentBound.bottom)
    }

    class TopRight(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NESW) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, newPoint.top, newPoint.left, currentBound.bottom)
    }

    class MiddleLeft(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_EW) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(newPoint.left, currentBound.top, currentBound.right, currentBound.bottom)
    }

    class MiddleRight(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_EW) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, currentBound.top, newPoint.left, currentBound.bottom)
    }

    class BottomLeft(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NESW) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(newPoint.left, currentBound.top, currentBound.right, newPoint.top)
    }

    class BottomMiddle(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NS) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, currentBound.top, currentBound.right, newPoint.top)
    }

    class BottomRight(
        shapeId: String,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top, MouseCursor.RESIZE_NWSE) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, currentBound.top, newPoint.left, newPoint.top)
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
