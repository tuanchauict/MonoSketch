package mono.shape.shape

import mono.graphics.geo.Point
import mono.shape.list.QuickList
import mono.shape.list.QuickList.AddPosition
import mono.shape.list.QuickList.MoveActionType

/**
 * A special shape which manages a collection of shapes.
 */
class Group(parentId: Int?) : AbstractShape(parentId = parentId) {
    private val quickList: QuickList<AbstractShape> = QuickList()
    val items: Collection<AbstractShape> = quickList
    val itemCount: Int get() = items.size

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

    override fun contains(point: Point): Boolean = quickList.any { it.contains(point) }

    internal fun move(shape: AbstractShape, moveActionType: MoveActionType) = update {
        quickList.move(shape, moveActionType)
    }

    override fun toString(): String {
        return "Group($id)"
    }
}
