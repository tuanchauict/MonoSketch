package mono.state.command

import mono.graphics.geo.MousePointer
import mono.shape.shape.Line
import mono.shapebound.InteractionPoint
import mono.shapebound.LineInteractionPoint
import mono.shapebound.ScaleInteractionPoint
import mono.state.MainStateManager
import mono.state.command.text.AddTextMouseCommand

/**
 * A strategy interface for mouse interaction command happens on [MainStateManager]
 */
internal interface MouseCommand {
    /**
     * Handles mouse events.
     * Returns true when the action finishes.
     */
    fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean
}

internal object MouseCommandFactory {
    fun getCommand(
        commandEnvironment: CommandEnvironment,
        mousePointer: MousePointer.Down,
        commandType: CommandType
    ): MouseCommand {
        val interactionCommand = detectInteractionCommand(commandEnvironment, mousePointer)
        if (interactionCommand != null) {
            return interactionCommand
        }

        return when (commandType) {
            CommandType.ADD_RECTANGLE -> AddShapeMouseCommand(ShapeFactory.RectangleFactory)
            CommandType.ADD_TEXT -> AddTextMouseCommand()
            CommandType.ADD_LINE -> AddLineMouseCommand()
            CommandType.IDLE -> SelectShapeMouseCommand()
        }
    }

    private fun detectInteractionCommand(
        commandEnvironment: CommandEnvironment,
        mousePointer: MousePointer.Down
    ): MouseCommand? {
        val selectedShapes = commandEnvironment.selectedShapeManager.selectedShapes
        if (selectedShapes.isEmpty()) {
            return null
        }

        val sharpBoundInteractionCommand = createShapeBoundInteractionMouseCommandIfValid(
            commandEnvironment,
            commandEnvironment.getInteractionPoint(mousePointer.pointPx)
        )
        if (sharpBoundInteractionCommand != null) {
            return sharpBoundInteractionCommand
        }

        if (!mousePointer.isWithShiftKey &&
            commandEnvironment.selectedShapeManager.isInSelectionBounds(mousePointer.point)
        ) {
            return MoveShapeMouseCommand(selectedShapes)
        }
        return null
    }

    private fun createShapeBoundInteractionMouseCommandIfValid(
        commandEnvironment: CommandEnvironment,
        interactionPoint: InteractionPoint?
    ): MouseCommand? {
        if (interactionPoint == null) {
            return null
        }
        val shape =
            commandEnvironment.shapeManager.getShape(interactionPoint.shapeId) ?: return null
        return when (interactionPoint) {
            is ScaleInteractionPoint -> ScaleShapeMouseCommand(shape, interactionPoint)
            is LineInteractionPoint ->
                if (shape is Line) LineInteractionMouseCommand(shape, interactionPoint) else null
            null -> null
        }
    }
}
