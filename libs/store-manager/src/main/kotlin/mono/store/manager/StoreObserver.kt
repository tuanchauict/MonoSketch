/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.store.manager

/**
 * An interface for observing storage change.
 */
fun interface StoreObserver {
    fun onChange(key: String, oldValue: String?, newValue: String?)
}
