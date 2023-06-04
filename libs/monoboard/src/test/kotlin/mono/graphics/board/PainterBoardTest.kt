/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.board

import kotlin.test.Test
import kotlin.test.assertEquals
import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Point
import mono.graphics.geo.Rect

/**
 * A test for [PainterBoard]
 */
class PainterBoardTest {
    @Test
    fun testFill_inside() {
        val board = PainterBoard(Rect.byLTWH(0, 0, 4, 4))
        board.fill(Rect.byLTWH(1, 2, 3, 2), '#', Highlight.NO)
        assertEquals("    \n    \n ###\n ###", board.toString())
    }

    @Test
    fun testFill_Outside() {
        val board = PainterBoard(Rect.byLTWH(1, 1, 4, 4))
        board.fill(Rect.byLTWH(0, 0, 5, 5), '#', Highlight.NO)
        assertEquals("####\n####\n####\n####", board.toString())
    }

    @Test
    fun testFillBoard() {
        val board1 = PainterBoard(Rect.byLTWH(0, 0, 4, 4))
        board1.fill(Rect.byLTWH(0, 0, 2, 2), 'a', Highlight.NO)
        board1.fill(Rect.byLTWH(0, 2, 2, 2), 'b', Highlight.NO)
        board1.fill(Rect.byLTWH(2, 0, 2, 2), 'c', Highlight.NO)
        board1.fill(Rect.byLTWH(2, 2, 2, 2), 'd', Highlight.NO)
        board1.set(Point(2, 1), TRANSPARENT_CHAR, Highlight.NO)

        val board2 = PainterBoard(Rect.byLTWH(1, 1, 3, 2))
        board2.set(Point(2, 1), 'x', Highlight.NO)
        board2.fill(board1)
        assertEquals("axc\nbdd", board2.toString())
    }

    @Test
    fun testFillMonoBitmap() {
        val builder = MonoBitmap.Builder(2, 2)
        builder.put(0, 0, 'a', 'a')
        builder.put(0, 1, 'b', 'b')

        val board = PainterBoard(Rect.byLTWH(0, 0, 3, 3))
        board.set(Point(2, 2), 'x', Highlight.NO)
        board.fill(Point(1, 1), builder.toBitmap(), Highlight.NO)
        assertEquals("   \n ab\n  x", board.toString())
    }
}
