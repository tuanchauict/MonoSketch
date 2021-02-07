package mono.state.command

import mono.shape.ShapeManager
import mono.shape.shape.Group
import mono.shapesearcher.ShapeSearcher
import mono.state.SelectedShapeManager

/**
 * An interface defines apis for command to interact with the environment.
 */
internal interface CommandEnvironment {
    val shapeManager: ShapeManager
    val shapeSearcher: ShapeSearcher
    val workingParentGroup: Group
    val selectedShapeManager: SelectedShapeManager
}
