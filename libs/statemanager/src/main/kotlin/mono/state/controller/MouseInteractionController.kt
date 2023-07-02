/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.controller

import mono.actionmanager.ActionManager
import mono.actionmanager.OneTimeActionType
import mono.actionmanager.RetainableActionType
import mono.common.post
import mono.graphics.geo.MousePointer
import mono.state.command.CommandEnvironment
import mono.state.command.MouseCommandFactory
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

    fun onMouseEvent(mousePointer: MousePointer) {
        if (mousePointer is MousePointer.DoubleClick) {
            val targetedShape =
                environment.getSelectedShapes()
                    .firstOrNull { it.contains(mousePointer.boardCoordinate) }
            actionManager.setOneTimeAction(OneTimeActionType.EditSelectedShape(targetedShape))
            return
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
            currentMouseCommand = null
            requestRedraw()
            // Avoid click when adding shape cause shape selection command
            post { actionManager.setRetainableAction(RetainableActionType.IDLE) }
        }
    }
}