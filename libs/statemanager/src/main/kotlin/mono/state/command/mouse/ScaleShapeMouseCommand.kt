/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.mouse

import mono.common.MouseCursor
import mono.graphics.geo.MousePointer
import mono.graphics.geo.PointF
import mono.shape.command.ChangeBound
import mono.shape.shape.AbstractShape
import mono.shapebound.ScaleInteractionPoint
import mono.state.command.CommandEnvironment
import mono.state.utils.UpdateConnectorHelper

/**
 * A [MouseCommand] for scaling shape.
 */
internal class ScaleShapeMouseCommand(
    private val shape: AbstractShape,
    private val interactionPoint: ScaleInteractionPoint
) : MouseCommand {
    override val mouseCursor: MouseCursor? = null

    private val initialBound = shape.bound
    override fun execute(
        environment: CommandEnvironment,
        mousePointer: MousePointer
    ): MouseCommand.CommandResultType {
        when (mousePointer) {
            is MousePointer.Drag ->
                scale(environment, mousePointer.boardCoordinateF, isUpdateConfirmed = false)

            is MousePointer.Up ->
                scale(environment, mousePointer.boardCoordinateF, isUpdateConfirmed = true)

            is MousePointer.Down,
            is MousePointer.Click,
            is MousePointer.DoubleClick,
            is MousePointer.Move,
            MousePointer.Idle -> Unit
        }

        return if (mousePointer == MousePointer.Idle) {
            MouseCommand.CommandResultType.DONE
        } else {
            MouseCommand.CommandResultType.WORKING
        }
    }

    private fun scale(environment: CommandEnvironment, pointF: PointF, isUpdateConfirmed: Boolean) {
        val newBound = interactionPoint.createNewShapeBound(initialBound, pointF)
        environment.shapeManager.execute(ChangeBound(shape, newBound))

        UpdateConnectorHelper.updateConnectors(environment, shape, newBound, isUpdateConfirmed)
        environment.updateInteractionBounds()
    }
}
