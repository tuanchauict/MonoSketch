/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.onetimeaction

import mono.shape.shape.RootGroup
import mono.state.StateHistoryManager
import mono.state.command.CommandEnvironment

/**
 * A helper class to handle file related one-time actions.
 */
internal class FileRelatedActionsHelper(
    private val environment: CommandEnvironment,
    private val stateHistoryManager: StateHistoryManager
) {
    fun newProject() {
        replaceWorkspace(RootGroup(null)) // passing null to let the ID generated automatically
    }

    private fun replaceWorkspace(rootGroup: RootGroup?) {
        if (rootGroup != null) {
            stateHistoryManager.clear()
            environment.replaceRoot(rootGroup)
        }
    }
}

