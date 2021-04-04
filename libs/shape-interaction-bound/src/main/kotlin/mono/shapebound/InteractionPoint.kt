package mono.shapebound

import mono.graphics.geo.Point
import mono.graphics.geo.Rect

/**
 * A sealed class for defining all possible interaction point types for a shape and common apis.
 *
 * [left] and [top] are the center position of the interaction point, with board-related unit.
 */
sealed class InteractionPoint(val left: Double, val top: Double)

/**
 * A sealed class for defining all possible scale interaction point types for a shape.
 */
sealed class ScaleInteractionPoint(left: Double, top: Double) : InteractionPoint(left, top) {
    abstract fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect

    class TopLeft(left: Double, top: Double) : ScaleInteractionPoint(left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(newPoint.left, newPoint.top, currentBound.right, currentBound.bottom)
    }

    class TopMiddle(left: Double, top: Double) : ScaleInteractionPoint(left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, newPoint.top, currentBound.right, currentBound.bottom)
    }

    class TopRight(left: Double, top: Double) : ScaleInteractionPoint(left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, newPoint.top, newPoint.left, currentBound.bottom)
    }

    class MiddleLeft(left: Double, top: Double) : ScaleInteractionPoint(left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(newPoint.left, currentBound.top, currentBound.right, currentBound.bottom)
    }

    class MiddleRight(left: Double, top: Double) : ScaleInteractionPoint(left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, currentBound.top, newPoint.left, currentBound.bottom)
    }

    class BottomLeft(left: Double, top: Double) : ScaleInteractionPoint(left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(newPoint.left, currentBound.top, currentBound.right, newPoint.top)
    }

    class BottomMiddle(left: Double, top: Double) : ScaleInteractionPoint(left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, currentBound.top, currentBound.right, newPoint.top)
    }

    class BottomRight(left: Double, top: Double) : ScaleInteractionPoint(left, top) {
        override fun createNewShapeBound(currentBound: Rect, newPoint: Point): Rect =
            Rect.byLTRB(currentBound.left, currentBound.top, newPoint.left, newPoint.top)
    }
}
