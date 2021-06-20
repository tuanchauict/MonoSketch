package mono.state.command.mouse

import mono.common.exhaustive
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.command.MoveLineAnchor
import mono.shape.command.MoveLineEdge
import mono.shape.shape.Line
import mono.shapebound.LineInteractionPoint
import mono.state.command.CommandEnvironment

/**
 * A [MouseCommand] for moving anchor points or edges of Line shape.
 */
internal class LineInteractionMouseCommand(
    private val lineShape: Line,
    private val interactionPoint: LineInteractionPoint
) : MouseCommand {
    override val mouseCursor: String? = null

    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        when (mousePointer) {
            is MousePointer.Drag -> move(environment, mousePointer.point, false)
            is MousePointer.Up -> move(environment, mousePointer.point, true)
            is MousePointer.Down,
            is MousePointer.Click,
            is MousePointer.Move,
            MousePointer.Idle -> Unit
        }.exhaustive

        return mousePointer == MousePointer.Idle
    }

    private fun move(environment: CommandEnvironment, point: Point, isReducedRequired: Boolean) {
        when (interactionPoint) {
            is LineInteractionPoint.Anchor ->
                moveAnchor(environment, interactionPoint, point, isReducedRequired)
            is LineInteractionPoint.Edge ->
                moveEdge(environment, interactionPoint, point, isReducedRequired)
        }
    }

    private fun moveAnchor(
        environment: CommandEnvironment,
        interactionPoint: LineInteractionPoint.Anchor,
        point: Point,
        isReducedRequired: Boolean
    ) {
        val edgeDirection = environment.getEdgeDirection(point)
        val direction =
            edgeDirection?.normalizedDirection ?: lineShape.getDirection(interactionPoint.anchor)
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
        environment.updateInteractionBounds()
    }

    private fun moveEdge(
        environment: CommandEnvironment,
        interactionPoint: LineInteractionPoint.Edge,
        point: Point,
        isReducedRequired: Boolean
    ) {
        environment.shapeManager.execute(
            MoveLineEdge(
                lineShape,
                interactionPoint.edgeId,
                point,
                isReducedRequired
            )
        )
        environment.updateInteractionBounds()
    }
}
