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
open class Group(
    id: String? = null,
    parentId: String?
) : AbstractShape(id = id, parentId = parentId) {
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

    constructor(
        serializableGroup: SerializableGroup,
        parentId: String?
    ) : this(id = serializableGroup.id, parentId = parentId) {
        for (serializableShape in serializableGroup.shapes) {
            addInternal(toShape(id, serializableShape))
        }
        versionCode = serializableGroup.versionCode
    }

    override fun toSerializableShape(isIdIncluded: Boolean): AbstractSerializableShape =
        SerializableGroup(
            id.takeIf { isIdIncluded },
            versionCode,
            items.map { it.toSerializableShape(isIdIncluded) }
        )

    internal fun add(shape: AbstractShape, position: AddPosition = AddPosition.Last) = update {
        addInternal(shape, position)
    }

    private fun addInternal(
        shape: AbstractShape,
        position: AddPosition = AddPosition.Last
    ): Boolean {
        if (shape.parentId != null && shape.parentId != id) {
            return false
        }
        shape.parentId = id

        quickList.add(shape, position)
        return true
    }

    internal fun remove(shape: AbstractShape) = update {
        quickList.remove(shape) != null
    }

    internal fun changeOrder(shape: AbstractShape, moveActionType: MoveActionType) = update {
        quickList.move(shape, moveActionType)
    }

    override fun toString(): String {
        return "Group($id)"
    }

    companion object {
        fun toShape(parentId: String, serializableShape: AbstractSerializableShape): AbstractShape =
            when (serializableShape) {
                is SerializableRectangle -> Rectangle(serializableShape, parentId = parentId)
                is SerializableText -> Text(serializableShape, parentId = parentId)
                is SerializableLine -> Line(serializableShape, parentId = parentId)
                is SerializableGroup -> Group(serializableShape, parentId = parentId)
            }
    }
}
