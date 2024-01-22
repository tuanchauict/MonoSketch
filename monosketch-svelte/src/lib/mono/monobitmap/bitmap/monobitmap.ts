import { Size } from '$libs/graphics-geo/size';
import { type Char } from '$libs/char';
import { binarySearch, getOrNull, mapIndexedNotNull, zip } from '$libs/sequence';
import { isHalfTransparentChar, isTransparentChar, TRANSPARENT_CHAR } from '$mono/common/character';
import { Rect } from '$libs/graphics-geo/rect';

namespace MonoBitmap {
    export class MonoBitmap {
        readonly size: Size;

        constructor(public matrix: Row[]) {
            if (matrix.length === 0) {
                this.size = Size.ZERO;
            } else {
                this.size = Size.of(matrix[0].size, matrix.length);
            }
        }

        isEmpty = (): boolean => this.size.equals(Size.ZERO);

        getVisual = (row: number, column: number): Char =>
            !this.isEmpty() ? this.matrix[row].getVisual(column) : TRANSPARENT_CHAR;

        getDirection = (row: number, column: number): Char =>
            !this.isEmpty() ? this.matrix[row].getDirection(column) : TRANSPARENT_CHAR;
    }

    export class Builder {
        private readonly bound: Rect;
        private readonly visualMatrix: Char[][];
        /**
         * A matrix for storing visual characters.
         * Except for crossing points, which is identified after combining some information from
         * the mono board, most of the visual characters will be displayed on the canvas.
         */
        private readonly directionMatrix: Char[][];

        constructor(
            private width: number,
            private height: number,
        ) {
            this.bound = Rect.byLTWH(0, 0, width, height);

            this.visualMatrix = Array.from({ length: height }, () =>
                Array.from({ length: width }, () => TRANSPARENT_CHAR),
            );

            this.directionMatrix = Array.from({ length: height }, () =>
                Array.from({ length: width }, () => TRANSPARENT_CHAR),
            );
        }

        put(row: number, column: number, visualChar: Char, directionChar: Char): void {
            if (row >= 0 && row < this.height && column >= 0 && column < this.width) {
                this.visualMatrix[row][column] = visualChar;

                // The direction char is only overridden with non-transparent chars.
                if (directionChar !== TRANSPARENT_CHAR) {
                    this.directionMatrix[row][column] = directionChar;
                }
            }
        }

        fillAll(char: Char): void {
            for (let row = 0; row < this.height; row++) {
                for (let col = 0; col < this.width; col++) {
                    this.visualMatrix[row][col] = char;
                    // TODO: Delegate direction char
                    this.directionMatrix[row][col] = char;
                }
            }
        }

        fillBitmap(row: number, column: number, bitmap: MonoBitmap): void {
            if (bitmap.isEmpty()) {
                return;
            }
            const inMatrix = bitmap.matrix;

            const inMatrixBound = Rect.byLTWH(row, column, bitmap.size.width, bitmap.matrix.length);

            const overlap = this.bound.getOverlappedRect(inMatrixBound);
            if (!overlap) {
                return;
            }
            const { left: startCol, top: startRow } = overlap.position.minus(this.bound.position);
            const { left: inStartCol, top: inStartRow } = overlap.position.minus(
                inMatrixBound.position,
            );

            for (let r = 0; r < overlap.height; r++) {
                const src = inMatrix[inStartRow + r];
                const destVisual = this.visualMatrix[startRow + r];
                const destDirection = this.directionMatrix[startRow + r];

                const updateCell = (index: number, visualChar: Char, directionChar: Char) => {
                    const destIndex = startCol + index;
                    // visualChar from source is always not transparent (0) due to the optimization of Row
                    if (isApplicable(destVisual[destIndex], visualChar)) {
                        destVisual[startCol + index] = visualChar;
                    }

                    // TODO: Double check this condition
                    if (isApplicable(destDirection[destIndex], directionChar)) {
                        destDirection[startCol + index] = directionChar;
                    }
                };

                src.forEachIndex(updateCell, inStartCol, inStartCol + overlap.width);
            }
        }

        toBitmap(): MonoBitmap {
            const rows = this.visualMatrix.map(
                (chars, index) => new Row(chars, this.directionMatrix[index]),
            );
            return new MonoBitmap(rows);
        }
    }

    const isApplicable = (oldChar: Char, newChar: Char): boolean => {
        return (
            (isTransparentChar(oldChar) && isHalfTransparentChar(newChar)) ||
            !isHalfTransparentChar(newChar)
        );
    };

    /**
     * A lightweight data structure to represent a row of the matrix.
     * Only cells that having visible characters will be kept to save the memory. For example, with
     * inputs:
     * ```
     * ab      c
     * ```
     * only `[a, b, c]` is kept.
     */
    class Row {
        readonly size: number;

        /**
         * A list of cells sorted by its [Cell.index].
         */
        private readonly sortedCells: Cell[];

        constructor(visualChars: Char[], directionChars: Char[]) {
            this.size = visualChars.length;

            this.sortedCells = mapIndexedNotNull(
                zip(visualChars, directionChars),
                (index, [vChar, dChar]) => {
                    const isApplicable = !isTransparentChar(vChar) || !isTransparentChar(dChar);
                    return isApplicable ? new Cell(index, vChar, dChar) : null;
                },
            );
        }

        forEachIndex(
            callback: ForEachIndex,
            fromIndex: number = 0,
            toExclusiveIndex: number = this.size,
        ) {
            const foundLow = binarySearch(this.sortedCells, (cell) => cell.index - fromIndex);
            const low = foundLow >= 0 ? foundLow : -foundLow - 1;
            for (let i = low; i < this.sortedCells.length; i++) {
                const cell = this.sortedCells[i];
                if (cell.index >= toExclusiveIndex) {
                    break;
                }
                callback(cell.index, cell.visual, cell.direction);
            }
        }

        getVisual(column: number): Char {
            const cell = this.getCell(column);
            return cell ? cell.visual : TRANSPARENT_CHAR;
        }

        getDirection(column: number): Char {
            const cell = this.getCell(column);
            return cell ? cell.direction : TRANSPARENT_CHAR;
        }

        private getCell(column: number): Cell | null {
            const index = binarySearch(this.sortedCells, (cell) => cell.index - column);
            return getOrNull(this.sortedCells, index);
        }
    }

    class Cell {
        constructor(
            public index: number,
            public visual: Char,
            public direction: Char,
        ) {}
    }

    type ForEachIndex = (index: number, visual: Char, direction: Char) => void;
}