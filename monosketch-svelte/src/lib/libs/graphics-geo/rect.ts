import { type IPoint, Point } from './point';
import { Size } from './size';
import { IntRange } from '$libs/sequence';
import type { Comparable } from '$libs/comparable';

export class Rect implements Comparable {
    static readonly ZERO = Rect.byLTWH(0, 0, 0, 0);

    static byLTRB(left: number, top: number, right: number, bottom: number): Rect {
        [left, right] = [Math.min(left, right), Math.max(left, right)];
        [top, bottom] = [Math.min(top, bottom), Math.max(top, bottom)];
        return new Rect(Point.of(left, top), Size.of(right - left + 1, bottom - top + 1));
    }

    static byLTRBf(left: number, top: number, right: number, bottom: number): Rect {
        [left, right] = [Math.min(left, right), Math.max(left, right)];
        [top, bottom] = [Math.min(top, bottom), Math.max(top, bottom)];
        return new Rect(Point.ofF(left, top), Size.ofF(right - left + 1, bottom - top + 1));
    }

    static byLTWH(left: number, top: number, width: number, height: number): Rect {
        return new Rect(Point.of(left, top), Size.of(width, height));
    }

    static byLTWHf(left: number, top: number, width: number, height: number): Rect {
        return new Rect(Point.ofF(left, top), Size.ofF(width, height));
    }

    private readonly validHorizontalRange: IntRange;
    private readonly validVerticalRange: IntRange;

    constructor(
        public readonly position: Point,
        public readonly size: Size,
    ) {
        this.validHorizontalRange = new IntRange(this.left, this.right + 1);
        this.validVerticalRange = new IntRange(this.top, this.bottom + 1);
    }

    get width(): number {
        return this.size.width;
    }

    get height(): number {
        return this.size.height;
    }

    get left(): number {
        return this.position.left;
    }

    get top(): number {
        return this.position.top;
    }

    get right(): number {
        return this.left + this.width - 1;
    }

    get bottom(): number {
        return this.top + this.height - 1;
    }

    equals(other: unknown): boolean {
        if (!(other instanceof Rect)) {
            return false;
        }
        return this.position.equals(other.position) && this.size.equals(other.size);
    }

    contains(point: IPoint): boolean {
        return (
            this.validHorizontalRange.contains(point.left) &&
            this.validVerticalRange.contains(point.top)
        );
    }

    isOverlapped(other: Rect): boolean {
        const isHorizontalOverlapped =
            this.validHorizontalRange.contains(other.left) ||
            other.validHorizontalRange.contains(this.left);
        const isVerticalOverlapped =
            this.validVerticalRange.contains(other.top) ||
            other.validVerticalRange.contains(this.top);

        return isHorizontalOverlapped && isVerticalOverlapped;
    }

    getOverlappedRect(rect: Rect): Rect | null {
        if (!this.isOverlapped(rect)) {
            return null;
        }
        const offset = rect.position.minus(this.position);
        const top = Math.max(offset.top, 0);
        const bottom = Math.min(offset.top + rect.height, this.height) - 1;
        const left = Math.max(offset.left, 0);
        const right = Math.min(offset.left + rect.width, this.width) - 1;
        return Rect.byLTRB(
            left + this.position.left,
            top + this.position.top,
            right + this.position.left,
            bottom + this.position.top,
        );
    }

    /**
     * Returns true if the point is one of its vertices.
     */
    isVertex(point: Point): boolean {
        const isHorizontalVertex = point.left === this.left || point.left === this.right;
        const isVerticalVertex = point.top === this.top || point.top === this.bottom;
        return isHorizontalVertex && isVerticalVertex;
    }

    toString(): string {
        return `[${this.left}, ${this.top}] - [${this.width} x ${this.height}]`;
    }

    // TODO: implement serialize and deserialize
}
