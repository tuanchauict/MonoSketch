package mono.graphics.shape

import mono.graphics.geo.Point
import mono.list.QuickList
import mono.list.QuickList.AddPosition
import mono.list.QuickList.MoveActionType

/**
 * A special shape which manages a collection of shapes.
 */
class Group(parentId: Int?) : AbstractShape(parentId = parentId) {
    private val quickList: QuickList<AbstractShape> = QuickList()
    val items: Collection<AbstractShape> = quickList
    val itemCount: Int get() = items.size

    fun add(shape: AbstractShape, position: AddPosition = AddPosition.Last) {
        if (shape.parentId != null && shape.parentId != id) {
            return
        }
        shape.parentId = id

        quickList.add(shape, position)
    }

    fun remove(shape: AbstractShape): AbstractShape? = quickList.remove(shape)

    override fun contains(point: Point): Boolean = quickList.any { it.contains(point) }

    fun move(shape: AbstractShape, moveActionType: MoveActionType) =
        quickList.move(shape, moveActionType)

    override fun toString(): String {
        return "Group($id)"
    }
}
