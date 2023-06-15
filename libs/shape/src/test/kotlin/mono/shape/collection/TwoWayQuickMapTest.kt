/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.collection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * A test for [TwoWayQuickMap]
 */
class TwoWayQuickMapTest {
    private val target = TwoWayQuickMap<Key, Value>()

    @Test
    fun testAdd() {
        target[K1] = V1
        assertEquals(V1, target[Key("k1")])
        assertEquals(setOf(K1), target.getKeys(Value("v1")).toSet())
        assertNull(target[K2])
        assertEquals(emptySet(), target.getKeys(Value("v2")).toSet())

        target[K2] = V1
        assertEquals(V1, target[Key("k1")])
        assertEquals(V1, target[Key("k2")])
        assertEquals(setOf(K1, K2), target.getKeys(Value("v1")).toSet())
        // Same id but different object
        assertNotEquals(setOf(Key("k1"), Key("k2")), target.getKeys(Value("v1")).toSet())
    }

    @Test
    fun testAdd_Conflict() {
        target[K1] = V1
        target[K1] = V1
        target[K2] = V1
        target[K3] = V2

        target[K1] = V2
        assertEquals(V2, target[K1])
        assertEquals(V1, target[K2])
        assertEquals(V2, target[K3])

        assertEquals(setOf(K1, K3), target.getKeys(V2).toSet())
        assertEquals(setOf(K2), target.getKeys(V1).toSet())
    }

    @Test
    fun testRemoveKey() {
        target.removeKey(K1)

        target[K1] = V1
        target[K2] = V2
        target.removeKey(K1)
        assertNull(target[K1])
        assertTrue(target.getKeys(V1).isEmpty())
        assertEquals(V2, target[K2])
    }

    @Test
    fun testRemoveValue() {
        target.removeValue(V1)

        target[K1] = V1
        target[K2] = V1

        target[K3] = V2

        target.removeValue(V1)
        assertEquals(emptySet(), target.getKeys(V1).toSet())
        assertNull(target[K1])
        assertNull(target[K2])
        assertEquals(V2, target[K3])
        assertEquals(setOf(K3), target.getKeys(V2).toSet())
    }

    @Test
    fun testGetKey() {
        target[K1] = V1

        assertEquals(K1, target.getKey(Key("k1")))
    }

    private class Key(override val id: String) : Identifier

    private class Value(override val id: String) : Identifier

    companion object {
        private val K1 = Key("k1")
        private val K2 = Key("k2")
        private val K3 = Key("k3")

        private val V1 = Value("v1")
        private val V2 = Value("v2")
    }
}
