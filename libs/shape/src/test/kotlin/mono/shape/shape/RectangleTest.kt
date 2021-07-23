package mono.shape.shape

import mono.graphics.geo.Rect
import mono.shape.serialization.SerializableRectangle
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [Rectangle].
 */
internal class RectangleTest {
    @Test
    fun testSerialization_init() {
        val rectangle = Rectangle(Rect.byLTWH(1, 2, 3, 4), PARENT_ID)

        val serializableRectangle = rectangle.toSerializableShape(true) as SerializableRectangle
        assertEquals(rectangle.bound, serializableRectangle.bound)
        assertEquals(rectangle.extra.toSerializableExtra(), serializableRectangle.extra)
    }

    @Test
    fun testSerialization_changeBound() {
        val rectangle = Rectangle(Rect.byLTWH(1, 2, 3, 4), parentId = PARENT_ID)
        rectangle.bound = Rect.byLTWH(5, 6, 7, 8)

        val serializableRectangle = rectangle.toSerializableShape(true) as SerializableRectangle
        assertEquals(rectangle.bound, serializableRectangle.bound)
        assertEquals(rectangle.extra.toSerializableExtra(), serializableRectangle.extra)
    }

    @Test
    fun testSerialization_Restore() {
        val rectangle = Rectangle(Rect.byLTWH(1, 2, 3, 4), parentId = PARENT_ID)
        rectangle.bound = Rect.byLTWH(5, 6, 7, 8)

        val serializableRectangle = rectangle.toSerializableShape(true) as SerializableRectangle

        val rectangle2 = Rectangle(serializableRectangle, parentId = PARENT_ID)
        assertEquals(rectangle.bound, rectangle2.bound)
        assertEquals(rectangle.extra, rectangle2.extra)
        assertEquals(PARENT_ID, rectangle2.parentId)
    }

    companion object {
        private const val PARENT_ID = "1"
    }
}
