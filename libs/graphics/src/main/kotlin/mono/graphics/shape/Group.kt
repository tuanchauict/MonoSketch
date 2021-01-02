package mono.graphics.shape

import mono.graphics.geo.Point
import mono.list.QuickList

/**
 * A special shape which manages a collection of shapes.
 */
class Group(parentId: Int?) : AbstractShape(parentId = parentId) {
    private val quickList: QuickList<AbstractShape> = QuickList()
    val items: Collection<AbstractShape> = quickList
    val itemCount: Int get() = items.size

    fun add(shape: AbstractShape, position: QuickList.AddPosition = QuickList.AddPosition.Last) {
        if (shape.parentId != null && shape.parentId != parentId) {
            return
        }
        shape.parentId = id

        quickList.add(shape, position)
    }

    fun remove(shape: AbstractShape): AbstractShape? = quickList.remove(shape)

    override fun contains(point: Point): Boolean = quickList.any { it.contains(point) }

    fun move(shape: AbstractShape, moveActionType: QuickList.MoveActionType) =
        quickList.move(shape, moveActionType)
}
