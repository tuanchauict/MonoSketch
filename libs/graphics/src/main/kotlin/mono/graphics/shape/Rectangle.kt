package mono.graphics.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect

/**
 * A rectangle shape.
 * The content of this shape also includes all vertical/horizontal lines created by [startPoint] and
 * [endPoint].
 */
class Rectangle(
    var rect: Rect,
    parentId: Int? = null
) : AbstractShape(parentId = parentId) {

    constructor(startPoint: Point, endPoint: Point, parentId: Int? = null) : this(
        Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top),
        parentId
    )

    override fun contains(point: Point): Boolean = point in rect
}
