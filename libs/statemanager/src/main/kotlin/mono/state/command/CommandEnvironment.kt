package mono.state.command

import mono.shape.ShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group

/**
 * An interface defines apis for command to interact with the environment.
 */
internal interface CommandEnvironment {
    val shapeManager: ShapeManager
    val workingParentGroup: Group

    fun setSelectedShapes(shapes: Set<AbstractShape>)
}
