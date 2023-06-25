/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.mouse

import mono.common.MouseCursor
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.command.ChangeBound
import mono.shape.shape.AbstractShape
import mono.shape.shape.Line
import mono.state.command.CommandEnvironment
import mono.state.command.mouse.MouseCommand.CommandResultType
import mono.state.utils.UpdateConnectorHelper

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

        val affectedLines = mutableSetOf<String>()
        val (lines, notLineShapes) = shapes.partition { it is Line }

        for (shape in notLineShapes) {
            val initialPosition = initialPositions[shape.id] ?: continue
            val newPosition = initialPosition + offset
            val newBound = shape.bound.copy(position = newPosition)

            environment.shapeManager.execute(ChangeBound(shape, newBound))
            affectedLines += UpdateConnectorHelper.updateConnectors(
                environment,
                shape,
                newBound,
                isUpdateConfirmed
            )
        }

        for (line in lines) {
            if (line.id in affectedLines) {
                // If a line is updated by connectors, ignore it from updating as individual shape
                continue
            }
            val initialPosition = initialPositions[line.id] ?: continue
            val newPosition = initialPosition + offset
            val newBound = line.bound.copy(position = newPosition)
            environment.shapeManager.execute(ChangeBound(line, newBound))
        }

        environment.updateInteractionBounds()

        val isDone = mousePointer is MousePointer.Up || mousePointer == MousePointer.Idle
        return if (isDone) CommandResultType.DONE else CommandResultType.WORKING
    }
}
