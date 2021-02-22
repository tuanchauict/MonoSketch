package mono.state.command

import mono.graphics.geo.MousePointer
import mono.state.MainStateManager

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

        val interactionPosition = commandEnvironment.getInteractionPosition(mousePointer.pointPx)
        if (interactionPosition != null) {
            return ScaleShapeMouseCommand(selectedShapes, interactionPosition)
        }

        if (!mousePointer.isWithShiftKey &&
            commandEnvironment.selectedShapeManager.isInSelectedBound(mousePointer.point)
        ) {
            return MoveShapeMouseCommand(selectedShapes)
        }
        return null
    }
}
