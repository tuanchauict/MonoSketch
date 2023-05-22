/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.store.manager

/**
 * A storage document for a specific path.
 * This helps the storage having structure similar to a directory in the file system.
 */
class StorageDocument private constructor(
    private val path: String,
    private val storeManager: StoreManager
) {

    private fun getFullPath(key: String): String = StoreKeys.getPath(path, key)

    fun childDocument(key: String): StorageDocument = Companion.get(getFullPath(key))

    fun get(key: String, defaultValue: String? = null): String? =
        storeManager.get(getFullPath(key)) ?: defaultValue

    fun set(key: String, value: String) = storeManager.set(getFullPath(key), value)

    fun remove(key: String) = storeManager.remove(getFullPath(key))

    fun setObserver(key: String, observer: StoreObserver) =
        storeManager.setObserver(getFullPath(key), observer)

    fun removeObserver(key: String) = storeManager.removeObserver(getFullPath(key))

    fun getKeys(predicate: (String) -> Boolean): Sequence<String> = storeManager.getKeys(predicate)

    companion object {
        private val documents = mutableMapOf<String, StorageDocument>()
        fun get(path: String): StorageDocument =
            documents.getOrPut(path) { StorageDocument(path, StoreManager.getInstance()) }
    }
}
