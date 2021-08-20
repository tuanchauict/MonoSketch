package mono.state

import mono.common.setTimeout
import mono.lifecycle.LifecycleOwner
import mono.shape.serialization.SerializableGroup
import mono.shape.serialization.ShapeSerializationUtil
import mono.shape.shape.Group
import mono.state.command.CommandEnvironment
import mono.store.manager.StoreManager

/**
 * A class which manages state history of the shapes.
 */
internal class StateHistoryManager(
    lifecycleOwner: LifecycleOwner,
    private val environment: CommandEnvironment,
    private val storeManager: StoreManager
) {
    private val historyStack = HistoryStack()

    init {
        restoreShapes()

        environment.shapeManager.versionLiveData.observe(
            lifecycleOwner,
            200,
            ::registerBackupShapes
        )
    }

    fun clear() = historyStack.clear()

    fun undo() {
        val history = historyStack.undo() ?: return
        val root = Group(history.serializableGroup, parentId = null)
        environment.replaceRoot(root)
    }

    fun redo() {
        val history = historyStack.redo() ?: return
        val root = Group(history.serializableGroup, parentId = null)
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

        val jsonRoot = ShapeSerializationUtil.toJson(serializableGroup)
        storeManager.set(BACKUP_SHAPES_KEY, jsonRoot)
    }

    private fun restoreShapes() {
        val rootJson = storeManager.get(BACKUP_SHAPES_KEY) ?: return
        val serializableGroup = ShapeSerializationUtil.fromJson(rootJson) as? SerializableGroup
        if (serializableGroup != null) {
            val rootGroup = Group(serializableGroup, parentId = null)
            environment.replaceRoot(rootGroup)
        } else {
            // Wipe local data with current shapes.
            backupShapes()
        }
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

    companion object {
        private const val BACKUP_SHAPES_KEY = "backup-shapes"
    }
}
