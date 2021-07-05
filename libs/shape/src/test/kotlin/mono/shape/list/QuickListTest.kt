package mono.shape.list

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * A test for [QuickList]
 */
internal class QuickListTest {
    private val target: QuickList<Item> = QuickList()

    @Test
    fun testAdd() {
        assertTrue(target.isEmpty())

        assertTrue(target.add(TestData.ITEM_1))
        assertFalse(target.add(TestData.ITEM_1))
        assertEquals(1, target.size)
        assertEquals(TestData.ITEM_1, target["1"])

        target.add(TestData.ITEM_0, QuickList.AddPosition.First)
        assertEquals(TestData.ITEM_0, target["0"])
        assertEquals(TestData.ITEM_1, target["1"])
        assertEquals(listOf(TestData.ITEM_0, TestData.ITEM_1), target.toList())

        target.add(TestData.ITEM_3)
        target.add(TestData.ITEM_2, QuickList.AddPosition.After(TestData.ITEM_1))
        assertEquals(
            listOf(TestData.ITEM_0, TestData.ITEM_1, TestData.ITEM_2, TestData.ITEM_3),
            target.toList()
        )
    }

    @Test
    fun testAddAll_last() {
        target.add(TestData.ITEM_0)
        target.add(TestData.ITEM_1)
        target.addAll(listOf(TestData.ITEM_2, TestData.ITEM_3))
        assertEquals(
            listOf(TestData.ITEM_0, TestData.ITEM_1, TestData.ITEM_2, TestData.ITEM_3),
            target.toList()
        )
    }

    @Test
    fun testAddAll_first() {
        target.add(TestData.ITEM_2)
        target.add(TestData.ITEM_3)
        target.addAll(
            listOf(TestData.ITEM_0, TestData.ITEM_1),
            QuickList.AddPosition.First
        )
        assertEquals(
            listOf(TestData.ITEM_0, TestData.ITEM_1, TestData.ITEM_2, TestData.ITEM_3),
            target.toList()
        )
    }

    @Test
    fun testAddAll_after() {
        target.add(TestData.ITEM_0)
        target.add(TestData.ITEM_3)
        target.addAll(
            listOf(TestData.ITEM_1, TestData.ITEM_2),
            QuickList.AddPosition.After(Item("0"))
        )
        assertEquals(
            listOf(TestData.ITEM_0, TestData.ITEM_1, TestData.ITEM_2, TestData.ITEM_3),
            target.toList()
        )
    }

    @Test
    fun testRemove() {
        target.add(TestData.ITEM_0)
        target.add(TestData.ITEM_1)

        assertNull(target.remove(TestData.ITEM_2))

        assertEquals(TestData.ITEM_1, target.remove(Item("1")))
        assertEquals(1, target.size)
        assertNull(target["1"])

        assertEquals(TestData.ITEM_0, target.remove(Item("0")))
        assertTrue(target.isEmpty())
    }

    @Test
    fun testRemoveAll() {
        target.addAll(TestData.ITEMS)
        target.removeAll()
        assertTrue(target.isEmpty())
    }

    @Test
    fun testMove_up() {
        target.addAll(TestData.ITEMS)

        target.move(Item("1"), QuickList.MoveActionType.UP)
        assertEquals(
            listOf(TestData.ITEM_0, TestData.ITEM_2, TestData.ITEM_1, TestData.ITEM_3),
            target.toList()
        )
    }

    @Test
    fun testMove_down() {
        target.addAll(TestData.ITEMS)

        target.move(Item("2"), QuickList.MoveActionType.DOWN)
        assertEquals(
            listOf(TestData.ITEM_0, TestData.ITEM_2, TestData.ITEM_1, TestData.ITEM_3),
            target.toList()
        )
    }

    @Test
    fun testMove_top() {
        target.addAll(TestData.ITEMS)

        target.move(Item("1"), QuickList.MoveActionType.TOP)
        assertEquals(
            listOf(TestData.ITEM_0, TestData.ITEM_2, TestData.ITEM_3, TestData.ITEM_1),
            target.toList()
        )
    }

    @Test
    fun testMove_bottom() {
        target.addAll(TestData.ITEMS)

        target.move(Item("2"), QuickList.MoveActionType.BOTTOM)
        assertEquals(
            listOf(TestData.ITEM_2, TestData.ITEM_0, TestData.ITEM_1, TestData.ITEM_3),
            target.toList()
        )
    }

    private data class Item(override val id: String) : QuickList.Identifier

    private object TestData {
        val ITEM_0 = Item("0")
        val ITEM_1 = Item("1")
        val ITEM_2 = Item("2")
        val ITEM_3 = Item("3")

        val ITEMS = listOf(ITEM_0, ITEM_1, ITEM_2, ITEM_3)
    }
}
