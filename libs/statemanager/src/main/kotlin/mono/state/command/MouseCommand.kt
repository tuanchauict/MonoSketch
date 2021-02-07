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
    ): MouseCommand = when (commandType) {
        CommandType.ADD_RECTANGLE -> AddShapeMouseCommand(ShapeFactory.RectangleFactory)
        CommandType.IDLE -> SelectShapeMouseCommand()
    }
}
