package mono.state.command.mouse

import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.command.ChangeBound
import mono.shape.shape.AbstractShape
import mono.state.command.CommandEnvironment

/**
 * A [MouseCommand] for moving selected shapes.
 */
internal class MoveShapeMouseCommand(private val shapes: Set<AbstractShape>) : MouseCommand {
    private val initialPositions = shapes.associate { it.id to it.bound.position }

    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        val offset = when (mousePointer) {
            is MousePointer.Drag -> mousePointer.point - mousePointer.mouseDownPoint
            is MousePointer.Up -> mousePointer.point - mousePointer.mouseDownPoint
            is MousePointer.Down,
            is MousePointer.Click,
            is MousePointer.Move,
            MousePointer.Idle -> Point.ZERO
        }

        for (shape in shapes) {
            val initialPosition = initialPositions[shape.id] ?: continue
            val newPosition = initialPosition + offset
            val newBound = shape.bound.copy(position = newPosition)
            environment.shapeManager.execute(ChangeBound(shape, newBound))
        }

        environment.selectedShapeManager.updateInteractionBound()

        return mousePointer is MousePointer.Up || mousePointer == MousePointer.Idle
    }
}
