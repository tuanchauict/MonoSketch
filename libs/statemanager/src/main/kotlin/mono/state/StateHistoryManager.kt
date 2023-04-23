/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state

import mono.common.currentTimeMillis
import mono.common.setTimeout
import mono.environment.Build
import mono.html.canvas.CanvasViewController
import mono.lifecycle.LifecycleOwner
import mono.livedata.combineLiveData
import mono.shape.serialization.SerializableGroup
import mono.shape.shape.RootGroup
import mono.state.command.CommandEnvironment
import mono.store.dao.workspace.WorkspaceDao
import mono.uuid.UUID

/**
 * A class which manages state history of the shapes.
 */
internal class StateHistoryManager(
    private val lifecycleOwner: LifecycleOwner,
    private val environment: CommandEnvironment,
    private val canvasViewController: CanvasViewController,
    private val workspaceDao: WorkspaceDao = WorkspaceDao.instance
) {
    private val historyStack = HistoryStack()

    fun restoreAndStartObserveStateChange(rootId: String) {
        val adjustedRootId = rootId.ifEmpty {
            workspaceDao.lastOpenedObjectId ?: UUID.generate()
        }

        restoreShapes(adjustedRootId)
        canvasViewController.setOffset(workspaceDao.getObject(adjustedRootId).offset)

        workspaceDao.lastOpenedObjectId = adjustedRootId
        workspaceDao.getObject(adjustedRootId).lastOpened = currentTimeMillis()

        combineLiveData(
            environment.shapeManager.versionLiveData,
            environment.editingModeLiveData
        ) { versionCode, editingMode ->
            if (!editingMode.isEditing && versionCode != editingMode.skippedVersion) {
                registerBackupShapes(versionCode)
            }
        }

        canvasViewController.drawingOffsetPointPxLiveData.observe(lifecycleOwner) {
            workspaceDao.getObject(environment.shapeManager.root.id).offset = it
        }
    }

    fun clear() = historyStack.clear()

    fun undo() {
        val history = historyStack.undo() ?: return
        val root = RootGroup(history.serializableGroup)
        environment.replaceRoot(root)
    }

    fun redo() {
        val history = historyStack.redo() ?: return
        val root = RootGroup(history.serializableGroup)
        environment.replaceRoot(root)
    }

    private fun registerBackupShapes(version: Int) {
        setTimeout(300) {
            // Only backup if the shape manager is idle.
            if (environment.shapeManager.versionLiveData.value == version) {
                backupShapes()
            }
        }
    }

    private fun backupShapes() {
        val root = environment.shapeManager.root
        val serializableGroup = root.toSerializableShape(true) as SerializableGroup

        historyStack.pushState(root.versionCode, serializableGroup)

        workspaceDao.getObject(root.id).rootGroup = serializableGroup
    }

    private fun restoreShapes(rootId: String = "") {
        val serializableGroup = workspaceDao.getObject(rootId).rootGroup
        val rootGroup = if (serializableGroup != null) {
            RootGroup(serializableGroup)
        } else {
            RootGroup(rootId)
        }
        environment.replaceRoot(rootGroup)
    }

    private class HistoryStack {
        private val undoStack = mutableListOf<History>()
        private val redoStack = mutableListOf<History>()

        fun pushState(version: Int, state: SerializableGroup) {
            if (version == undoStack.lastOrNull()?.versionCode) {
                return
            }
            undoStack.add(History(version, state))
            redoStack.clear()
            if (Build.DEBUG) {
                println("Push history stack ${undoStack.map { it.versionCode }}")
            }
        }

        fun clear() {
            undoStack.clear()
            redoStack.clear()
        }

        fun undo(): History? {
            if (undoStack.size <= 1) {
                return null
            }
            val currentState = undoStack.removeLastOrNull()
            if (currentState != null) {
                redoStack.add(currentState)
            }
            return undoStack.lastOrNull()
        }

        fun redo(): History? {
            val currentState = redoStack.removeLastOrNull()
            if (currentState != null) {
                undoStack.add(currentState)
            }
            return currentState
        }
    }

    private class History(val versionCode: Int, val serializableGroup: SerializableGroup)
}
