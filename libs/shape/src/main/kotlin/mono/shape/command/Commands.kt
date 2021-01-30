package mono.shape.command

import mono.shape.ShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group

sealed class Command {
    internal open fun getDirectAffectedGroup(shapeManager: ShapeManager): Group? = null

    internal abstract fun execute(shapeManager: ShapeManager, parent: Group)
}

class AddShape(val shape: AbstractShape) : Command() {
    override fun getDirectAffectedGroup(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(shape.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        parent.add(shape)
        shape.parentId = parent.id
        shapeManager.register(shape)
    }
}
