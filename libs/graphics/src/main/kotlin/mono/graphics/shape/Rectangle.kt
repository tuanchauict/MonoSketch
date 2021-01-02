package mono.graphics.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect

/**
 * A rectangle shape.
 * The content of this shape also includes all vertical/horizontal lines created by [startPoint] and
 * [endPoint].
 */
class Rectangle(
    startPoint: Point,
    endPoint: Point,
    parentId: Int? = null
) : AbstractShape(parentId = parentId) {
    private var rect: Rect =
        Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top)

    override fun contains(point: Point): Boolean = point in rect
}
