package mono.graphics.board

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [MonoBoard]
 */
class MonoBoardTest {
    private val target = MonoBoard().apply {
        clearAndSetWindow(Rect.byLTWH(-100, -100, 200, 200))
    }
    @Test
    fun testGetSet() {
        val points = listOf(-48, -32, -18, -16, 0, 16, 18, 32, 48).map { Point(it, it) }

        points.forEach {
            assertEquals(Pixel.TRANSPARENT_PIXEL, target[it])
        }
        val chars = "012345678"
        chars.forEachIndexed { index, c -> target.set(points[index], c, Highlight.NO) }
        chars.forEachIndexed { index, c ->
            assertEquals(c, target[points[index]].char)
        }

        assertEquals(7, target.boardCount)
    }

    @Test
    fun testFill() {
        target.fill(Rect.byLTWH(1, 1, 3, 3), 'A', Highlight.NO)
        assertEquals(
            """
                |                
                | AAA            
                | AAA            
                | AAA            
                |                
                |                
                |                
                |                
                |                
                |                
                |                
                |                
                |                
                |                
                |                
                |                
            """.trimMargin(), target.toString()
        )
        assertEquals(1, target.boardCount)

        target.fill(Rect.byLTWH(-3, -3, 3, 3), 'B', Highlight.NO)
        assertEquals(
            """
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |             BBB                
                |             BBB                
                |             BBB                
                |                                
                |                 AAA            
                |                 AAA            
                |                 AAA            
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
            """.trimMargin(),
            target.toString()
        )
        assertEquals(2, target.boardCount)

        target.fill(Rect.byLTWH(-1, 0, 3, 1), 'C', Highlight.NO)
        assertEquals(
            """
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |             BBB                
                |             BBB                
                |             BBB                
                |               CCC              
                |                 AAA            
                |                 AAA            
                |                 AAA            
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
                |                                
            """.trimMargin(),
            target.toString()
        )
        assertEquals(3, target.boardCount)
    }
}
