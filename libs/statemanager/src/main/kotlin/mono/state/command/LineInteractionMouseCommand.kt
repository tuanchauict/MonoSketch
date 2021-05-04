package mono.state.command

import mono.graphics.geo.MousePointer
import mono.shape.shape.Line
import mono.shapebound.LineInteractionPoint

/**
 * A [MouseCommand] for moving anchor points or edges of Line shape.
 */
internal class LineInteractionMouseCommand(
    private val lineShape: Line,
    private val interactionPoint: LineInteractionPoint
) : MouseCommand {
    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        when (mousePointer) {
            is MousePointer.Move -> TODO()
            is MousePointer.Up -> TODO()
            is MousePointer.Down,
            is MousePointer.Click,
            MousePointer.Idle -> Unit
        }
        
        return mousePointer == MousePointer.Idle
    }
}
