/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.livedata

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [NonNullOnlyLiveData].
 */
class NonNullOnlyLiveDataTest {
    private val lifecycleOwner = MockLifecycleOwner()

    @Test
    fun testNonNullOnly_withNullInit() {
        val liveData = MutableLiveData<Int?>(null)
        var count = 0
        var currentValue = 0

        liveData.filterNotNull().observe(lifecycleOwner) {
            count += 1
            currentValue = it
        }
        assertEquals(0, count)
        assertEquals(0, currentValue) // unset

        liveData.value = 1
        assertEquals(1, count)
        assertEquals(1, currentValue)

        liveData.value = 10
        assertEquals(2, count)
        assertEquals(10, currentValue)
    }

    @Test
    fun testNonNullOnly_withNonNullInit() {
        val liveData = MutableLiveData<Int?>(20)
        var count = 0
        var currentValue = 0

        liveData.filterNotNull().observe(lifecycleOwner) {
            count += 1
            currentValue = it
        }

        assertEquals(1, count)
        assertEquals(20, currentValue)
    }
}
