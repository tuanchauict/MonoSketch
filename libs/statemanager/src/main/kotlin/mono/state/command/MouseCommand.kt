package mono.state.command

import mono.graphics.geo.MousePointer
import mono.shapebound.InteractionPoint
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

        val scaleShapeMouseCommand = createScaleShapeMouseCommandIfValid(
            commandEnvironment,
            commandEnvironment.getInteractionPoint(mousePointer.pointPx)
        )
        if (scaleShapeMouseCommand != null) {
            return scaleShapeMouseCommand
        }

        if (!mousePointer.isWithShiftKey &&
            commandEnvironment.selectedShapeManager.isInSelectionBounds(mousePointer.point)
        ) {
            return MoveShapeMouseCommand(selectedShapes)
        }
        return null
    }

    private fun createScaleShapeMouseCommandIfValid(
        commandEnvironment: CommandEnvironment,
        interactionPoint: InteractionPoint?
    ): ScaleShapeMouseCommand? {
        if (interactionPoint == null) {
            return null
        }
        val shape =
            commandEnvironment.shapeManager.getShape(interactionPoint.shapeId) ?: return null
        val scaleInteractionPoint = interactionPoint as? ScaleInteractionPoint ?: return null
        return ScaleShapeMouseCommand(shape, scaleInteractionPoint)
    }
}
