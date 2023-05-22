/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.store.manager

/**
 * An object to gather all store keys to avoid conflict.
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
    const val OBJECT_LAST_OPENED = "last-opened"

    const val PATH_SEPARATOR = "/"

    fun getPath(parent: String, key: String): String =
        if (parent.isNotEmpty()) "$parent$PATH_SEPARATOR$key" else key
}
