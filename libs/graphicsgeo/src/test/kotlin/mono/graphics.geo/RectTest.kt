/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.geo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * A test for [Rect]
 */
class RectTest {
    @Test
    fun testGetOverlappedRect() {
        val rect1 = Rect.byLTWH(0, 0, 6, 4)
        val rect2 = Rect.byLTWH(2, 2, 4, 4)

        assertEquals(
            Rect.byLTWH(2, 2, 4, 2),
            rect1.getOverlappedRect(rect2)
        )

        assertEquals(
            Rect.byLTWH(2, 2, 4, 2),
            rect2.getOverlappedRect(rect1)
        )
    }

    @Test
    fun testGetOverlappedRect_NotOverlapped() {
        val rect1 = Rect.byLTWH(0, 0, 1, 1)
        val rect2 = Rect.byLTWH(2, 2, 1, 1)
        assertNull(rect1.getOverlappedRect(rect2))
    }
}
