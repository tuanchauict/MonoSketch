package mono.state

import mono.common.setTimeout
import mono.environment.Build
import mono.graphics.geo.Point
import mono.html.canvas.CanvasViewController
import mono.lifecycle.LifecycleOwner
import mono.livedata.combineLiveData
import mono.shape.serialization.SerializableGroup
import mono.shape.serialization.ShapeSerializationUtil
import mono.shape.shape.RootGroup
import mono.state.command.CommandEnvironment
import mono.store.manager.StoreManager
import mono.uuid.UUID

/**
 * A class which manages state history of the shapes.
 */
internal class StateHistoryManager(
    private val lifecycleOwner: LifecycleOwner,
    private val environment: CommandEnvironment,
    private val storeManager: StoreManager,
    private val canvasViewController: CanvasViewController
) {
    private val historyStack = HistoryStack()

    init {
        migrationTo1_1_0()
    }

    fun restoreAndStartObserveStateChange(rootId: String) {
        val adjustedRootId = rootId.ifEmpty {
            storeManager.get(BACKUP_LAST_OPEN_PROJECT_KEY) ?: UUID.generate()
        }

        restoreShapes(adjustedRootId)
        restoreOffset(adjustedRootId)

        storeManager.set(BACKUP_LAST_OPEN_PROJECT_KEY, adjustedRootId)

        combineLiveData(
            environment.shapeManager.versionLiveData, environment.editingModeLiveData
        ) { versionCode, editingMode ->
            if (!editingMode.isEditing && versionCode != editingMode.skippedVersion) {
                registerBackupShapes(versionCode)
            }
        }

        canvasViewController.drawingOffsetPointPxLiveData.observe(lifecycleOwner) {
            val backupKey = getBackupKey(BACKUP_OFFSET_KEY, environment.shapeManager.root.id)
            storeManager.set(backupKey, "${it.left}|${it.top}")
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

        val backupKey = getBackupKey(BACKUP_SHAPES_KEY, root.id)
        val jsonRoot = ShapeSerializationUtil.toJson(serializableGroup)
        storeManager.set(backupKey, jsonRoot)
    }

    private fun restoreShapes(rootId: String = "") {
        val backupKey = getBackupKey(BACKUP_SHAPES_KEY, rootId)
        val rootJson = storeManager.get(backupKey)
        val serializableGroup =
            rootJson?.let(ShapeSerializationUtil::fromJson) as? SerializableGroup
        val rootGroup = if (serializableGroup != null) {
            RootGroup(serializableGroup)
        } else {
            RootGroup(rootId)
        }
        environment.replaceRoot(rootGroup)
    }

    private fun restoreOffset(rootId: String = "") {
        val backupKey = getBackupKey(BACKUP_OFFSET_KEY, rootId)
        val storedOffsetString = storeManager.get(backupKey) ?: return
        val (leftString, topString) = storedOffsetString.split('|').takeIf { it.size == 2 }
            ?: return
        val left = leftString.toIntOrNull() ?: return
        val top = topString.toIntOrNull() ?: return
        val offset = Point(left, top)
        canvasViewController.setOffset(offset)
    }

    @Suppress("FunctionName")
    private fun migrationTo1_1_0() {
        val defaultBackupShapeJson = storeManager.get(BACKUP_SHAPES_KEY)
        if (defaultBackupShapeJson.isNullOrEmpty()) {
            return
        }
        val serializableGroup =
            ShapeSerializationUtil.fromJson(defaultBackupShapeJson)
        val id = serializableGroup?.id ?: return
        storeManager.set(getBackupKey(BACKUP_SHAPES_KEY, id), defaultBackupShapeJson)

        val defaultBackupOffsetValue = storeManager.get(BACKUP_OFFSET_KEY)
        if (defaultBackupOffsetValue != null) {
            storeManager.set(getBackupKey(BACKUP_OFFSET_KEY, id), defaultBackupOffsetValue)
        }

        storeManager.remove(BACKUP_SHAPES_KEY)
        storeManager.remove(BACKUP_OFFSET_KEY)

        storeManager.set(BACKUP_LAST_OPEN_PROJECT_KEY, id)
    }

    private fun getBackupKey(prefix: String, rootId: String): String = "$prefix:$rootId"

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

    companion object {
        private const val BACKUP_SHAPES_KEY = "backup-shapes"
        private const val BACKUP_OFFSET_KEY = "offset"
        private const val BACKUP_LAST_OPEN_PROJECT_KEY = "last-open"
    }
}
