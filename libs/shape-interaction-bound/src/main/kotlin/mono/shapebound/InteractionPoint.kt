package mono.shapebound

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.shape.Line

/**
 * A sealed class for defining all possible interaction point types for a shape and common apis.
 *
 * [left] and [top] are the center position of the interaction point, with board-related unit.
 */
sealed class InteractionPoint(val shapeId: Int, val left: Double, val top: Double)

/**
 * A sealed class for defining all possible scale interaction point types for a shape.
 */
sealed class ScaleInteractionPoint(
    shapeId: Int,
    left: Double,
    top: Double
) : InteractionPoint(shapeId, left, top) {
    abstract fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect

    class TopLeft(
        shapeId: Int,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(newPoint.left, newPoint.top, currentBound.right, currentBound.bottom)
    }

    class TopMiddle(
        shapeId: Int,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, newPoint.top, currentBound.right, currentBound.bottom)
    }

    class TopRight(
        shapeId: Int,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, newPoint.top, newPoint.left, currentBound.bottom)
    }

    class MiddleLeft(
        shapeId: Int,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(newPoint.left, currentBound.top, currentBound.right, currentBound.bottom)
    }

    class MiddleRight(
        shapeId: Int,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, currentBound.top, newPoint.left, currentBound.bottom)
    }

    class BottomLeft(
        shapeId: Int,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(newPoint.left, currentBound.top, currentBound.right, newPoint.top)
    }

    class BottomMiddle(
        shapeId: Int,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, currentBound.top, currentBound.right, newPoint.top)
    }

    class BottomRight(
        shapeId: Int,
        left: Double,
        top: Double
    ) : ScaleInteractionPoint(shapeId, left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, currentBound.top, newPoint.left, newPoint.top)
    }
}

/**
 * A sealed class to define all possible interaction points on Line shapes.
 */
sealed class LineInteractionPoint(
    shapeId: Int,
    left: Double,
    top: Double
) : InteractionPoint(shapeId, left, top) {

    class Anchor(
        shapeId: Int,
        val anchor: Line.Anchor,
        left: Double,
        top: Double
    ) : LineInteractionPoint(shapeId, left, top)

    class Edge(
        shapeId: Int,
        val edgeId: Int,
        left: Double,
        top: Double
    ) : LineInteractionPoint(shapeId, left, top)
}
