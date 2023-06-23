/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.mouse

import mono.common.MouseCursor
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
    override val mouseCursor: MouseCursor? = null

    override fun execute(
        environment: CommandEnvironment,
        mousePointer: MousePointer
    ): MouseCommand.CommandResultType {
        when (mousePointer) {
            is MousePointer.Drag -> move(environment, mousePointer.boardCoordinate, false)
            is MousePointer.Up -> move(environment, mousePointer.boardCoordinate, true)
            is MousePointer.Down,
            is MousePointer.Click,
            is MousePointer.DoubleClick,
            is MousePointer.Move,
            MousePointer.Idle -> Unit
        }.exhaustive

        return if (mousePointer == MousePointer.Idle) {
            MouseCommand.CommandResultType.DONE
        } else {
            MouseCommand.CommandResultType.WORKING
        }
    }

    private fun move(environment: CommandEnvironment, point: Point, isUpdateConfirmed: Boolean) {
        when (interactionPoint) {
            is LineInteractionPoint.Anchor ->
                moveAnchor(environment, interactionPoint, point, isUpdateConfirmed)

            is LineInteractionPoint.Edge ->
                moveEdge(environment, interactionPoint, point, isUpdateConfirmed)
        }
    }

    private fun moveAnchor(
        environment: CommandEnvironment,
        interactionPoint: LineInteractionPoint.Anchor,
        point: Point,
        isUpdateConfirmed: Boolean
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
                isUpdateConfirmed,
                // TODO: Allow setting this option with keyboard
                justMoveAnchor = false,
                // TODO: If the performance is bad when moving the shape, it's okay to only search
                //  for the candidates when the action is end (mouse up)
                connectableCandidateShapes = environment.shapeSearcher.getShapes(point)
            )
        )
        environment.updateInteractionBounds()
    }

    private fun moveEdge(
        environment: CommandEnvironment,
        interactionPoint: LineInteractionPoint.Edge,
        point: Point,
        isUpdateConfirmed: Boolean
    ) {
        environment.shapeManager.execute(
            MoveLineEdge(
                lineShape,
                interactionPoint.edgeId,
                point,
                isUpdateConfirmed
            )
        )
        environment.updateInteractionBounds()
    }
}
