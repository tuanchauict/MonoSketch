package mono.shape.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect

/**
 * A simple shape for testing purpose
 */
class MockShape(
    rect: Rect,
    parentId: Int? = null
) : AbstractShape(parentId = parentId) {
    override var bound: Rect = rect
        set(value) = update {
            val isUpdated = field != value
            field = value
            isUpdated
        }

    override fun contains(point: Point): Boolean = bound.contains(point)
}
