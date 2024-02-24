import { HighlightType, Pixel } from '$mono/monobitmap/board/pixel';
import { Rect } from '$libs/graphics-geo/rect';
import { list } from '$libs/sequence';
import { Size } from '$libs/graphics-geo/size';
import type { Point } from '$libs/graphics-geo/point';
import { MonoBitmap } from '$mono/monobitmap/bitmap/monobitmap';
import { isHalfTransparentChar } from '$mono/common/character';
import type { Char } from '$libs/char';
import { isConnectableChar } from '$mono/monobitmap/board/crosssing-resources';
import type { CrossPoint } from '$mono/monobitmap/board/cross-point';

/**
 * A model class to manage drawn pixel.
 * This is where a pixel is represented with its absolute position.
 */
export class PainterBoard {
    private readonly matrix: Pixel[][];

    constructor(private bound: Rect) {
        this.matrix = list(bound.height, () => list(bound.width, () => new Pixel()));
    }

    getBound = (): Rect => this.bound;

    clear = () => {
        for (let row of this.matrix) {
            for (let pixel of row) {
                pixel.reset();
            }
        }
    };

    /**
     * Fills with another [PainterBoard].
     * If a pixel in input [PainterBoard] is transparent, the value in the current board at that
     * position won't be overwritten.
     */
    fill = (board: PainterBoard) => {
        if (this.matrix.length == 0 || this.matrix[0].length == 0) {
            return;
        }

        const position = board.bound.position;
        const inMatrix = board.matrix;

        const inMatrixBound = new Rect(position, Size.of(inMatrix[0].length, inMatrix.length));

        const overlap = this.bound.getOverlappedRect(inMatrixBound);
        if (!overlap) {
            return;
        }
        const { left: startCol, top: startRow } = overlap.position.minus(this.bound.position);
        const { left: inStartCol, top: inStartRow } = overlap.position.minus(position);

        for (let r = 0; r < overlap.height; r++) {
            const src = inMatrix[inStartRow + r];
            const dest = this.matrix[startRow + r];

            for (let c = 0; c < overlap.width; c++) {
                const px = src[inStartCol + c];
                if (!px.isTransparent) {
                    dest[startCol + c].set(px.visualChar, px.directionChar, px.highlight);
                }
            }
        }
    };

    /**
     * Fills with a bitmap and the highlight state of that bitmap from [position] excepts crossing
     * points. Connection point are the point having the char which is connectable
     * ([CrossingResources.isConnectable]) and there is a character drawn at the position.
     * A list of [CrossPoint] will be returned to let [MonoBoard] able to adjust and draw the
     * adjusted character of the connection points.
     *
     * The main reason why it is required to let [MonoBoard] draws the connection points is the
     * painter board cannot see the pixel outside its bound which is required to identify the final
     * connection character.
     *
     * If a pixel in input [bitmap] is transparent, the value in the current board at that
     * position won't be overwritten.
     */
    fillBitmap = (
        position: Point,
        bitmap: MonoBitmap.Bitmap,
        highlight: HighlightType,
    ): Array<CrossPoint> => {
        if (bitmap.isEmpty()) {
            return [];
        }
        const inMatrix = bitmap.matrix;
        const inMatrixBound = new Rect(position, bitmap.size);

        const overlap = this.bound.getOverlappedRect(inMatrixBound);
        if (!overlap) {
            return [];
        }

        const { left: startCol, top: startRow } = overlap.position.minus(this.bound.position);
        const { left: inStartCol, top: inStartRow } = overlap.position.minus(position);
        const { left: boundColumn, top: boundRow } = this.bound.position;

        const crossPoints: CrossPoint[] = [];

        for (let r = 0; r < overlap.height; r++) {
            const bitmapRow = inStartRow + r;
            const painterRow = startRow + r;
            const src = inMatrix[bitmapRow];
            const dest = this.matrix[painterRow];

            for (let cell of src.asSequence(inStartCol, inStartCol + overlap.width)) {
                const index = cell.index - inStartCol;
                const bitmapColumn = inStartCol + index;
                const painterColumn = startCol + index;
                const pixel = dest[painterColumn];

                if (this.isApplicable(pixel, cell.visual)) {
                    // Not drawing half transparent character
                    // (full transparent character is removed by bitmap)
                    if (!isHalfTransparentChar(cell.visual)) {
                        pixel.set(cell.visual, cell.direction, highlight);
                    }
                } else {
                    // Crossing points will be drawn after finishing drawing all pixels of the
                    // bitmap on the Mono Board. Each unit painter board does not have enough
                    // information to decide the value of the crossing point.
                    crossPoints.push({
                        boardRow: painterRow + boundRow,
                        boardColumn: painterColumn + boundColumn,
                        visualChar: cell.visual,
                        directionChar: cell.direction,
                        leftChar: bitmap.getDirection(bitmapRow, bitmapColumn - 1),
                        rightChar: bitmap.getDirection(bitmapRow, bitmapColumn + 1),
                        topChar: bitmap.getDirection(bitmapRow - 1, bitmapColumn),
                        bottomChar: bitmap.getDirection(bitmapRow + 1, bitmapColumn),
                    });
                }
            }
        }

        return crossPoints;
    };

    /**
     * Force values overlap with [rect] to be [char] regardless they are [TRANSPARENT_CHAR].
     *
     * Note: This method is for testing only
     */
    fillRect = (rect: Rect, char: Char, highlight: HighlightType) => {
        const overlap = this.bound.getOverlappedRect(rect);
        if (!overlap) {
            return;
        }
        const { left: startCol, top: startRow } = overlap.position.minus(this.bound.position);

        for (let r = 0; r < overlap.height; r++) {
            const row = this.matrix[startRow + r];
            for (let c = 0; c < overlap.width; c++) {
                const pixel = row[startCol + c];
                pixel.set(char, char, highlight);
            }
        }
    };

    /**
     * Force value at [position] to be [char] with [highlight].
     *
     * Note: This method is for testing only
     */
    setPoint = (position: Point, char: Char, highlight: HighlightType) => {
        const columnIndex = position.left - this.bound.left;
        const rowIndex = position.top - this.bound.top;

        if (
            columnIndex < 0 ||
            columnIndex >= this.bound.width ||
            rowIndex < 0 ||
            rowIndex >= this.bound.height
        ) {
            return;
        }
        this.matrix[rowIndex][columnIndex].set(char, char, highlight);
    };

    get = (left: number, top: number): Pixel | null => {
        const columnIndex = left - this.bound.left;
        const rowIndex = top - this.bound.top;
        if (
            columnIndex < 0 ||
            columnIndex >= this.bound.width ||
            rowIndex < 0 ||
            rowIndex >= this.bound.height
        ) {
            return null;
        }
        return this.matrix[rowIndex][columnIndex];
    };

    private isApplicable = (pixel: Pixel, visual: Char): boolean => {
        if (pixel.isTransparent) {
            return true;
        }
        if (pixel.visualChar === visual) {
            return true;
        }
        return !isConnectableChar(pixel.visualChar);
    };

    toString = (): string => {
        let result = '';
        for (let row of this.matrix) {
            for (let pixel of row) {
                result += pixel.toString();
            }
            result += '\n';
        }
        return result;
    };
}
