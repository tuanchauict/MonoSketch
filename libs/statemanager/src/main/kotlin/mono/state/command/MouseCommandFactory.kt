package mono.state.command

import mono.graphics.geo.MousePointer
import mono.html.toolbar.RetainableActionType
import mono.shape.shape.Line
import mono.shapebound.InteractionPoint
import mono.shapebound.LineInteractionPoint
import mono.shapebound.ScaleInteractionPoint
import mono.state.command.mouse.AddLineMouseCommand
import mono.state.command.mouse.AddShapeMouseCommand
import mono.state.command.mouse.AddTextMouseCommand
import mono.state.command.mouse.LineInteractionMouseCommand
import mono.state.command.mouse.MouseCommand
import mono.state.command.mouse.MoveShapeMouseCommand
import mono.state.command.mouse.ScaleShapeMouseCommand
import mono.state.command.mouse.SelectShapeMouseCommand
import mono.state.command.mouse.ShapeFactory

/**
 * A factory of [MouseCommand].
 */
internal object MouseCommandFactory {
    fun getCommand(
        commandEnvironment: CommandEnvironment,
        mousePointer: MousePointer.Down,
        commandType: RetainableActionType
    ): MouseCommand {
        val interactionCommand = detectInteractionCommand(commandEnvironment, mousePointer)
        if (interactionCommand != null) {
            return interactionCommand
        }

        return when (commandType) {
            RetainableActionType.ADD_RECTANGLE -> AddShapeMouseCommand(ShapeFactory.RectangleFactory)
            RetainableActionType.ADD_TEXT -> AddTextMouseCommand()
            RetainableActionType.ADD_LINE -> AddLineMouseCommand()
            RetainableActionType.IDLE -> SelectShapeMouseCommand()
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
