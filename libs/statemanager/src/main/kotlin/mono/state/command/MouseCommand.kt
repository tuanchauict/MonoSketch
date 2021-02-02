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

    companion object {
        fun getCommand(
            commandEnvironment: CommandEnvironment,
            mousePointer: MousePointer.Down,
            commandType: CommandType
        ): MouseCommand? {
            val shape = commandEnvironment.shapeManager.getShapeWithPoint(
                commandEnvironment.workingParentGroup,
                mousePointer.point
            )

            if (shape != null) {
                TODO("Add move/edit shape command")
            }

            return when (commandType) {
                CommandType.ADD_RECTANGLE -> AddShapeMouseCommand(ShapeFactory.RectangleFactory)
                CommandType.IDLE -> null
            }
        }
    }
}

