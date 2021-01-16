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
class MonoBoard(private val unitSize: Size = STANDARD_UNIT_SIZE) {

    private val painterBoards: MutableMap<BoardAddress, PainterBoard> = mutableMapOf()

    internal val boardCount: Int
        get() = painterBoards.size

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

    operator fun set(position: Point, char: Char) = set(position.left, position.top, char)

    fun set(left: Int, top: Int, char: Char) = getOrCreateBoard(left, top).set(left, top, char)

    operator fun get(position: Point): Char = get(position.left, position.top)

    fun get(left: Int, top: Int): Char {
        val boardAddress = toBoardAddress(left, top)
        return painterBoards[boardAddress]?.get(left, top) ?: TRANSPARENT_CHAR
    }

    private fun getOrCreateOverlappedBoards(rect: Rect): List<PainterBoard> {
        val affectedBoards = mutableListOf<PainterBoard>()

        val leftIndex = rect.left adjustDivide unitSize.width
        val rightIndex = rect.right adjustDivide unitSize.width
        val topIndex = rect.top adjustDivide unitSize.height
        val bottomIndex = rect.bottom adjustDivide unitSize.height

        for (left in leftIndex..rightIndex) {
            for (top in topIndex..bottomIndex) {
                affectedBoards += getOrCreateBoard(left * unitSize.width, top * unitSize.height)
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
        boardRowIndex = top adjustDivide unitSize.height,
        boardColIndex = left adjustDivide unitSize.width
    )

    private infix fun Int.adjustDivide(denominator: Int): Int =
        if (this > 0 || this % denominator == 0) this / denominator else this / denominator - 1

    override fun toString(): String {
        val left = painterBoards.keys.minOf { it.col }
        val right = painterBoards.keys.maxOf { it.col } + 1
        val top = painterBoards.keys.minOf { it.row }
        val bottom = painterBoards.keys.maxOf { it.row } + 1
        val rect = Rect.byLTWH(
            left = left * unitSize.width,
            top = top * unitSize.height,
            width = (right - left) * unitSize.width,
            height = (bottom - top) * unitSize.height
        )
        val painterBoard = PainterBoard(rect)

        painterBoards.values.forEach {
            painterBoard.fill(it)
        }
        return painterBoard.toString()
    }

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

    companion object {
        val STANDARD_UNIT_SIZE = Size(128, 64)
    }
}
