package mono.livedata

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [TransformLiveData]
 */
class TransformLiveDataTest {
    private val lifecycleOwner = MockLifecycleOwner()

    @Test
    fun testTransform() {
        val liveData = MutableLiveData(1)
        val transformLiveData = liveData.map { "$it" }

        var currentValue = ""
        var count = 0
        transformLiveData.observe(lifecycleOwner) {
            currentValue = it
            count += 1
        }
        assertEquals("1", transformLiveData.value)
        assertEquals(1, count)
        assertEquals("1", currentValue)

        liveData.value = 2
        assertEquals("2", currentValue)
        assertEquals(2, count)

        liveData.value = 3
        assertEquals("3", currentValue)
        assertEquals(3, count)
    }
}
