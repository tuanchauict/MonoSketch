/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.store.manager

import kotlinx.browser.localStorage
import kotlinx.browser.window
import mono.store.manager.migrations.Migration
import org.w3c.dom.StorageEvent
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * A class for managing storage.
 */
class StoreManager private constructor() {
    private val keyToObserverMap: MutableMap<String, StoreObserver> = mutableMapOf()
    private val migrations: Map<Int, Migration> =
        listOf<Migration>(
        ).associateBy { it.targetVersion }

    init {
        migrate()

        window.onstorage = ::onStorageChange
    }

    private fun migrate() {
        // handle migration, default does not have app version
        val currentDbVersion = get(StoreKeys.DB_VERSION)?.toIntOrNull() ?: 1

        for (version in currentDbVersion + 1..DB_VERSION) {
            migrations[version]?.migrate(this)
        }

        set(StoreKeys.DB_VERSION, DB_VERSION.toString())
    }

    fun set(key: String, json: String) {
        localStorage[key] = json
    }

    fun get(key: String): String? = localStorage[key]

    fun remove(key: String) {
        localStorage.removeItem(key)
    }

    fun setObserver(key: String, observer: StoreObserver) {
        if (key in keyToObserverMap) {
            error("$key's observer is set!")
        }
        keyToObserverMap[key] = observer
    }

    fun removeObserver(key: String) {
        keyToObserverMap.remove(key)
    }

    private fun onStorageChange(event: StorageEvent) {
        val key = event.key ?: return
        keyToObserverMap[key]?.onChange(key, event.oldValue, event.newValue)
    }

    /**
     * An interface for observing storage change.
     */
    fun interface StoreObserver {
        fun onChange(key: String, oldValue: String?, newValue: String?)
    }

    companion object {
        private const val DB_VERSION = 1

        private var instance: StoreManager? = null

        fun getInstance(): StoreManager {
            val nonNullInstance = instance ?: StoreManager()
            instance = nonNullInstance
            return nonNullInstance
        }
    }
}
