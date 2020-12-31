package mono.graphics.board

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
        println(board.toString())
        assertEquals("    \n    \n ###\n ###\n", board.toString())
    }

    @Test
    fun testFill_Outside() {
        val board = PainterBoard(Rect.byLTWH(1, 1, 4, 4))
        board.fill(Rect.byLTWH(0, 0, 5, 5), '#')
        println(board.toString())
    }

    @Test
    fun testFillBoard() {
        val board1 = PainterBoard(Rect.byLTWH(0, 0, 4, 4))
        board1.fill(Rect.byLTWH(0, 0, 2, 2), 'a')
        board1.fill(Rect.byLTWH(0, 2, 2, 2), 'b')
        board1.fill(Rect.byLTWH(2, 0, 2, 2), 'c')
        board1.fill(Rect.byLTWH(2, 2, 2, 2), 'd')
        println(board1)
        val board2 = PainterBoard(Rect.byLTWH(1, 1, 3, 2))
        board2.fill(board1)
        println(board2)
        assertEquals("acc\nbdd\n", board2.toString())
    }
}
