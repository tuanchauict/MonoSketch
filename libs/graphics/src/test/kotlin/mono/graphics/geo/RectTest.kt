package mono.graphics.geo

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
