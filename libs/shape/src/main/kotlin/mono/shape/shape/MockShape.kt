package mono.shape.shape

import mono.graphics.geo.Rect
import mono.shape.serialization.AbstractSerializableShape

/**
 * A simple shape for testing purpose
 */
class MockShape(
    rect: Rect,
    parentId: Int? = null
) : AbstractShape(parentId = parentId) {

    override fun toSerializableShape(): AbstractSerializableShape {
        TODO("Not yet implemented")
    }

    override var bound: Rect = rect
        set(value) = update {
            val isUpdated = field != value
            field = value
            isUpdated
        }
}
