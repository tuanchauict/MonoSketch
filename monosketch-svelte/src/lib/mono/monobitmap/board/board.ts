import { CrossingResources } from "$mono/monobitmap/board/crossing-resources";
import { HighlightType, Pixel } from '$mono/monobitmap/board/pixel';
import { PainterBoard } from '$mono/monobitmap/board/painter-board';
import { Size } from '$libs/graphics-geo/size';
import { Rect } from '$libs/graphics-geo/rect';
import { MapExt } from '$libs/sequence';
import { Point } from '$libs/graphics-geo/point';
import { MonoBitmap } from '$mono/monobitmap/bitmap/monobitmap';
import type { CrossPoint } from '$mono/monobitmap/board/cross-point';
import type { Char } from '$libs/char';
import getCrossingChar = CrossingResources.getCrossingChar;

const STANDARD_UNIT_SIZE = Size.of(16, 16);

/**
 * An interface for the board of the Mono Bitmap.
 */
export class MonoBoard {
    private readonly painterBoards: Map<BoardAddress, PainterBoard> = new Map();
    private windowBound: Rect = Rect.ZERO;

    get boardCount(): number {
        return this.painterBoards.size;
    }

    constructor(private readonly unitSize: Size = STANDARD_UNIT_SIZE) {
    }

    clearAndSetWindow = (bound: Rect) => {
        this.windowBound = bound;
        this.painterBoards.clear();
        const affectedBoards = this.getOrCreateOverlappedBoards(bound, false);
        for (const board of affectedBoards) {
            board.clear();
        }
    };

    fillBitmap = (position: Point, bitmap: MonoBitmap.Bitmap, highlight: HighlightType) => {
        const rect = new Rect(position, bitmap.size);
        const affectedBoards = this.getOrCreateOverlappedBoards(rect, true);

        const crossingPoints: CrossPoint[] = [];
        for (const board of affectedBoards) {
            crossingPoints.push(...board.fillBitmap(position, bitmap, highlight));
        }

        this.drawCrossingPoints(crossingPoints, highlight);
    };

    // This method is for testing only
    fillRect = (rect: Rect, char: Char, highlight: HighlightType) => {
        const affectedBoards = this.getOrCreateOverlappedBoards(rect, true);
        for (const board of affectedBoards) {
            board.fillRect(rect, char, highlight);
        }
    };

    private drawCrossingPoints = (crossingPoints: CrossPoint[], highlight: HighlightType) => {
        for (const charPoint of crossingPoints) {
            const left = charPoint.boardColumn;
            const top = charPoint.boardRow;
            const currentPixel = this.get(left, top);
            const crossingChar = getCrossingChar(
                // Upper
                charPoint.visualChar,
                charPoint.leftChar,
                charPoint.rightChar,
                charPoint.topChar,
                charPoint.bottomChar,
                // Lower
                this.get(left, top).visualChar,
                this.get(left - 1, top).directionChar,
                this.get(left + 1, top).directionChar,
                this.get(left, top - 1).directionChar,
                this.get(left, top + 1).directionChar,
            );
            const visualChar = crossingChar ?? charPoint.visualChar;
            const directionChar = crossingChar ?? charPoint.directionChar;
            currentPixel.set(visualChar, directionChar, highlight);
        }
    };

    getPoint = (position: Point): Pixel => {
        return this.get(position.left, position.top);
    };

    get = (left: number, top: number): Pixel => {
        const address = this.toBoardAddress(left, top);
        const board = this.painterBoards.get(address);
        const pixel = board ? board.get(left, top) : null;
        return pixel ? pixel : Pixel.TRANSPARENT;
    };

    set = (left: number, top: number, char: Char, highlight: HighlightType) => {
        this.getOrCreateBoard(left, top, true)
            ?.setPoint(new Point(left, top), char, highlight);
    };

    setPoint = (position: Point, char: Char, highlight: HighlightType) => {
        this.set(position.left, position.top, char, highlight);
    };

