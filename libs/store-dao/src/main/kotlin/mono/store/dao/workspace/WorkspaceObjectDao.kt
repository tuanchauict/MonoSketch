/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.store.dao.workspace

import mono.common.currentTimeMillis
import mono.graphics.geo.Point
import mono.shape.serialization.SerializableGroup
import mono.shape.serialization.ShapeSerializationUtil
import mono.store.manager.StorageDocument
import mono.store.manager.StoreKeys.OBJECT_CONTENT
import mono.store.manager.StoreKeys.OBJECT_LAST_MODIFIED
import mono.store.manager.StoreKeys.OBJECT_LAST_OPENED
import mono.store.manager.StoreKeys.OBJECT_NAME
import mono.store.manager.StoreKeys.OBJECT_OFFSET

/**
 * A dao for an object (aka project or file) in the workspace.
 */
class WorkspaceObjectDao internal constructor(
    val objectId: String,
    workspaceDocument: StorageDocument
) {

    private val objectDocument: StorageDocument = workspaceDocument.childDocument(objectId)

    var offset: Point
        get() {
            val offsetString = objectDocument.get(OBJECT_OFFSET)
            val (leftString, topString) = offsetString?.split('|')?.takeIf { it.size == 2 }
                ?: return Point.ZERO
            val left = leftString.toIntOrNull() ?: return Point.ZERO
            val top = topString.toIntOrNull() ?: return Point.ZERO
            return Point(left, top)
        }
        set(value) = objectDocument.set(OBJECT_OFFSET, "${value.left}|${value.top}")

    var rootGroup: SerializableGroup?
        get() {
            val json = objectDocument.get(OBJECT_CONTENT) ?: return null
            return ShapeSerializationUtil.fromJson(json) as? SerializableGroup
        }
        set(value) {
            if (value != null) {
                val json = ShapeSerializationUtil.toJson(value)
                objectDocument.set(OBJECT_CONTENT, json)
            }
            lastModifiedTimestampMillis = currentTimeMillis()
        }

    var name: String
        get() = objectDocument.get(OBJECT_NAME) ?: DEFAULT_NAME
        set(value) {
            objectDocument.set(OBJECT_NAME, value)
            lastModifiedTimestampMillis = currentTimeMillis()
        }

    var lastModifiedTimestampMillis: Double
        get() {
            val lastModifiedString = objectDocument.get(OBJECT_LAST_MODIFIED)
            return lastModifiedString?.toDoubleOrNull() ?: currentTimeMillis()
        }
        private set(value) = objectDocument.set(OBJECT_LAST_MODIFIED, value.toString())

    var lastOpened: Double
        get() {
            val lastOpenedString = objectDocument.get(OBJECT_LAST_OPENED)
            return lastOpenedString?.toDoubleOrNull() ?: currentTimeMillis()
        }
        private set(value) = objectDocument.set(OBJECT_LAST_OPENED, value.toString())

    fun updateLastOpened() {
        lastOpened = currentTimeMillis()
    }

    fun removeSelf() {
        with(objectDocument) {
            remove(OBJECT_OFFSET)
            remove(OBJECT_CONTENT)
            remove(OBJECT_NAME)
            remove(OBJECT_LAST_MODIFIED)
        }
    }

    companion object {
        const val DEFAULT_NAME = "Undefined"
    }
}
