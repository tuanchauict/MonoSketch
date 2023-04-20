/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.canvas.canvas

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import mono.graphics.geo.SizeF
import mono.html.Canvas
import mono.html.px
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.CanvasTextBaseline
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.LEFT
import org.w3c.dom.MIDDLE
import kotlin.math.ceil
import kotlin.math.floor

/**
 * A controller class to manage drawing info for the other canvas.
 */
internal class DrawingInfoController(container: HTMLDivElement) {
    private val context: CanvasRenderingContext2D

    private val drawingInfoMutableLiveData: MutableLiveData<DrawingInfo> =
        MutableLiveData(DrawingInfo())
    val drawingInfoLiveData: LiveData<DrawingInfo> =
        drawingInfoMutableLiveData.distinctUntilChange()

    init {
        val canvas = Canvas(container, CLASS_NAME)
        context = canvas.getContext("2d") as CanvasRenderingContext2D

        setSize(canvas.width, canvas.height)
        setFont(13)
    }

    fun setFont(fontSize: Int) {
        val cellSizePx = context.getCellSizePx(fontSize)
        val cellCharOffsetPx = SizeF(0.0, cellSizePx.height * 0.2)
        drawingInfoMutableLiveData.value =
            drawingInfoMutableLiveData.value.copy(
                cellSizePx = cellSizePx,
                cellCharOffsetPx = cellCharOffsetPx,
                font = "normal normal normal ${fontSize.px} $DEFAULT_FONT",
                fontSize = fontSize
            )
    }

    fun setSize(widthPx: Int, heightPx: Int) {
        drawingInfoMutableLiveData.value =
            drawingInfoMutableLiveData.value.copy(canvasSizePx = Size(widthPx, heightPx))
    }

    fun setOffset(offset: Point) {
        drawingInfoMutableLiveData.value = drawingInfoMutableLiveData.value.copy(offsetPx = offset)
    }

    private fun CanvasRenderingContext2D.getCellSizePx(fontSize: Int): SizeF {
        context.font = font
        context.textAlign = CanvasTextAlign.LEFT
        context.textBaseline = CanvasTextBaseline.MIDDLE
        val cWidth = floor(fontSize.toDouble() * 0.63)
        val cHeight = fontSize.toDouble() * 1.312
        return SizeF(cWidth, cHeight)
    }

    /**
     * A data class to hold drawing info and provide conversion functions for converting between
     * pixel unit and board unit.
     */
    internal data class DrawingInfo(
        val offsetPx: Point = Point.ZERO,
        val cellSizePx: SizeF = SizeF(1.0, 1.0),
        val cellCharOffsetPx: SizeF = SizeF(0.0, 0.0),
        val canvasSizePx: Size = Size(1, 1),
        val font: String = "",
        val fontSize: Int = 0
    ) {
        val boundPx: Rect = Rect(offsetPx, canvasSizePx)

        private val boardOffsetRow: Int = (-offsetPx.top / cellSizePx.height).toInt()
        private val boardOffsetColumn: Int = (-offsetPx.left / cellSizePx.width).toInt()
        private val rowCount: Int = ceil(canvasSizePx.height / cellSizePx.height).toInt()
        private val columnCount: Int = ceil(canvasSizePx.width / cellSizePx.width).toInt()

        val boardBound: Rect = Rect.byLTWH(boardOffsetColumn, boardOffsetRow, columnCount, rowCount)

        internal val boardRowRange: IntRange = boardOffsetRow..(boardOffsetRow + rowCount)
        internal val boardColumnRange: IntRange =
            boardOffsetColumn..(boardOffsetColumn + columnCount)

        /**
         * Converts the board column to pixel X coordinate.
         * Algorithm:
         * 1. Multiply the board column by the cell width.
         * 2. Add the left offset.
         */
        fun toXPx(column: Double): Double = floor(offsetPx.left + cellSizePx.width * column)

        /**
         * Converts the board row to pixel Y coordinate.
         * Algorithm:
         * 1. Multiply the board row by the cell height.
         * 2. Add the top offset.
         */
        fun toYPx(row: Double): Double = floor(offsetPx.top + cellSizePx.height * row)

        /**
         * Converts the screen Y coordinate (pixel) to the board row.
         * Algorithm:
         * 1. Subtract the top offset from the Y coordinate.
         * 2. Divide the result by the cell height.
         */
        fun toBoardRow(yPx: Int): Int = floor((yPx - offsetPx.top) / cellSizePx.height).toInt()

        /**
         * Converts the screen X coordinate (pixel) to the board column.
         * Algorithm:
         * 1. Subtract the left offset from the X coordinate.
         * 2. Divide the result by the cell width.
         */
        fun toBoardColumn(xPx: Int): Int = floor((xPx - offsetPx.left) / cellSizePx.width).toInt()

        /**
         * Converts the width in the board unit to pixel unit.
         */
        fun toWidthPx(width: Double) = floor(cellSizePx.width * width)

        /**
         * Converts the height in the board unit to pixel unit.
         */
        fun toHeightPx(height: Double) = floor(cellSizePx.height * height)
    }

    companion object {
        const val DEFAULT_FONT = "'Jetbrains Mono'"
        private const val CLASS_NAME = "drawing-info"
    }
}
