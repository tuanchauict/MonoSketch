/*
 * Copyright (c) 2023-2024, tuanchauict
 */

package mono.graphics.board

import mono.environment.Build
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.board.CrossingResources.hasBottom
import mono.graphics.board.CrossingResources.hasLeft
import mono.graphics.board.CrossingResources.hasRight
import mono.graphics.board.CrossingResources.hasTop
import mono.graphics.board.CrossingResources.inDirectionMark
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

    private var windowBound: Rect = Rect.ZERO

    fun clearAndSetWindow(windowBound: Rect) {
        this.windowBound = windowBound
        val affectedBoards = getOrCreateOverlappedBoards(windowBound, isCreateRequired = false)
        for (board in affectedBoards) {
            board.clear()
        }
    }

    fun fill(position: Point, bitmap: MonoBitmap, highlight: Highlight) {
        val rect = Rect(position, bitmap.size)
        val affectedBoards = getOrCreateOverlappedBoards(rect, isCreateRequired = true)

        val crossingPoints = mutableListOf<CrossPoint>()
        for (board in affectedBoards) {
            crossingPoints += board.fill(position, bitmap, highlight)
        }

        drawCrossingPoints2(crossingPoints, highlight)
    }

    private fun drawCrossingPoints(crossingPoints: List<CrossPoint>, highlight: Highlight) {
        for (charPoint in crossingPoints) {
            val currentPixel = get(charPoint.left, charPoint.top)
            val directionMap =
                CrossingResources.getDirectionMap(currentPixel.visualChar, charPoint.visualChar)
            if (directionMap == null) {
                // Unsupported pair
                currentPixel.set(charPoint.visualChar, charPoint.directionChar, highlight)
                continue
            }
            val directionMark =
                inDirectionMark(
                    hasLeft = charPoint.leftChar.hasLeft ||
                        get(charPoint.left - 1, charPoint.top).directionChar.hasLeft,
                    hasRight = charPoint.rightChar.hasRight ||
                        get(charPoint.left + 1, charPoint.top).directionChar.hasRight,
                    hasTop = charPoint.topChar.hasTop ||
                        get(charPoint.left, charPoint.top - 1).directionChar.hasTop,
                    hasBottom = charPoint.bottomChar.hasBottom ||
                        get(charPoint.left, charPoint.top + 1).directionChar.hasBottom
                )

            if (Build.DEBUG && DEBUG) {
                val bitmapSurroundingChars = listOf(
                    charPoint.leftChar,
                    charPoint.rightChar,
                    charPoint.topChar,
                    charPoint.bottomChar
                ).joinToString("•")
                val boardSurroundingChars = listOf(
                    get(charPoint.left - 1, charPoint.top).directionChar,
                    get(charPoint.left + 1, charPoint.top).directionChar,
                    get(charPoint.left, charPoint.top - 1).directionChar,
                    get(charPoint.left, charPoint.top + 1).directionChar
                ).joinToString("•")
                println(
                    "${charPoint.visualChar}${currentPixel.directionChar} " +
                        "($bitmapSurroundingChars) - ($boardSurroundingChars) -> " +
                        "${directionMap[directionMark]}"
                )
            }
            currentPixel.set(
                visualChar = directionMap[directionMark] ?: charPoint.visualChar,
                directionChar = directionMap[directionMark] ?: charPoint.directionChar,
                highlight = highlight
            )
        }
    }

    private fun drawCrossingPoints2(crossingPoints: List<CrossPoint>, highlight: Highlight) {
        for (charPoint in crossingPoints) {
            val left = charPoint.left
            val top = charPoint.top
            val currentPixel = get(left, top)
            val crossingChar = CrossingResources.getCrossingChar(
                upper = charPoint.visualChar,
                adjacentLeftUpper = charPoint.leftChar,
                adjacentRightUpper = charPoint.rightChar,
                adjacentTopUpper = charPoint.topChar,
                adjacentBottomUpper = charPoint.bottomChar,
                lower = get(left, top).visualChar,
                adjacentLeftLower = get(left - 1, top).directionChar,
                adjacentRightLower = get(left + 1, top).directionChar,
                adjacentTopLower = get(left, top - 1).directionChar,
                adjacentBottomLower = get(left, top + 1).directionChar
            )
            if (crossingChar != null) {
                currentPixel.set(crossingChar, crossingChar, highlight)
            } else {
                currentPixel.set(charPoint.visualChar, charPoint.directionChar, highlight)
            }
        }
    }

    // This method is for testing only
    internal fun fill(rect: Rect, char: Char, highlight: Highlight) {
        val affectedBoards = getOrCreateOverlappedBoards(rect, isCreateRequired = true)
        for (board in affectedBoards) {
            board.fill(rect, char, highlight)
        }
    }

    // This method is for testing only
    internal fun set(position: Point, char: Char, highlight: Highlight) {
        set(position.left, position.top, char, highlight)
    }

    // This method is for testing only
    fun set(left: Int, top: Int, char: Char, highlight: Highlight) {
        getOrCreateBoard(left, top, isCreateRequired = true)
            ?.set(left, top, char, highlight)
    }

    operator fun get(position: Point): Pixel = get(position.left, position.top)

    fun get(left: Int, top: Int): Pixel {
        val boardAddress = toBoardAddress(left, top)
        return painterBoards[boardAddress]?.get(left, top) ?: Pixel.TRANSPARENT_PIXEL
    }

    private fun getOrCreateOverlappedBoards(
        rect: Rect,
        isCreateRequired: Boolean
    ): List<PainterBoard> {
        val affectedBoards = mutableListOf<PainterBoard>()

        val leftIndex = rect.left adjustDivide unitSize.width
        val rightIndex = rect.right adjustDivide unitSize.width
        val topIndex = rect.top adjustDivide unitSize.height
        val bottomIndex = rect.bottom adjustDivide unitSize.height

        for (left in leftIndex..rightIndex) {
            for (top in topIndex..bottomIndex) {
                val board = getOrCreateBoard(
                    left = left * unitSize.width,
                    top = top * unitSize.height,
                    isCreateRequired = isCreateRequired
                )
                if (board != null) {
                    affectedBoards += board
                }
            }
        }
        return affectedBoards
    }

    private fun getOrCreateBoard(
        left: Int,
        top: Int,
        isCreateRequired: Boolean
    ): PainterBoard? {
        val boardAddress = toBoardAddress(left, top)
        val board = if (isCreateRequired) {
            painterBoards.getOrPut(boardAddress) { createNewBoard(boardAddress) }
        } else {
            painterBoards[boardAddress]
        }
        return board?.takeIf { windowBound.isOverlapped(it.bound) }
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

        painterBoards.values.forEach(painterBoard::fill)
        return painterBoard.toString()
    }

    fun toStringInBound(bound: Rect): String {
        val painterBoard = PainterBoard(bound)
        painterBoards.values.forEach(painterBoard::fill)
        return painterBoard.toString()
    }

    private data class BoardAddress(val row: Int, val col: Int)

    private object BoardAddressManager {
        private val addressMap: MutableMap<Int, MutableMap<Int, BoardAddress>> = mutableMapOf()

        init {
            for (rowIndex in -4..10) {
                addressMap[rowIndex] = mutableMapOf()
                for (colIndex in -4..16) {
                    addressMap[rowIndex]!![colIndex] = BoardAddress(rowIndex, colIndex)
                }
            }
        }

        fun get(boardRowIndex: Int, boardColIndex: Int): BoardAddress =
            addressMap.getOrPut(boardRowIndex) { mutableMapOf() }
                .getOrPut(boardColIndex) { BoardAddress(boardRowIndex, boardColIndex) }
    }

    /**
     * A data class that stores information of a cross point when drawing a bitmap with
     * [PainterBoard].
     * CrossPoint will then be drawn to the board after non-crossing pixels are drawn.
     *
     * @param [boardRow] and [boardColumn] are the location of point on the board.
     * @param [visualChar] is the character at the crossing point
     * @param [leftChar], [rightChar], [topChar], and [bottomChar] are 4 characters around the
     * crossing point
     */
    internal data class CrossPoint(
        val boardRow: Int,
        val boardColumn: Int,
        val visualChar: Char,
        val directionChar: Char,
        val leftChar: Char,
        val rightChar: Char,
        val topChar: Char,
        val bottomChar: Char
    ) {
        val left: Int = boardColumn
        val top: Int = boardRow
    }

    companion object {
        val STANDARD_UNIT_SIZE = Size(16, 16)

        // DO NOT change this value to true when commit
        private const val DEBUG = false
    }
}
