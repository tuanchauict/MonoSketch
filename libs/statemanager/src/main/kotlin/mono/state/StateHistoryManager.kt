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
    init {
        restoreShapes()

        environment.shapeManager.versionLiveData.observe(
            lifecycleOwner,
            500,
            ::registerBackupShapes
        )
    }

    private fun registerBackupShapes(version: Int) {
        setTimeout(500) {
            // Only backup if the shape manager is idle.
            if (environment.shapeManager.versionLiveData.value == version) {
                backupShapes()
            }
        }
    }

    private fun backupShapes() {
        val serializableGroup = environment.shapeManager.root.toSerializableShape(true)
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

    companion object {
        private const val BACKUP_SHAPES_KEY = "backup-shapes"
    }
}
