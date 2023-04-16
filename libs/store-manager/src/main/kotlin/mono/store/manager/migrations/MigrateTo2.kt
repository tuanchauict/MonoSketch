/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.store.manager.migrations

import mono.store.manager.StoreKeys
import mono.store.manager.StoreKeys.getPath
import mono.store.manager.StoreManager

internal object MigrateTo2 : Migration(2) {
    override fun migrate(storageManager: StoreManager) {
        migrateSettings(storageManager)
        migrateWorkspace(storageManager)
    }

    private fun migrateSettings(storageManager: StoreManager) {
        console.dir("migrate settings")
        // Theme mode
        storageManager.migrateAndRemove(
            oldKey = "local-theme-mode",
            newKey = getPath(StoreKeys.SETTINGS, StoreKeys.THEME_MODE),
            defaultValue = "DARK"
        )
    }

    private fun migrateWorkspace(storageManager: StoreManager) {
        storageManager.migrateAndRemove(
            oldKey = "last-open",
            newKey = getPath(StoreKeys.WORKSPACE, StoreKeys.LAST_OPEN)
        )

        val backupShapeKeyPrefix = "backup-shapes:"
        val shapeIds = storageManager
            .getKeys { it.startsWith(backupShapeKeyPrefix) }
            .map { it.removePrefix(backupShapeKeyPrefix) }
        for (id in shapeIds) {
            val objectKey = getPath(StoreKeys.WORKSPACE, id)

            storageManager.migrateAndRemove(
                oldKey = "$backupShapeKeyPrefix$id",
                newKey = getPath(objectKey, StoreKeys.OBJECT_CONTENT)
            )

            storageManager.migrateAndRemove(
                oldKey = "offset:$id",
                newKey = getPath(objectKey, StoreKeys.OBJECT_OFFSET),
                defaultValue = "0|0"
            )

            storageManager.migrateAndRemove(
                oldKey = "name:$id",
                newKey = getPath(objectKey, StoreKeys.OBJECT_NAME),
                defaultValue = "Undefined"
            )
        }
    }

    private fun StoreManager.migrateAndRemove(
        oldKey: String,
        newKey: String,
        defaultValue: String? = null
    ) {
        val currentContent = get(oldKey) ?: defaultValue
        if (currentContent != null) {
            set(newKey, currentContent)
        }
        remove(oldKey)
    }
}
