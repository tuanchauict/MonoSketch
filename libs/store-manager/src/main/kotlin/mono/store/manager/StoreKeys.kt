/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.store.manager

/**
 * An object to gather all store keys to avoid conflict.
 *
 * TODO: Move all store keys which are in use to this object.
 */
object StoreKeys {
    const val DB_VERSION = "DB_VERSION"

    const val SETTINGS = "settings"
    const val THEME_MODE = "theme-mode"

    const val WORKSPACE = "workspace"
    const val LAST_OPEN = "last-open"
    const val OBJECT_NAME = "name"
    const val OBJECT_CONTENT = "content"
    const val OBJECT_OFFSET = "offset"
    const val OBJECT_LAST_MODIFIED = "last-modified"

    fun getPath(parent: String, key: String): String =
        if (parent.isNotEmpty()) "$parent/$key" else key
}
