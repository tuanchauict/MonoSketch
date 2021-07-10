package mono.bitmap.manager.factory

import mono.graphics.geo.Rect
import mono.shape.shape.Rectangle
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [RectangleBitmapFactory]
 */
class RectangleBitmapFactoryTest {
    @Test
    fun testToBitmap() {
        val shape = Rectangle(Rect.byLTWH(10, 10, 5, 5))
        val bitmap = RectangleBitmapFactory.toBitmap(shape.bound.size, shape.extra)
        assertEquals(shape.bound.width, bitmap.size.width)
        assertEquals(shape.bound.height, bitmap.size.height)
        assertEquals(
            """
            ┌───┐
            │   │
            │   │
            │   │
            └───┘
            """.trimIndent(),
            bitmap.toString()
        )
    }
}
