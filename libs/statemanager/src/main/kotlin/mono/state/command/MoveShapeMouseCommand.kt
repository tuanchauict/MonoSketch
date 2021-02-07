package mono.state.command

import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.command.ChangeBound
import mono.shape.shape.AbstractShape

/**
 * A [MouseCommand] for moving selected shapes.
 */
internal class MoveShapeMouseCommand(private val shapes: Set<AbstractShape>) : MouseCommand {
    private val initialPositions = shapes.map { it.id to it.bound.position }.toMap()

    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        val offset = when (mousePointer) {
            is MousePointer.Move -> mousePointer.point - mousePointer.mouseDownPoint
            is MousePointer.Up -> mousePointer.point - mousePointer.mouseDownPoint
            is MousePointer.Down,
            is MousePointer.Click,
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
