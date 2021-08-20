package mono.shape.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.serialization.AbstractSerializableShape
import mono.shape.serialization.SerializableRectangle
import mono.shape.shape.extra.RectangleExtra
import mono.shape.shape.extra.ShapeExtra

/**
 * A rectangle shape.
 */
class Rectangle(
    rect: Rect,
    id: String? = null,
    parentId: String? = null
) : AbstractShape(id, parentId = parentId) {

    override var bound: Rect = rect
        set(value) = update {
            val isUpdated = field != value
            field = value
            isUpdated
        }

    override var extra: RectangleExtra = RectangleExtra.withDefault()
        private set

    /**
     * The content of this shape also includes all vertical/horizontal lines created by [startPoint]
     * and [endPoint].
     */
    constructor(
        startPoint: Point,
        endPoint: Point,
        id: String? = null,
        parentId: String? = null
    ) : this(
        Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top),
        id = id,
        parentId = parentId
    )

    internal constructor(
        serializableRectangle: SerializableRectangle,
        parentId: String? = null
    ) : this(
        serializableRectangle.bound,
        id = serializableRectangle.id,
        parentId = parentId
    ) {
        extra = RectangleExtra(serializableRectangle.extra)
        versionCode = serializableRectangle.versionCode
    }

    override fun toSerializableShape(isIdIncluded: Boolean): AbstractSerializableShape =
        SerializableRectangle(
            id = id.takeIf { isIdIncluded },
            versionCode,
            bound,
            extra = extra.toSerializableExtra()
        )

    override fun setBound(newBound: Rect) {
        bound = newBound
    }

    override fun setExtra(newExtra: ShapeExtra) {
        if (newExtra !is RectangleExtra || newExtra == extra) {
            return
        }
        update {
            extra = newExtra
            true
        }
    }

    override fun isValid(): Boolean = bound.width > 1 && bound.height > 1
}
