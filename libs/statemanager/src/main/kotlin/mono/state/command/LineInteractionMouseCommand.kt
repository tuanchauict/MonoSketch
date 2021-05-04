package mono.state.command

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.command.MoveLineAnchor
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
            is MousePointer.Move -> move(environment, mousePointer.point, false)
            is MousePointer.Up -> move(environment, mousePointer.point, true)
            is MousePointer.Down,
            is MousePointer.Click,
            MousePointer.Idle -> Unit
        }

        return mousePointer == MousePointer.Idle
    }

    private fun move(environment: CommandEnvironment, point: Point, isReducedRequired: Boolean) {
        if (interactionPoint is LineInteractionPoint.Anchor) {
            moveAnchor(environment, interactionPoint, point, isReducedRequired)
        } else {
            moveEdge(environment, point, isReducedRequired)
        }
    }

    private fun moveAnchor(
        environment: CommandEnvironment,
        interactionPoint: LineInteractionPoint.Anchor,
        point: Point,
        isReducedRequired: Boolean
    ) {
        // TODO: Detect direction based on the environment.
        val direction = lineShape.getDirection(interactionPoint.anchor)
        val anchorPointUpdate = Line.AnchorPointUpdate(
            interactionPoint.anchor,
            DirectedPoint(direction, point)
        )
        environment.shapeManager.execute(
            MoveLineAnchor(
                lineShape,
                anchorPointUpdate,
                isReducedRequired
            )
        )
        environment.selectedShapeManager.updateInteractionBound()
    }

    private fun moveEdge(
        environment: CommandEnvironment,
        point: Point,
        isReducedRequired: Boolean
    ) {
        TODO()
    }
}
