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
import mono.shape.connector.ShapeConnectorUseCase
import mono.shape.shape.AbstractShape
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

    private val pointToTargetMap = mutableMapOf<DirectedPoint, AbstractShape?>()

    override fun execute(
        environment: CommandEnvironment,
        mousePointer: MousePointer
    ): MouseCommand.CommandResultType {
        when (mousePointer) {
            is MousePointer.Drag -> move(
                environment,
                mousePointer.boardCoordinate,
                isUpdateConfirmed = false,
                justMoveAnchor = !mousePointer.isWithShiftKey
            )

            is MousePointer.Up -> move(
                environment,
                mousePointer.boardCoordinate,
                isUpdateConfirmed = true,
                justMoveAnchor = !mousePointer.isWithShiftKey
            )

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

    private fun move(
        environment: CommandEnvironment,
        point: Point,
        isUpdateConfirmed: Boolean,
        justMoveAnchor: Boolean
    ) {
        when (interactionPoint) {
            is LineInteractionPoint.Anchor ->
                moveAnchor(environment, interactionPoint, point, isUpdateConfirmed, justMoveAnchor)

            is LineInteractionPoint.Edge ->
                moveEdge(environment, interactionPoint, point, isUpdateConfirmed)
        }
    }

    private fun moveAnchor(
        environment: CommandEnvironment,
        interactionPoint: LineInteractionPoint.Anchor,
        point: Point,
        isUpdateConfirmed: Boolean,
        justMoveAnchor: Boolean
    ) {
        val edgeDirection = environment.getEdgeDirection(point)
        val direction =
            edgeDirection?.normalizedDirection ?: lineShape.getDirection(interactionPoint.anchor)
        val anchorPointUpdate = Line.AnchorPointUpdate(
            interactionPoint.anchor,
            DirectedPoint(direction, point)
        )
        val connectShape = pointToTargetMap.getOrSearch(environment, anchorPointUpdate.point)
        environment.shapeManager.execute(
            MoveLineAnchor(
                lineShape,
                anchorPointUpdate,
                isUpdateConfirmed,
                justMoveAnchor = justMoveAnchor,
                connectShape = connectShape
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

    private fun MutableMap<DirectedPoint, AbstractShape?>.getOrSearch(
        environment: CommandEnvironment,
        point: DirectedPoint
    ): AbstractShape? = getOrPut(point) {
        if (point !in this) {
            ShapeConnectorUseCase.getConnectableShape(
                point,
                environment.getShapes(point.point)
            )
        } else {
            // This point is already in the map, so we don't need to search again.
            null
        }
    }
}
