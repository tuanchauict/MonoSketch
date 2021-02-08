package mono.state.command

import mono.graphics.geo.EdgeRelatedPosition
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Rect
import mono.shape.shape.AbstractShape

/**
 * A [MouseCommand] for scaling shapes.
 * [shapes] must have at least 1 item.
 */
internal class ScaleShapeMouseCommand(
    private val shapes: Collection<AbstractShape>,
    private val interactionPosition: EdgeRelatedPosition
) : MouseCommand {
    private val initialSizes = shapes.map { it.id to it.bound.size }
    private val initialBound = Rect.boundOf(shapes.asSequence().map { it.bound })
    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        // TODO: Implement this method

        return mousePointer == MousePointer.Idle
    }
}
