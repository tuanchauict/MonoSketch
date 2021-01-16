package mono.graphics.board

import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size

/**
 * A model class which manages all mono-pixels of the app.
 * This class is to allow infinity drawing.
 */
class MonoBoard(private val unitSize: Size) {

    private val painterBoards: MutableMap<BoardAddress, PainterBoard> = mutableMapOf()

    fun fill(rect: Rect, char: Char) {
        val affectedBoards = getOrCreateOverlappedBoards(rect)
        for (board in affectedBoards) {
            board.fill(rect, char)
        }
    }

    fun fill(position: Point, bitmap: MonoBitmap) {
        val rect = Rect.byLTWH(position.left, position.top, bitmap.width, bitmap.height)
        val affectedBoards = getOrCreateOverlappedBoards(rect)
        for (board in affectedBoards) {
            board.fill(position, bitmap)
        }
    }

    operator fun set(position: Point, char: Char) {
        getOrCreateBoard(position.left, position.top)[position] = char
    }

    operator fun get(position: Point): Char = get(position.left, position.top)

    fun get(left: Int, top: Int): Char {
        val boardAddress = toBoardAddress(left, top)
        return painterBoards[boardAddress]?.get(left, top) ?: TRANSPARENT_CHAR
    }

    private fun getOrCreateOverlappedBoards(rect: Rect): List<PainterBoard> {
        val affectedBoards = mutableListOf<PainterBoard>()
        for (left in rect.left..rect.right step unitSize.width) {
            for (top in rect.top..rect.bottom step unitSize.height) {
                affectedBoards += getOrCreateBoard(left, top)
            }
        }
        return affectedBoards
    }

    private fun getOrCreateBoard(left: Int, top: Int): PainterBoard {
        val boardAddress = toBoardAddress(left, top)
        return painterBoards.getOrPut(boardAddress) { createNewBoard(boardAddress) }
    }

    private fun createNewBoard(boardAddress: BoardAddress): PainterBoard {
        val newBoardPosition =
            Point(boardAddress.col * unitSize.width, boardAddress.row * unitSize.height)
        val bound = Rect(newBoardPosition, unitSize)
        return PainterBoard(bound)
    }

    private fun toBoardAddress(left: Int, top: Int): BoardAddress = BoardAddressManager.get(
        boardRowIndex = top / unitSize.height,
        boardColIndex = left / unitSize.width
    )

    internal data class BoardAddress(val row: Int, val col: Int)

    private object BoardAddressManager {
        private val addressMap: MutableMap<Int, MutableMap<Int, BoardAddress>> = mutableMapOf()

        init {
            for (rowIndex in -4..4) {
                addressMap[rowIndex] = mutableMapOf()
                for (colIndex in -4..4) {
                    addressMap[rowIndex]!![colIndex] = BoardAddress(rowIndex, colIndex)
                }
            }
        }

        fun get(boardRowIndex: Int, boardColIndex: Int): BoardAddress =
            addressMap.getOrPut(boardRowIndex) { mutableMapOf() }
                .getOrPut(boardColIndex) { BoardAddress(boardRowIndex, boardColIndex) }
    }
}
