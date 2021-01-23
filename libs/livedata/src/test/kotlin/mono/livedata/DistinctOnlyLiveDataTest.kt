package mono.livedata

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [DistinctOnlyLiveData]
 */
class DistinctOnlyLiveDataTest {
    private val lifecycleOwner = MockLifecycleOwner()

    @Test
    fun testDistinctOnly() {
        val liveData = MutableLiveData(10)
        var count = 0
        var currentValue = 0
        liveData.distinctUntilChange().observe(lifecycleOwner) {
            count += 1
            currentValue = it
        }
        assertEquals(1, count)
        assertEquals(10, currentValue)

        liveData.value = 10
        assertEquals(1, count)
        assertEquals(10, currentValue)

        liveData.value = 11
        assertEquals(2, count)
        assertEquals(11, currentValue)

        liveData.value = 10
        assertEquals(3, count)
        assertEquals(10, currentValue)
    }
}
