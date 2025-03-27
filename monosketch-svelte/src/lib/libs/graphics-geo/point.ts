import type { Comparable } from '$libs/comparable';

export interface IPoint extends Comparable {
    left: number;
    top: number;
    row: number;
    column: number;
}

/**
 * A point class with integer location.
 */
export class Point implements IPoint {
    static readonly ZERO = Point.of(0, 0);

    static of(left: number, top: number): Point {
        return new Point(left, top);
    }

    static ofF(left: number, top: number): Point {
        return new Point(Math.trunc(left), Math.trunc(top));
    }

    static from(point: { left: number; top: number }): Point {
        return Point.of(point.left, point.top);
    }

    static fromF(point: { left: number; top: number }): Point {
        return Point.ofF(point.left, point.top);
    }

    constructor(
        public readonly left: number,
        public readonly top: number,
    ) {
        if (!(Number.isInteger(left) && Number.isInteger(top))) {
            throw Error(`location must be integer (${left}, ${top})`);
        }
    }

    get row(): number {
        return this.top;
    }

    get column(): number {
        return this.left;
    }

    equals(other: unknown): boolean {
        if (!(other instanceof Point)) {
            return false;
        }
        return this.left === other.left && this.top === other.top;
    }

    /**
     * Returns a new point that is the difference between this point and the
     * other point.
     */
    minus(other: Point): Point {
        return Point.of(this.left - other.left, this.top - other.top);
    }

    /**
     * Returns a new point that is the sum of this point and the other point.
     */
    plus(other: Point): Point {
        return Point.of(this.left + other.left, this.top + other.top);
    }

    /**
     * Returns a new point with updated properties.
     */
    copy({ left = this.left, top = this.top }: { left?: number; top?: number } = {}): Point {
        return new Point(left, top);
    }

    toString(): string {
        return `${this.left}|${this.top}`;
    }
}

export enum Direction {
    HORIZONTAL,
    VERTICAL,
}

export namespace Direction {
    export function normalizedDirection(direction: Direction): Direction {
        return direction === Direction.HORIZONTAL ? Direction.HORIZONTAL : Direction.VERTICAL;
    }
}

export class DirectedPoint implements Comparable {
    static of(direction: Direction, left: number, top: number): DirectedPoint {
        return new DirectedPoint(direction, new Point(left, top));
    }

    static ofF(direction: Direction, left: number, top: number): DirectedPoint {
        return new DirectedPoint(direction, new Point(Math.trunc(left), Math.trunc(top)));
    }

    constructor(
        public readonly direction: Direction,
        public readonly point: Point,
    ) {
    }

    get left(): number {
        return this.point.left;
    }

    get top(): number {
        return this.point.top;
    }

    copy({ direction = this.direction, point = this.point }: {
        direction?: Direction;
        point?: Point
    } = {}): DirectedPoint {
        return new DirectedPoint(direction, point);
    }

    equals(other: unknown): boolean {
        if (!(other instanceof DirectedPoint)) {
            return false;
        }
        return this.direction === other.direction && this.point.equals(other.point);
    }

    /**
     * Returns a new directed point that has the point is sum of this point and
     * the base point.
     */
    plus(base: Point): DirectedPoint {
        return new DirectedPoint(this.direction, this.point.plus(base));
    }

    static ZERO = new DirectedPoint(Direction.HORIZONTAL, Point.ZERO);
}

/**
 * A point class that represents a point in 2D space whose values are in float number.
 * This class is only used for calculation, should not use for serialization or storage.
 */
export class PointF implements IPoint {
    constructor(
        public left: number,
        public top: number,
    ) {
    }

    static create({ left, top }: { left: number; top: number }): PointF {
        return new PointF(left, top);
    }

    get row(): number {
        return this.top;
    }

    get column(): number {
        return this.left;
    }

    equals(other: unknown): boolean {
        if (!(other instanceof PointF)) {
            return false;
        }
        return this.left === other.left && this.top === other.top;
    }

    static ZERO = new PointF(0, 0);
}
