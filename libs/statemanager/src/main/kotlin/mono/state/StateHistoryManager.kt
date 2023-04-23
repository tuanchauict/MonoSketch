/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state

import mono.common.setTimeout
import mono.environment.Build
import mono.html.canvas.CanvasViewController
import mono.lifecycle.LifecycleOwner
import mono.livedata.combineLiveData
import mono.shape.serialization.SerializableGroup
import mono.shape.shape.RootGroup
import mono.state.command.CommandEnvironment
import mono.store.dao.workspace.WorkspaceDao
import mono.store.dao.workspace.WorkspaceObjectDao
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

    /**
     * Restores and starts observing the state change of the object with [rootId].
     * If [rootId] is empty, the last opened object is used. In case of there is no objects in the
     * database, a new ID is generated with [UUID.generate].
     *
     * If [rootId] is not in the database, a new object will be created and use [rootId] as the id
     * of the root shape.
     */
    fun restoreAndStartObserveStateChange(rootId: String) {
        val objectDao = getObjectDaoById(rootId)

        restoreShapes(objectDao)
        canvasViewController.setOffset(objectDao.offset)

        combineLiveData(
            environment.shapeManager.versionLiveData,
            environment.editingModeLiveData
        ) { versionCode, editingMode ->
            if (!editingMode.isEditing && versionCode != editingMode.skippedVersion) {
                registerBackupShapes(versionCode)
            }
        }

        // This is tricky because the `rootId` is not used but root shape in the shape manager is 
        // used.
        // TODO: Move this into the caller or somewhere else.
        canvasViewController.drawingOffsetPointPxLiveData.observe(lifecycleOwner) {
            workspaceDao.getObject(environment.shapeManager.root.id).offset = it
        }
    }

    /**
     * Gets the [WorkspaceObjectDao] with [rootId].
     * If [rootId] is not empty, the object with [rootId] is returned even if it is not in the
     * database.
     * If [rootId] is empty, the last opened object is used. In case of there is no objects in the
     * database, a new ID is generated with [UUID.generate].
     */
    private fun getObjectDaoById(rootId: String): WorkspaceObjectDao =
        if (rootId.isNotEmpty()) {
            workspaceDao.getObject(rootId)
        } else {
            workspaceDao.getObjects().firstOrNull() ?: workspaceDao.getObject(UUID.generate())
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

    private fun restoreShapes(objectDao: WorkspaceObjectDao) {
        val serializableGroup = objectDao.rootGroup
        val rootGroup = if (serializableGroup != null) {
            RootGroup(serializableGroup)
        } else {
            RootGroup(objectDao.objectId)
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
