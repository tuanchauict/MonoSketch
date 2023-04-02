/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.livedata

import mono.common.setTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * A test for [LiveData]
 */
class LiveDataTest {
    private val lifecycleOwner = MockLifecycleOwner()

    @Test
    fun test_simple_validLifecycle() {
        val liveData = MutableLiveData(1)
        var currentValue = 100
        var count = 0
        liveData.observe(lifecycleOwner) {
            currentValue = it
            count += 1
        }

        liveData.value = 2
        assertEquals(2, currentValue)
        assertEquals(2, count)

        liveData.value = 3
        assertEquals(3, currentValue)
        assertEquals(3, count)
    }

    @Test
    fun test_throttle_validLifecycle() {
        val liveData = MutableLiveData(1)
        var currentValue = 100
        var count = 0
        liveData.observe(lifecycleOwner, throttleDurationMillis = 10) {
            currentValue = it
            count += 1
        }

        liveData.value = 2
        liveData.value = 3
        assertEquals(100, currentValue)
        assertEquals(0, count)

        setTimeout(20) {
            // This callback is not called since test method has ended.
            // TODO: Correct this. How to wait?
            assertEquals(3, currentValue)
            assertEquals(1, count)
        }
    }

    @Test
    fun test_normal_lifecycleStopped() {
        val liveData = MutableLiveData(1)
        var currentValue = 100
        var count = 0
        liveData.observe(lifecycleOwner) {
            currentValue = it
            count += 1
        }
        assertEquals(1, liveData.observers.size)

        lifecycleOwner.onStop()
        assertTrue(liveData.observers.isEmpty())

        liveData.value = 10
        assertEquals(1, currentValue)
        assertEquals(1, count)

        liveData.observe(lifecycleOwner) {
        }
        assertTrue(liveData.observers.isEmpty())
    }
}
