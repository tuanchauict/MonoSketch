/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.mouse

import mono.common.MouseCursor
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.command.ChangeBound
import mono.shape.connector.ShapeConnectorUseCase.getPointInNewBound
import mono.shape.shape.AbstractShape
import mono.shape.shape.Line
import mono.state.command.CommandEnvironment
import mono.state.command.mouse.MouseCommand.CommandResultType

/**
 * A [MouseCommand] for moving selected shapes.
 */
internal class MoveShapeMouseCommand(private val shapes: Set<AbstractShape>) : MouseCommand {
    override val mouseCursor: MouseCursor = MouseCursor.MOVE

    private val initialPositions = shapes.associate { it.id to it.bound.position }

    override fun execute(
        environment: CommandEnvironment,
        mousePointer: MousePointer
    ): CommandResultType {
        val offset = when (mousePointer) {
            is MousePointer.Drag -> mousePointer.boardCoordinate - mousePointer.mouseDownPoint
            is MousePointer.Up -> mousePointer.boardCoordinate - mousePointer.mouseDownPoint
            is MousePointer.Down,
            is MousePointer.Click,
            is MousePointer.DoubleClick,
            is MousePointer.Move,
            MousePointer.Idle -> Point.ZERO
        }

        val isUpdateConfirmed = mousePointer is MousePointer.Up
        for (shape in shapes) {
            val initialPosition = initialPositions[shape.id] ?: continue
            val newPosition = initialPosition + offset
            val newBound = shape.bound.copy(position = newPosition)
            environment.shapeManager.execute(
                ChangeBound(shape, newBound)
            )

            val connectors = environment.shapeManager.connectorManager.getConnectors(shape)
            for (connector in connectors) {
                val line = environment.shapeManager.getShape(connector.lineId) as? Line ?: continue
                val anchorPointUpdate = Line.AnchorPointUpdate(
                    connector.anchor,
                    connector.getPointInNewBound(line.getDirection(connector.anchor), newBound)
                )

                line.moveAnchorPoint(anchorPointUpdate, isUpdateConfirmed)
            }
        }

        environment.updateInteractionBounds()

        val isDone = mousePointer is MousePointer.Up || mousePointer == MousePointer.Idle
        return if (isDone) CommandResultType.DONE else CommandResultType.WORKING
    }
}
