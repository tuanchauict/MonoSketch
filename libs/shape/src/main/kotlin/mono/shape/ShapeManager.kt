package mono.shape

import mono.shape.list.QuickList
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group

/**
 * A model class which contains all shapes of the app and also defines all shape handling logics.
 */
class ShapeManager {
    // Visible for testing only
    internal val root: Group = Group(null)
    private val allShapeMap: MutableMap<Int, AbstractShape> = mutableMapOf(root.id to root)

    private fun getGroup(shapeId: Int?): Group? =
        if (shapeId == null) root else allShapeMap[shapeId] as? Group

    fun add(shape: AbstractShape) {
        val group = getGroup(shape.parentId) ?: return
        group.add(shape)
        shape.parentId = group.id
        allShapeMap[shape.id] = shape
    }

    fun remove(shape: AbstractShape) {
        val parent = getGroup(shape.parentId) ?: return
        parent.remove(shape)
        allShapeMap.remove(shape.id)
        println("parent $parent ${parent.itemCount} ${parent.parentId} root $root\n")
        if (parent == root) {
            return
        }
        when (parent.itemCount) {
            1 -> ungroup(parent)
            0 -> remove(parent)
        }
    }

    fun group(sameParentShapes: List<AbstractShape>) {
        if (sameParentShapes.size < 2) {
            // No group 1 or 0 items
            return
        }
        val parentId = sameParentShapes.first().parentId
        if (sameParentShapes.any { it.parentId != parentId }) {
            // No group cross group items
            return
        }
        val parent = getGroup(parentId) ?: return

        val group = Group(parentId)
        parent.add(group, QuickList.AddPosition.After(sameParentShapes.last()))

        for (shape in sameParentShapes) {
            parent.remove(shape)
            shape.parentId = group.id
            group.add(shape)
        }
    }

    fun ungroup(group: Group) {
        val parent = getGroup(group.parentId) ?: return

        for (shape in group.items.reversed()) {
            group.remove(shape)
            shape.parentId = parent.parentId
            parent.add(shape, QuickList.AddPosition.After(group))
        }
        remove(group)
    }
}
