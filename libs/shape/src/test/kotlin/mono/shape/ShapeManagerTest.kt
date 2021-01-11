package mono.shape

import mono.graphics.geo.Rect
import mono.shape.shape.Group
import mono.shape.shape.Rectangle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * A test of [ShapeManager]
 */
class ShapeManagerTest {
    private val target: ShapeManager = ShapeManager()

    @Test
    fun testAdd() {
        val shape1 = Rectangle(Rect.ZERO)
        target.add(shape1)
        assertEquals(listOf(shape1), target.root.items.toList())

        val group1 = Group(null)
        target.add(group1)
        assertEquals(listOf(shape1, group1), target.root.items.toList())

        val shape2 = Rectangle(Rect.ZERO, parentId = group1.id)
        target.add(shape2)
        assertEquals(listOf(shape1, group1), target.root.items.toList())
        assertEquals(listOf(shape2), group1.items.toList())

        val shape3 = Rectangle(Rect.ZERO, parentId = 1000)
        target.add(shape3)
        assertEquals(listOf(shape1, group1), target.root.items.toList())
        assertEquals(listOf(shape2), group1.items.toList())
    }

    @Test
    fun testRemove_singleGroupItem_removeGroup() {
        val group = Group(null)
        val shape = Rectangle(Rect.ZERO, group.id)

        target.add(group)
        target.add(shape)
        target.remove(shape)
        assertEquals(0, target.root.itemCount)
    }

    @Test
    fun testRemove_removeGroupItem_ungroup() {
        val group = Group(null)
        val shape1 = Rectangle(Rect.ZERO)
        val shape2 = Rectangle(Rect.ZERO, group.id)
        val shape3 = Rectangle(Rect.ZERO, group.id)

        target.add(group)
        target.add(shape1)
        target.add(shape2)
        target.add(shape3)

        target.remove(shape3)
        assertEquals(listOf(shape2, shape1), target.root.items.toList())
        assertEquals(target.root.id, shape2.parentId)
    }

    @Test
    fun testRemove_removeGroupItem_unchangeRoot() {
        val group = Group(null)
        val shape1 = Rectangle(Rect.ZERO)
        val shape2 = Rectangle(Rect.ZERO, group.id)
        val shape3 = Rectangle(Rect.ZERO, group.id)
        val shape4 = Rectangle(Rect.ZERO, group.id)

        target.add(group)
        target.add(shape1)
        target.add(shape2)
        target.add(shape3)
        target.add(shape4)

        target.remove(shape4)
        assertEquals(listOf(group, shape1), target.root.items.toList())
        assertEquals(listOf(shape2, shape3), group.items.toList())
    }

    @Test
    fun testGroup_invalid() {
        val group = Group(null)
        val shape1 = Rectangle(Rect.ZERO)
        val shape2 = Rectangle(Rect.ZERO, group.id)

        target.add(group)
        target.add(shape1)
        target.add(shape2)

        target.group(listOf(shape1))
        assertEquals(listOf(group, shape1), target.root.items.toList())

        target.group(listOf(shape1, shape2))
        assertEquals(listOf(group, shape1), target.root.items.toList())
        assertEquals(listOf(shape2), group.items.toList())
    }

    @Test
    fun testGroup_valid() {
        val shape0 = Rectangle(Rect.ZERO)
        val shape1 = Rectangle(Rect.ZERO)
        val shape2 = Rectangle(Rect.ZERO)
        val shape3 = Rectangle(Rect.ZERO)

        target.add(shape0)
        target.add(shape1)
        target.add(shape2)
        target.add(shape3)
        target.group(listOf(shape1, shape2))

        val items = target.root.items.toList()

        assertEquals(3, target.root.itemCount)
        assertEquals(shape0, items.first())
        assertEquals(shape3, items.last())
        val group = items[1] as Group
        assertEquals(listOf(shape1, shape2), group.items.toList())
    }

    @Test
    fun testUngroup() {
        val group = Group(null)
        val shape0 = Rectangle(Rect.ZERO)
        val shape1 = Rectangle(Rect.ZERO, group.id)
        val shape2 = Rectangle(Rect.ZERO, group.id)
        val shape3 = Rectangle(Rect.ZERO)

        target.add(shape0)
        target.add(group)
        target.add(shape1)
        target.add(shape2)
        target.add(shape3)

        target.ungroup(group)
        assertEquals(listOf(shape0, shape1, shape2, shape3), target.root.items.toList())
    }

    @Test
    fun testRecursiveVersionUpdate() {
        val group0 = Group(null)
        target.add(group0)
        val group1 = Group(group0.id)
        target.add(group1)

        val rootOldVersion = target.root.version
        val group0OldVersion = group0.version
        val group1OldVersion = group1.version

        val group2 = Group(group1.id)
        target.add(group2)

        assertTrue(target.root.version != rootOldVersion)
        assertTrue(group0.version != group0OldVersion)
        assertTrue(group1.version != group1OldVersion)
    }
}
