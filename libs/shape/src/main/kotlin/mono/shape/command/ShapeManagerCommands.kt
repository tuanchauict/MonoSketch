/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.command

import mono.shape.ShapeManager
import mono.shape.list.QuickList
import mono.shape.remove
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.ungroup

/**
 * A [Command] for adding new shape into [ShapeManager].
 */
class AddShape(private val shape: AbstractShape) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(shape.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        parent.add(shape)
        shape.parentId = parent.id
        shapeManager.register(shape)
    }
}

/**
 * A [Command] for removing a shape from [ShapeManager].
 */
class RemoveShape(private val shape: AbstractShape) : Command() {

    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(shape.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        // TODO: This hasn't cleaned group's items. Fix this
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

/**
 * A [Command] for grouping shapes.
 */
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
        val group = Group(parentId = parent.id)
        parent.add(group, QuickList.AddPosition.After(sameParentShapes.last()))
        shapeManager.register(group)

        for (shape in sameParentShapes) {
            parent.remove(shape)
            shape.parentId = group.id
            group.add(shape)
        }
    }
}

/**
 * A [Command] for decomposing a [Group].
 */
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

class ChangeOrder(
    private val shape: AbstractShape,
    private val changeOrderType: ChangeOrderType
) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(shape.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) =
        parent.changeOrder(shape, changeOrderType.orderType)

    enum class ChangeOrderType(internal val orderType: QuickList.MoveActionType) {
        FORWARD(QuickList.MoveActionType.UP),
        BACKWARD(QuickList.MoveActionType.DOWN),
        FRONT(QuickList.MoveActionType.TOP),
        BACK(QuickList.MoveActionType.BOTTOM)
    }
}
