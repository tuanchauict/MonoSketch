package mono.shape.command

import mono.shape.ShapeManager
import mono.shape.list.QuickList
import mono.shape.remove
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.ungroup

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
