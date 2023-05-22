/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.actionmanager

/**
 * A controller to execute command trigger from Dev tool's console.
 */
internal class DebugCommandController(private val setOneTimeAction: (OneTimeActionType) -> Unit) {
    fun executeCommand(command: String, vararg params: String) {
        console.log("Command:", command, params)
        val action = NO_PARAMS_ACTIONS[command]
        if (action != null) {
            setOneTimeAction(action)
            return
        }

        console.error("Not support '$command' yet")
    }

    companion object {
        private val NO_PARAMS_ACTIONS = listOf(
            OneTimeActionType.ProjectAction.SaveShapesAs,
            OneTimeActionType.ProjectAction.OpenShapes,
            OneTimeActionType.ProjectAction.ExportSelectedShapes,
            OneTimeActionType.ShowKeyboardShortcuts,
            OneTimeActionType.SelectAllShapes,
            OneTimeActionType.DeselectShapes,
            OneTimeActionType.DeleteSelectedShapes,
            OneTimeActionType.EditSelectedShapes,
            OneTimeActionType.CopyText,
            OneTimeActionType.Undo,
            OneTimeActionType.Redo
        ).associateBy { it::class.simpleName }
    }
}
