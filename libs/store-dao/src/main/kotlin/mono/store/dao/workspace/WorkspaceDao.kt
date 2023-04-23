/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.store.dao.workspace

import mono.store.manager.StorageDocument
import mono.store.manager.StoreKeys.LAST_OPEN
import mono.store.manager.StoreKeys.WORKSPACE

/**
 * A dao for workspace
 */
class WorkspaceDao private constructor(
    private val workspaceDocument: StorageDocument
) {
    var lastOpenedObjectId: String?
        get() = workspaceDocument.get(LAST_OPEN)
        set(value) {
            if (value != null) {
                workspaceDocument.set(LAST_OPEN, value)
            }
        }

    companion object {
        val instance: WorkspaceDao by lazy { WorkspaceDao(StorageDocument.get(WORKSPACE)) }
    }
}