    private getOrCreateOverlappedBoards = (
        rect: Rect,
        isCreatedRequired: boolean,
    ): PainterBoard[] => {
        const affectedBoards: PainterBoard[] = [];
        const { width: unitWidth, height: unitHeight } = this.unitSize;

        const leftIndex = Math.floor(rect.left / unitWidth);
        const rightIndex = Math.floor(rect.right / unitWidth);
        const topIndex = Math.floor(rect.top / unitHeight);
        const bottomIndex = Math.floor(rect.bottom / unitHeight);

        for (let left = leftIndex; left <= rightIndex; left++) {
            for (let top = topIndex; top <= bottomIndex; top++) {
                const board = this.getOrCreateBoard(
                    left * unitWidth,
                    top * unitHeight,
                    isCreatedRequired,
                );

                if (board) {
                    affectedBoards.push(board);
                }
            }
        }

        return affectedBoards;
    };

    private getOrCreateBoard = (
        left: number,
        top: number,
        isCreatedRequired: boolean,
    ): PainterBoard | null => {
        const address = this.toBoardAddress(left, top);
        const board = isCreatedRequired
            ? MapExt.getOrPut(this.painterBoards, address, () => this.createNewBoard(address))
            : this.painterBoards.get(address);
        if (!board) {
            return null;
        }
        return this.windowBound.isOverlapped(board.getBound()) ? board : null;
    };

    private createNewBoard = (address: BoardAddress): PainterBoard => {
        const newBoardPosition = new Point(
            address.columnIndex * this.unitSize.width,
            address.rowIndex * this.unitSize.height,
        );
        const bound = new Rect(newBoardPosition, this.unitSize);
        return new PainterBoard(bound);
    };

    private toBoardAddress = (left: number, top: number): BoardAddress => {
        const rowIndex = Math.floor(top / this.unitSize.height);
        const columnIndex = Math.floor(left / this.unitSize.width);
        return BoardAddressManager.get(rowIndex, columnIndex);
    };

    toString = (): string => {
        if (this.painterBoards.size === 0) {
            return '';
        }
        const left = Math.min(
            ...Array.from(this.painterBoards.keys(), (point) => point.columnIndex),
        );
        const right =
            Math.max(...Array.from(this.painterBoards.keys(), (point) => point.columnIndex)) + 1;
        const top = Math.min(...Array.from(this.painterBoards.keys(), (point) => point.rowIndex));
        const bottom =
            Math.max(...Array.from(this.painterBoards.keys(), (point) => point.rowIndex)) + 1;
        const rect = Rect.byLTWH(
            left * this.unitSize.width,
            top * this.unitSize.height,
            (right - left) * this.unitSize.width,
            (bottom - top) * this.unitSize.height,
        );
        const painterBoard = new PainterBoard(rect);

        Array.from(this.painterBoards.values()).forEach((pb) => painterBoard.fill(pb));

        return painterBoard.toString();
    };

    toStringInBound = (bound: Rect): string => {
        const painterBoard = new PainterBoard(bound);
        Array.from(this.painterBoards.values()).forEach((pb) => painterBoard.fill(pb));
        return painterBoard.toString();
    }
}

type BoardAddress = {
    rowIndex: number;
    columnIndex: number;
};

class BoardAddressManager {
    private static readonly addressMap = new Map<number, Map<number, BoardAddress>>();

    static {
        const addressMap = BoardAddressManager.addressMap;
        for (let rowIndex = -4; rowIndex < 10; rowIndex++) {
            addressMap.set(rowIndex, new Map<number, BoardAddress>());
            for (let columnIndex = -4; columnIndex < 16; columnIndex++) {
                addressMap.get(rowIndex)!.set(columnIndex, { rowIndex, columnIndex });
            }
        }
    }

    static get(rowIndex: number, columnIndex: number): BoardAddress {
        const addressMap = BoardAddressManager.addressMap;
        if (!addressMap.has(rowIndex)) {
            addressMap.set(rowIndex, new Map<number, BoardAddress>());
        }
        const row = addressMap.get(rowIndex)!;
        if (!row.has(columnIndex)) {
            row.set(columnIndex, { rowIndex, columnIndex });
        }
        return row.get(columnIndex)!;
    }
}
