package mono.shape.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect

/**
 * A rectangle shape.
 */
class Rectangle(
    rect: Rect,
    parentId: Int? = null
) : AbstractShape(parentId = parentId) {

    override var bound: Rect = rect
        set(value) = update {
            val isUpdated = field != value
            field = value
            isUpdated
        }

    /**
     * The content of this shape also includes all vertical/horizontal lines created by [startPoint]
     * and [endPoint].
     */
    constructor(startPoint: Point, endPoint: Point, parentId: Int? = null) : this(
        Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top),
        parentId
    )

    override fun setBound(newBound: Rect) {
        bound = newBound
    }

    override fun isValid(): Boolean = bound.width > 1 && bound.height > 1

    override fun contains(point: Point): Boolean = point in bound
}
