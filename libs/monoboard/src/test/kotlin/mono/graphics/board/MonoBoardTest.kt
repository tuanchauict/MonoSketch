package mono.graphics.board

import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [MonoBoard]
 */
class MonoBoardTest {

    @Test
    fun testGetSet() {
        val target = MonoBoard(Size(100, 100))

        val points = listOf(-300, -256, -128, -100, 0, 100, 128, 256, 300).map { Point(it, it) }

        points.forEach {
            assertEquals(TRANSPARENT_CHAR, target[it])
        }
        val chars = "012345678"
        chars.forEachIndexed { index, c -> target[points[index]] = c }
        chars.forEachIndexed { index, c ->
            assertEquals(c, target[points[index]])
        }

        assertEquals(7, target.boardCount)
    }

    @Test
    fun testFill() {
        val target = MonoBoard(Size(5, 5))
        target.fill(Rect.byLTWH(1, 1, 3, 3), 'A')
        assertEquals(
            """     
 AAA 
 AAA 
 AAA 
     """, target.toString()
        )
        assertEquals(1, target.boardCount)

        target.fill(Rect.byLTWH(-3, -3, 3, 3), 'B')
        assertEquals(
            """          
          
  BBB     
  BBB     
  BBB     
          
      AAA 
      AAA 
      AAA 
          """,
        target.toString())
        assertEquals(2, target.boardCount)

        target.fill(Rect.byLTWH(-1, 0, 3, 1), 'C')
        assertEquals(
            """          
          
  BBB     
  BBB     
  BBB     
    CCC   
      AAA 
      AAA 
      AAA 
          """,
            target.toString()
        )
        assertEquals(3, target.boardCount)
    }
}
