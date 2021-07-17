package mono.shape.shape

import mono.graphics.geo.Rect
import mono.shape.list.QuickList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * A test for [Group].
 */
class GroupTest {
    private val target: Group = Group(parentId = "100")

    @Test
    fun testAdd() {
        assertEquals(0, target.itemCount)
        val invalidShape = Rectangle(Rect.ZERO, parentId = "10000")
        val validShape1 = Rectangle(Rect.ZERO, parentId = null)
        val validShape2 = Rectangle(Rect.ZERO, parentId = target.id)
        val validShape3 = Rectangle(Rect.ZERO, parentId = null)

        target.add(invalidShape)
        assertEquals(0, target.itemCount)
        assertTrue(target.items.isEmpty())

        target.add(validShape1)
        assertEquals(1, target.itemCount)
        assertEquals(target.id, validShape1.parentId)
        assertEquals(listOf(validShape1), target.items.toList())

        // Repeat adding existing object
        target.add(validShape1)
        assertEquals(1, target.itemCount)
        assertEquals(target.id, validShape1.parentId)
        assertEquals(listOf(validShape1), target.items.toList())

        target.add(validShape2, QuickList.AddPosition.First)
        assertEquals(2, target.itemCount)
        assertEquals(target.id, validShape2.parentId)
        assertEquals(listOf(validShape2, validShape1), target.items.toList())

        target.add(validShape3, QuickList.AddPosition.After(validShape2))
        assertEquals(3, target.itemCount)
        assertEquals(target.id, validShape3.parentId)
        assertEquals(listOf(validShape2, validShape3, validShape1), target.items.toList())
    }

    @Test
    fun testRemove() {
        val shape1 = Rectangle(Rect.ZERO)
        val shape2 = Rectangle(Rect.ZERO)

        target.add(shape1)
        target.add(shape2)

        target.remove(shape1)
        assertEquals(1, target.itemCount)
        assertEquals(listOf(shape2), target.items.toList())

        target.remove(shape2)
        assertEquals(0, target.itemCount)
        assertTrue(target.items.isEmpty())
    }

    @Test
    fun testMove_up() {
        val shape1 = Rectangle(Rect.ZERO)
        val shape2 = Rectangle(Rect.ZERO)
        val shape3 = Rectangle(Rect.ZERO)

        target.add(shape1)
        target.add(shape2)
        target.add(shape3)

        target.changeOrder(shape1, QuickList.MoveActionType.UP)
        assertEquals(listOf(shape2, shape1, shape3), target.items.toList())
    }

    @Test
    fun testMove_down() {
        val shape1 = Rectangle(Rect.ZERO)
        val shape2 = Rectangle(Rect.ZERO)
        val shape3 = Rectangle(Rect.ZERO)

        target.add(shape1)
        target.add(shape2)
        target.add(shape3)

        target.changeOrder(shape3, QuickList.MoveActionType.DOWN)
        assertEquals(listOf(shape1, shape3, shape2), target.items.toList())
    }

    @Test
    fun testMove_top() {
        val shape1 = Rectangle(Rect.ZERO)
        val shape2 = Rectangle(Rect.ZERO)
        val shape3 = Rectangle(Rect.ZERO)

        target.add(shape1)
        target.add(shape2)
        target.add(shape3)

        target.changeOrder(shape1, QuickList.MoveActionType.TOP)
        assertEquals(listOf(shape2, shape3, shape1), target.items.toList())
    }

    @Test
    fun testMove_bottom() {
        val shape1 = Rectangle(Rect.ZERO)
        val shape2 = Rectangle(Rect.ZERO)
        val shape3 = Rectangle(Rect.ZERO)

        target.add(shape1)
        target.add(shape2)
        target.add(shape3)

        target.changeOrder(shape3, QuickList.MoveActionType.BOTTOM)
        assertEquals(listOf(shape3, shape1, shape2), target.items.toList())
    }
}
