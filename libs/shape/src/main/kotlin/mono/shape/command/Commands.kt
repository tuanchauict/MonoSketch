package mono.shape.command

import mono.graphics.geo.Rect
import mono.shape.ShapeManager
import mono.shape.list.QuickList
import mono.shape.remove
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.ungroup

/**
 * A sealed class which defines common apis for a command. A command must determine direct affected
 * parent group via [getDirectAffectedParent]. If [getDirectAffectedParent] returns null, the
 * command won't be executed.
 */
sealed class Command {
    internal abstract fun getDirectAffectedParent(shapeManager: ShapeManager): Group?

    internal abstract fun execute(shapeManager: ShapeManager, parent: Group)
}

class AddShape(private val shape: AbstractShape) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(shape.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        parent.add(shape)
        shape.parentId = parent.id
        shapeManager.register(shape)
    }
}

class RemoveShape(private val shape: AbstractShape) : Command() {

    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(shape.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        parent.remove(shape)
        shapeManager.unregister(shape)

        if (parent == shapeManager.root) {
            return
        }
        when (parent.itemCount) {
            1 -> shapeManager.ungroup(parent)
            0 -> shapeManager.remove(parent)
        }
    }
}

class GroupShapes(private val sameParentShapes: List<AbstractShape>) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? {
        if (sameParentShapes.size < 2) {
            // No group 1 or 0 items
            return null
        }
        val parentId = sameParentShapes.first().parentId
        if (sameParentShapes.any { it.parentId != parentId }) {
            // No group cross group items
            return null
        }
        return shapeManager.getGroup(parentId)
    }

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val group = Group(parent.id)
        parent.add(group, QuickList.AddPosition.After(sameParentShapes.last()))
        shapeManager.register(group)

        for (shape in sameParentShapes) {
            parent.remove(shape)
            shape.parentId = group.id
            group.add(shape)
        }
    }
}

class Ungroup(private val group: Group) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(group.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        for (shape in group.items.reversed()) {
            group.remove(shape)
            shape.parentId = null
            parent.add(shape, QuickList.AddPosition.After(group))
        }
        shapeManager.remove(group)
    }
}

class ChangeBound(private val target: AbstractShape, private val newBound: Rect) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.version
        target.setBound(newBound)
        if (currentVersion != target.version) {
            parent.update { true }
        }
    }
}
