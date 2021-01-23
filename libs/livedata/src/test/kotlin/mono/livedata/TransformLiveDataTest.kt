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

        liveData.value = 2
        assertEquals("2", currentValue)
        assertEquals(1, count)

        liveData.value = 3
        assertEquals("3", currentValue)
        assertEquals(2, count)
    }

    @Test
    fun testTransform_distinct() {
        val liveData = MutableLiveData(1)
        val transformLiveData = liveData.map { "${it % 2}" }

        var currentValue = ""
        var count = 0
        transformLiveData.observe(lifecycleOwner, true) {
            currentValue = it
            count += 1
        }

        liveData.value = 3
        liveData.value = 5
        assertEquals(0, count)
        assertEquals("", currentValue)

        liveData.value = 2
        assertEquals(1, count)
        assertEquals("0", currentValue)
    }
}
