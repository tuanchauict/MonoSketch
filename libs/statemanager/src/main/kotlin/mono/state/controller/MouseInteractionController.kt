/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.controller

import mono.actionmanager.ActionManager
import mono.actionmanager.OneTimeActionType
import mono.actionmanager.RetainableActionType
import mono.common.post
import mono.graphics.geo.MousePointer
import mono.shape.selection.SelectedShapeManager
import mono.state.command.CommandEnvironment
import mono.state.command.MouseCommandFactory
import mono.state.command.mouse.HoverShapeManager
import mono.state.command.mouse.MouseCommand

/**
 * A controller class specific for mouse interaction.
 */
internal class MouseInteractionController(
    private val environment: CommandEnvironment,
    private val actionManager: ActionManager,
    private val requestRedraw: () -> Unit
) {
    val currentRetainableActionType: RetainableActionType
        get() = actionManager.retainableActionLiveData.value

    var currentMouseCommand: MouseCommand? = null
        private set

    private val lineConnectHoverShapeManager = HoverShapeManager.forLineConnectHover()
    private val hoverShapeManager = HoverShapeManager.forHoverShape()

    fun reset() {
        lineConnectHoverShapeManager.resetCache()
        hoverShapeManager.resetCache()
    }

    fun onMouseEvent(mousePointer: MousePointer) {
        if (mousePointer is MousePointer.Down || mousePointer is MousePointer.Up) {
            reset()
        }
        if (mousePointer is MousePointer.DoubleClick) {
            val targetedShape =
                environment.getSelectedShapes()
                    .firstOrNull { it.contains(mousePointer.boardCoordinate) }
            actionManager.setOneTimeAction(OneTimeActionType.EditSelectedShape(targetedShape))
            return
        }

        if (currentMouseCommand == null) {
            detectHoverShape(mousePointer)
        }

        val mouseCommand =
            MouseCommandFactory.getCommand(environment, mousePointer, currentRetainableActionType)
                ?: currentMouseCommand
                ?: return

        currentMouseCommand = mouseCommand

        environment.enterEditingMode()
        val commandResultType = mouseCommand.execute(environment, mousePointer)

        if (commandResultType == MouseCommand.CommandResultType.DONE) {
            environment.exitEditingMode(true)
        }

        if (commandResultType == MouseCommand.CommandResultType.DONE ||
            commandResultType == MouseCommand.CommandResultType.WORKING_PHASE2
        ) {
            lineConnectHoverShapeManager.resetCache()
            currentMouseCommand = null
            requestRedraw()
            // Avoid click when adding shape cause shape selection command
            post { actionManager.setRetainableAction(RetainableActionType.IDLE) }
        }
    }

    private fun detectHoverShape(mousePointer: MousePointer) {
        val (hoverShape, type) = when (currentRetainableActionType) {
            RetainableActionType.ADD_LINE -> {
                val hoverShape = lineConnectHoverShapeManager.getHoverShape(
                    environment,
                    mousePointer.boardCoordinate
                )
                hoverShape to SelectedShapeManager.ShapeFocusType.LINE_CONNECTING
            }

            RetainableActionType.IDLE -> {
                val hoverShape = hoverShapeManager.getHoverShape(
                    environment,
                    mousePointer.boardCoordinate
                )
                hoverShape to SelectedShapeManager.ShapeFocusType.SELECT_MODE_HOVER
            }

            RetainableActionType.ADD_RECTANGLE,
            RetainableActionType.ADD_TEXT -> {
                null
            }
        } ?: return
        environment.setFocusingShape(hoverShape, type)
        requestRedraw()
    }
}
