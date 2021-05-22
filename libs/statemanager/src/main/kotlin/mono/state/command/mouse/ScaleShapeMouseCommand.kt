package mono.state.command.mouse

import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.command.ChangeBound
import mono.shape.shape.AbstractShape
import mono.shapebound.ScaleInteractionPoint
import mono.state.command.CommandEnvironment

/**
 * A [MouseCommand] for scaling shape.
 */
internal class ScaleShapeMouseCommand(
    private val shape: AbstractShape,
    private val interactionPoint: ScaleInteractionPoint
) : MouseCommand {
    private val initialBound = shape.bound
    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        when (mousePointer) {
            is MousePointer.Move -> scale(environment, mousePointer.point)
            is MousePointer.Up -> scale(environment, mousePointer.point)
            is MousePointer.Down,
            is MousePointer.Click,
            MousePointer.Idle -> Unit
        }

        return mousePointer == MousePointer.Idle
    }

    private fun scale(environment: CommandEnvironment, point: Point) {
        val newBound = interactionPoint.createNewShapeBound(initialBound, point)
        environment.shapeManager.execute(ChangeBound(shape, newBound))

        environment.selectedShapeManager.updateInteractionBound()
    }
}
