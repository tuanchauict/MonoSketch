/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.collection

/**
 * A special map that allows querying by both with key and value.
 * Key is going with only 1 value and 1 value can go with multiple keys.
 * This map supports querying with key and querying with value.
 */
internal class TwoWayQuickMap<K : Identifier, V : Identifier> {
    private val keyToValueMap: MutableMap<String, V> = mutableMapOf()
    private val valueToKeysMap: MutableMap<String, MutableMap<String, K>> = mutableMapOf()

    operator fun set(key: K, value: V) {
        if (key.id in keyToValueMap) {
            removeKey(key)
        }
        keyToValueMap[key.id] = value
        valueToKeysMap.getOrPut(value.id) { mutableMapOf() }[key.id] = key
    }

    fun removeKey(key: Identifier) {
        val value = keyToValueMap[key.id] ?: return
        keyToValueMap.remove(key.id)
        val keyMap = valueToKeysMap[value.id]!!
        keyMap.remove(key.id)
        if (keyMap.isEmpty()) {
            valueToKeysMap.remove(value.id)
        }
    }

    /**
     * Removes the [value].
     * All keys associated to [value] will be also removed.
     */
    fun removeValue(value: Identifier) {
        val keyMap = valueToKeysMap[value.id] ?: return
        valueToKeysMap.remove(value.id)

        for (keyId in keyMap.keys) {
            keyToValueMap.remove(keyId)
        }
    }

    fun getKeys(value: Identifier): Collection<K> = valueToKeysMap[value.id]?.values.orEmpty()

    operator fun get(key: Identifier): V? = keyToValueMap[key.id]

    fun getKey(keyId: Identifier): K? {
        val value = keyToValueMap[keyId.id] ?: return null
        return valueToKeysMap[value.id]?.get(keyId.id)
    }
}
