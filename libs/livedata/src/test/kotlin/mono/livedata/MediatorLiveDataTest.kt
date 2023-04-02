/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.livedata

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [MediatorLiveData]
 */
class MediatorLiveDataTest {
    private val lifecycleOwner = MockLifecycleOwner()

    @Test
    fun testMediator() {
        val liveData1 = MutableLiveData(1)
        val liveData2 = MutableLiveData(10)

        val mediatorLiveData = MediatorLiveData(Pair(0, 0))
        mediatorLiveData.add(liveData1) {
            value = value.copy(first = it)
        }
        mediatorLiveData.add(liveData2) {
            value = value.copy(second = it)
        }

        var count = 0
        var currentValue = 0 to 0
        mediatorLiveData.observe(lifecycleOwner) {
            count += 1
            currentValue = it
        }
        assertEquals(1, count)
        assertEquals(1 to 10, currentValue)

        liveData1.value = 2
        assertEquals(2, count)
        assertEquals(2 to 10, currentValue)

        liveData2.value = 11
        assertEquals(3, count)
        assertEquals(2 to 11, currentValue)
    }
}
