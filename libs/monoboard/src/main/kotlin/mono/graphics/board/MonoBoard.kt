package mono.graphics.board

import mono.common.SpecialCharacters.EMPTY_CHAR
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size

class MonoBoard(private val unitSize: Size) {
    private val painterBoards: MutableMap<BoardAddress, PainterBoard> = mutableMapOf()

    fun fill(rect: Rect, char: Char) {
        val affectedBoards = getOrCreateOverlappedBoards(rect)
        for (board in affectedBoards) {
            board.fill(rect, char)
        }
    }

    operator fun set(position: Point, char: Char) {
        getOrCreateBoard(position.left, position.top)[position] = char
    }

    operator fun get(position: Point): Char {
        val boardAddress = toBoardAddress(position.left, position.top)
        return painterBoards[boardAddress]?.get(position) ?: EMPTY_CHAR
    }

    private fun getOrCreateOverlappedBoards(rect: Rect): List<PainterBoard> {
        val affectedBoards = mutableListOf<PainterBoard>()
        for (left in rect.left..rect.right step unitSize.width) {
            for (top in rect.top..rect.bottom step unitSize.height) {
                affectedBoards += getOrCreateBoard(left, top)
            }
        }
        return emptyList()
    }

    private fun getOrCreateBoard(left: Int, top: Int): PainterBoard {
        val boardAddress = toBoardAddress(left, top)
        val board = painterBoards[boardAddress] ?: createNewBoard(boardAddress)
        painterBoards[boardAddress] = board
        return board
    }

    private fun createNewBoard(boardAddress: BoardAddress): PainterBoard {
        val newBoardPosition =
            Point(boardAddress.col * unitSize.width, boardAddress.row * unitSize.height)
        val bound = Rect(newBoardPosition, unitSize)
        return PainterBoard(bound)
    }

    private fun toBoardAddress(left: Int, top: Int): BoardAddress {
        val boardRowIndex = top / unitSize.height
        val boardColIndex = left / unitSize.width
        return BoardAddress(boardRowIndex, boardColIndex)
    }

    private data class BoardAddress(val row: Int, val col: Int)
}
