package mono.state.command

import mono.shape.ShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shapesearcher.ShapeSearcher

/**
 * An interface defines apis for command to interact with the environment.
 */
internal interface CommandEnvironment {
    val shapeManager: ShapeManager
    val shapeSearcher: ShapeSearcher
    val workingParentGroup: Group

    fun setSelectedShapes(vararg shapes: AbstractShape?)
}
