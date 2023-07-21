/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command

import mono.actionmanager.RetainableActionType
import mono.graphics.geo.MousePointer
import mono.shape.selection.SelectedShapeManager.ShapeFocusType.SELECT_MODE_HOVER
import mono.shape.shape.Line
import mono.shapebound.InteractionPoint
import mono.shapebound.LineInteractionPoint
import mono.shapebound.ScaleInteractionPoint
import mono.state.command.mouse.AddLineMouseCommand
import mono.state.command.mouse.AddTextMouseCommand
import mono.state.command.mouse.LineInteractionMouseCommand
import mono.state.command.mouse.MouseCommand
import mono.state.command.mouse.MoveShapeMouseCommand
import mono.state.command.mouse.ScaleShapeMouseCommand
import mono.state.command.mouse.SelectShapeMouseCommand

/**
 * A factory of [MouseCommand].
 */
internal object MouseCommandFactory {
    fun getCommand(
        commandEnvironment: CommandEnvironment,
        mousePointer: MousePointer,
        commandType: RetainableActionType
    ): MouseCommand? = when (mousePointer) {
        is MousePointer.Down -> detectMouseCommandWithMouseDown(
            commandEnvironment,
            mousePointer,
            commandType
        )

        is MousePointer.Click ->
            if (commandType == RetainableActionType.IDLE) SelectShapeMouseCommand else null

        is MousePointer.DoubleClick -> null
        is MousePointer.Move,
        is MousePointer.Drag,
        is MousePointer.Up,
        MousePointer.Idle -> null
    }

    private fun detectMouseCommandWithMouseDown(
        environment: CommandEnvironment,
        mousePointer: MousePointer.Down,
        commandType: RetainableActionType
    ): MouseCommand {
        val interactionCommand = detectInteractionCommand(environment, mousePointer)
        if (interactionCommand != null) {
            return interactionCommand
        }

        return when (commandType) {
            RetainableActionType.ADD_RECTANGLE -> AddTextMouseCommand(false)
            RetainableActionType.ADD_TEXT -> AddTextMouseCommand(true)
            RetainableActionType.ADD_LINE -> AddLineMouseCommand()
            RetainableActionType.IDLE -> SelectShapeMouseCommand
        }
    }

    private fun detectInteractionCommand(
        environment: CommandEnvironment,
        mousePointer: MousePointer.Down
    ): MouseCommand? {
        val focusingShape =
            environment.getFocusingShape()?.takeIf { it.focusType == SELECT_MODE_HOVER }

        if (!mousePointer.isWithShiftKey &&
            focusingShape != null &&
            focusingShape.shape !in environment.getSelectedShapes()
        ) {
            // If focusing shape is not selected and not in multiple selection mode (Shift key),
            // clear all selected shapes and select focusing shape.
            environment.clearSelectedShapes()
            environment.addSelectedShape(focusingShape.shape)
        }
        val selectedShapes = environment.getSelectedShapes()
        if (selectedShapes.isEmpty()) {
            return null
        }

        val sharpBoundInteractionCommand = createShapeBoundInteractionMouseCommandIfValid(
            environment,
            environment.getInteractionPoint(mousePointer.pointPx)
        )
        if (sharpBoundInteractionCommand != null) {
            return sharpBoundInteractionCommand
        }

        if (!mousePointer.isWithShiftKey &&
            environment.isPointInInteractionBounds(mousePointer.boardCoordinate)
        ) {
            val relatedShapes = selectedShapes
                .asSequence()
                .flatMap { environment.shapeManager.shapeConnector.getConnectors(it) }
                .mapNotNull { environment.shapeManager.getShape(it.lineId) }
            return MoveShapeMouseCommand(selectedShapes, relatedShapes)
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
