/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.bitmap.drawable

import kotlin.test.Test
import kotlin.test.assertEquals
import mono.graphics.bitmap.drawable.NinePatchDrawable.RepeatableRange

/**
 * A test for [RepeatableRange]
 */
class RepeatableRangeTest {

    @Test
    fun testToIndexes_Scale() {
        val target = RepeatableRange.Scale(3, 5)
        assertEquals(
            listOf(0, 1, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 7),
            target.toIndexes(16, 8)
        )
        assertEquals(
            listOf(0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4),
            target.toIndexes(16, 5)
        )
    }

    @Test
    fun testToIndexes_Repeat() {
        val target = RepeatableRange.Repeat(3, 5)
        assertEquals(
            listOf(0, 1, 2, 3, 4, 5, 3, 4, 5, 3, 4, 5, 3, 4, 6, 7),
            target.toIndexes(16, 8)
        )
        assertEquals(
            listOf(0, 1, 2, 3, 4, 3, 4, 3, 4, 3, 4, 3, 4, 3, 4, 3),
            target.toIndexes(16, 5)
        )
    }
}
