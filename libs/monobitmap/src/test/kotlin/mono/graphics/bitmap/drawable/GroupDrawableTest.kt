package mono.graphics.bitmap.drawable

import mono.graphics.bitmap.MonoBitmapManager
import mono.graphics.geo.Rect
import mono.shape.ShapeManager
import mono.shape.add
import mono.shape.shape.Group
import mono.shape.shape.MockShape
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [GroupDrawable]
 */
class GroupDrawableTest {
    private val bitmapManager: MonoBitmapManager = MonoBitmapManager()
    private val shapeManager: ShapeManager = ShapeManager()

    @Test
    fun testToBitmap() {
        val group = Group(null)
        shapeManager.add(group)

        val rect1 = MockShape(Rect.byLTWH(10, 10, 6, 4), group.id)
        shapeManager.add(rect1)
        val rect2 = MockShape(Rect.byLTWH(12, 11, 5, 5), group.id)
        shapeManager.add(rect2)
        val rect3 = MockShape(Rect.byLTWH(-5, 9, 3, 3), group.id)
        shapeManager.add(rect3)

        val bitmap = GroupDrawable.toBitmap(bitmapManager, group)
        println("$bitmap\n")
        assertEquals(
            """
            +-+                   
            | |            +----+ 
            +-+            | +---+
                           | |  ||
                           +-|--+|
                             |   |
                             +---+
            """.trimIndent(),
            bitmap.toString()
        )
    }
}
