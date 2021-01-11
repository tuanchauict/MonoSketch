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
        getGroup(shape.parentId)?.recursiveUpdate { parent ->
            parent.add(shape)
            shape.parentId = parent.id
            allShapeMap[shape.id] = shape
        }
    }

    fun remove(shape: AbstractShape) {
        getGroup(shape.parentId)?.recursiveUpdate { parent ->
            parent.remove(shape)
            allShapeMap.remove(shape.id)
            if (parent != root) {
                when (parent.itemCount) {
                    1 -> ungroup(parent)
                    0 -> remove(parent)
                }
            }
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
        getGroup(parentId)?.recursiveUpdate { parent ->
            val group = Group(parentId)
            parent.add(group, QuickList.AddPosition.After(sameParentShapes.last()))

            for (shape in sameParentShapes) {
                parent.remove(shape)
                shape.parentId = group.id
                group.add(shape)
            }
        }
    }

    fun ungroup(group: Group) {
        getGroup(group.parentId)?.recursiveUpdate { parent ->
            for (shape in group.items.reversed()) {
                group.remove(shape)
                shape.parentId = parent.parentId
                parent.add(shape, QuickList.AddPosition.After(group))
            }
            remove(group)
        }
    }

    /**
     * Applies update by [action] to the current group.
     * If the current group's version is changed, all of its parents' version will be updated too.
     */
    private fun Group.recursiveUpdate(action: (Group) -> Unit) {
        val oldVersion = version

        action(this)

        if (oldVersion == version) {
            return
        }
        for (parent in getAllAncestors()) {
            parent.update { true }
        }
    }

    private fun Group.getAllAncestors(): List<Group> {
        val result = mutableListOf<Group>()
        var parent = allShapeMap[parentId] as? Group
        while (parent != null) {
            result.add(parent)
            parent = allShapeMap[parent.parentId] as? Group
        }
        return result
    }
}
