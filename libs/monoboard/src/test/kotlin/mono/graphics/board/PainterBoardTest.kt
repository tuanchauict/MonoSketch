package mono.graphics.board

import mono.common.SpecialCharacters.EMPTY_CHAR
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [PainterBoard]
 */
class PainterBoardTest {
    @Test
    fun testFill_inside() {
        val board = PainterBoard(Rect.byLTWH(0, 0, 4, 4))
        board.fill(Rect.byLTWH(1, 2, 3, 2), '#')
        assertEquals("    \n    \n ###\n ###", board.toString())
    }

    @Test
    fun testFill_Outside() {
        val board = PainterBoard(Rect.byLTWH(1, 1, 4, 4))
        board.fill(Rect.byLTWH(0, 0, 5, 5), '#')
        assertEquals("####\n####\n####\n####", board.toString())
    }

    @Test
    fun testFillBoard() {
        val board1 = PainterBoard(Rect.byLTWH(0, 0, 4, 4))
        board1.fill(Rect.byLTWH(0, 0, 2, 2), 'a')
        board1.fill(Rect.byLTWH(0, 2, 2, 2), 'b')
        board1.fill(Rect.byLTWH(2, 0, 2, 2), 'c')
        board1.fill(Rect.byLTWH(2, 2, 2, 2), 'd')
        board1[Point(2,1)] = EMPTY_CHAR

        val board2 = PainterBoard(Rect.byLTWH(1, 1, 3, 2))
        board2[Point(2,1)] = 'x'
        board2.fill(board1)
        assertEquals("axc\nbdd", board2.toString())
    }

    @Test
    fun testFillMonoBitmap() {
        val bitmap = MonoBitmap(2, 2)
        bitmap.put(0, 0, 'a')
        bitmap.put(0, 1, 'b')

        val board = PainterBoard(Rect.byLTWH(0, 0, 3, 3))
        board[Point(2, 2)] = 'x'
        board.fill(Point(1, 1), bitmap)
        assertEquals("   \n ab\n  x", board.toString())
    }
}
