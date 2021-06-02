package mono.shape.shape

import mono.graphics.geo.Rect
import mono.shape.list.QuickList
import mono.shape.list.QuickList.AddPosition
import mono.shape.list.QuickList.MoveActionType
import mono.shape.serialization.AbstractSerializableShape
import mono.shape.serialization.SerializableGroup
import mono.shape.serialization.SerializableLine
import mono.shape.serialization.SerializableRectangle
import mono.shape.serialization.SerializableText

/**
 * A special shape which manages a collection of shapes.
 */
class Group(parentId: Int?) : AbstractShape(parentId = parentId) {
    private val quickList: QuickList<AbstractShape> = QuickList()
    val items: Collection<AbstractShape> = quickList
    val itemCount: Int get() = items.size

    override val bound: Rect
        get() {
            // TODO: Use heap or R-tree to improve performance
            if (quickList.isEmpty()) {
                return Rect.ZERO
            }
            val left = quickList.minOf { it.bound.left }
            val right = quickList.maxOf { it.bound.right }
            val top = quickList.minOf { it.bound.top }
            val bottom = quickList.maxOf { it.bound.bottom }
            return Rect.byLTRB(left, top, right, bottom)
        }

    internal constructor(serializableGroup: SerializableGroup, parentId: Int?) : this(parentId) {
        for (serializableShape in serializableGroup.shapes) {
            val shape = when (serializableShape) {
                is SerializableRectangle -> Rectangle(serializableShape, id)
                is SerializableText -> Text(serializableShape, id)
                is SerializableLine -> Line(serializableShape, id)
                is SerializableGroup -> Group(serializableShape, id)
            }
            add(shape)
        }
    }

    override fun toSerializableShape(): AbstractSerializableShape =
        SerializableGroup(items.map { it.toSerializableShape() })

    internal fun add(shape: AbstractShape, position: AddPosition = AddPosition.Last) = update {
        if (shape.parentId != null && shape.parentId != id) {
            return@update false
        }
        shape.parentId = id

        quickList.add(shape, position)
    }

    internal fun remove(shape: AbstractShape) = update {
        quickList.remove(shape) != null
    }

    internal fun move(shape: AbstractShape, moveActionType: MoveActionType) = update {
        quickList.move(shape, moveActionType)
    }

    override fun toString(): String {
        return "Group($id)"
    }
}
