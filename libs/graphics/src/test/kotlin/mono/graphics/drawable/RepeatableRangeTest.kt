package mono.graphics.drawable

import mono.graphics.drawable.NinePatchDrawable.RepeatableRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * A test for [RepeatableRange]
 */
class RepeatableRangeTest {
    @Test
    fun testVerify() {
        val target1 = RepeatableRange.repeat(1, 2)
        assertTrue(target1.verify(3))
        assertFalse(target1.verify(1))

        val target2 = RepeatableRange.repeat(-1, 1)
        assertFalse(target2.verify(10))
    }

    @Test
    fun testToIndexes_Scale() {
        val target = RepeatableRange.scale(3, 5)
        assertEquals(
            listOf(0, 1, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 7),
            target.toIndexes(16, 8)
        )
    }

    @Test
    fun testToIndexes_Repeat() {
        val target = RepeatableRange.repeat(3, 5)
        assertEquals(
            listOf(0, 1, 2, 3, 4, 5, 3, 4, 5, 3, 4, 5, 3, 4, 6, 7),
            target.toIndexes(16, 8)
        )
    }
}
