import { HighlightType, Pixel } from '$mono/monobitmap/board/pixel';
import { PainterBoard } from '$mono/monobitmap/board/painter-board';
import { Size } from '$libs/graphics-geo/size';
import { Rect } from '$libs/graphics-geo/rect';
import { MapExt } from '$libs/sequence';
import { Point } from '$libs/graphics-geo/point';
import { MonoBitmap } from '$mono/monobitmap/bitmap/monobitmap';
import type { CrossPoint } from '$mono/monobitmap/board/cross-point';

const STANDARD_UNIT_SIZE = Size.of(16, 16);

/**
 * An interface for the board of the Mono Bitmap.
 */
export class MonoBoard {
    private readonly painterBoards: Map<BoardAddress, PainterBoard> = new Map();
    private windowBound: Rect = Rect.ZERO;

    constructor(private readonly unitSize: Size = STANDARD_UNIT_SIZE) {}

    clearAndSetWindow = (bound: Rect) => {
        this.windowBound = bound;
        this.painterBoards.clear();
        const affectedBoards = this.getOrCreateOverlappedBoards(bound, false);
        for (let board of affectedBoards) {
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
    }

    private drawCrossingPoints = (crossingPoints: CrossPoint[], highlight: HighlightType) => {
        // TODO: implement this method
    }

    get(left: number, top: number): Pixel {
        const address = this.toBoardAddress(left, top);
        const board = this.painterBoards.get(address);
        const pixel = board ? board.get(left, top) : null;
        return pixel ? pixel : Pixel.TRANSPARENT;
    }

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
                addressMap.get(rowIndex)!!.set(columnIndex, { rowIndex, columnIndex });
            }
        }
    }

    static get(rowIndex: number, columnIndex: number): BoardAddress {
        const addressMap = BoardAddressManager.addressMap;
        if (!addressMap.has(rowIndex)) {
            addressMap.set(rowIndex, new Map<number, BoardAddress>());
        }
        const row = addressMap.get(rowIndex)!!;
        if (!row.has(columnIndex)) {
            row.set(columnIndex, { rowIndex, columnIndex });
        }
        return row.get(columnIndex)!!;
    }
}
