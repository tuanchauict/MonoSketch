import { Point } from '$libs/graphics-geo/point';
import { Size, SizeF } from '$libs/graphics-geo/size';
import { Rect } from '$libs/graphics-geo/rect';
import { IntRange } from '$libs/sequence';

/**
 * A class to hold drawing info and provide conversion functions for converting between
 * pixel unit and board unit.
 */
export class DrawingInfo {
    readonly boundPx: Rect;

    readonly boardOffsetRow: number;
    readonly boardOffsetColumn: number;
    readonly rowCount: number;
    readonly columnCount: number;
    readonly boardBound: Rect;
    readonly boardRowRange: IntRange;
    readonly boardColumnRange: IntRange;

    constructor(
        public readonly offsetPx: Point = Point.ZERO,
        public readonly cellSizePx: SizeF = new SizeF(1.0, 1.0),
        public readonly cellCharOffsetPx: SizeF = new SizeF(0.0, 0.0),
        public readonly canvasSizePx: Size = new Size(1, 1),
        public readonly font: string = '',
        public readonly fontSize: number = 0,
    ) {
        this.boundPx = new Rect(this.offsetPx, this.canvasSizePx);

        this.boardOffsetRow = floor(-this.offsetPx.top / this.cellSizePx.height);
        this.boardOffsetColumn = floor(-this.offsetPx.left / this.cellSizePx.width);
        this.rowCount = ceil(this.canvasSizePx.height / this.cellSizePx.height);
        this.columnCount = ceil(this.canvasSizePx.width / this.cellSizePx.width);

        this.boardBound = Rect.byLTWH(
            this.boardOffsetColumn,
            this.boardOffsetRow,
            this.columnCount,
            this.rowCount,
        );

        this.boardRowRange = new IntRange(this.boardOffsetRow, this.boardOffsetRow + this.rowCount);
        this.boardColumnRange = new IntRange(
            this.boardOffsetColumn,
            this.boardOffsetColumn + this.columnCount,
        );
    }

    copyWith = ({
        offsetPx,
        cellSizePx,
        cellCharOffsetPx,
        canvasSizePx,
        font,
        fontSize,
    }: {
        offsetPx?: Point;
        cellSizePx?: SizeF;
        cellCharOffsetPx?: SizeF;
        canvasSizePx?: Size;
        font?: string;
        fontSize?: number;
    }): DrawingInfo =>
        new DrawingInfo(
            offsetPx ?? this.offsetPx,
            cellSizePx ?? this.cellSizePx,
            cellCharOffsetPx ?? this.cellCharOffsetPx,
            canvasSizePx ?? this.canvasSizePx,
            font ?? this.font,
            fontSize ?? this.fontSize,
        );

    /**
     * Converts the board column to pixel X coordinate.
     * Algorithm:
     * 1. Multiply the board column by the cell width.
     * 2. Add the left offset.
     */
    toXPx = (column: number): number => floor(this.offsetPx.left + this.cellSizePx.width * column);

    /**
     * Converts the board row to pixel Y coordinate.
     * Algorithm:
     * 1. Multiply the board row by the cell height.
     * 2. Add the top offset.
     */
    toYPx = (row: number): number => floor(this.offsetPx.top + this.cellSizePx.height * row);

    /**
     * Converts the screen Y coordinate (pixel) to the board row.
     * Algorithm:
     * 1. Subtract the top offset from the Y coordinate.
     * 2. Divide the result by the cell height.
     */
    toBoardRow = (yPx: number): number => floor((yPx - this.offsetPx.top) / this.cellSizePx.height);

    /**
     * Converts the screen X coordinate (pixel) to the board column.
     * Algorithm:
     * 1. Subtract the left offset from the X coordinate.
     * 2. Divide the result by the cell width.
     */
    toBoardColumn = (xPx: number): number =>
        floor((xPx - this.offsetPx.left) / this.cellSizePx.width);

    /**
     * Converts the pixel Y coordinate (pixel) to the board row with 1 decimal place.
     * Algorithm:
     * 1. Subtract the top offset from the Y coordinate.
     * 2. Divide the result by the cell height.
     */
    toBoardRowF = (yPx: number): number =>
        roundTo1DecimalPlace((yPx - this.offsetPx.top) / this.cellSizePx.height);

    /**
     * Converts the screen X coordinate (pixel) to the board column with 1 decimal place.
     * Algorithm:
     * 1. Subtract the left offset from the X coordinate.
     * 2. Divide the result by the cell width.
     */
    toBoardColumnF = (xPx: number): number =>
        roundTo1DecimalPlace((xPx - this.offsetPx.left) / this.cellSizePx.width);

    /**
     * Converts the width in the board unit to pixel unit.
     */
    toWidthPx = (width: number): number => floor(this.cellSizePx.width * width);

    /**
     * Converts the height in the board unit to pixel unit.
     */
    toHeightPx = (height: number): number => floor(this.cellSizePx.height * height);
}

const floor = Math.floor;
const ceil = Math.ceil;
const roundTo1DecimalPlace = (value: number): number => Math.round(value * 10) / 10;
