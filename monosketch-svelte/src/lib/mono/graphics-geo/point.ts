import type { Comparable } from '$mono/base-interface/comparable';

export class Point implements Comparable {
    static readonly ZERO = new Point(0, 0);

    static of(left: number, top: number): Point {
        return new Point(left, top);
    }

    static from(point: { left: number; top: number }): Point {
        return new Point(point.left, point.top);
    }

    constructor(
        public readonly left: number,
        public readonly top: number,
    ) {}

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
        return new Point(this.left - other.left, this.top - other.top);
    }

    /**
     * Returns a new point that is the sum of this point and the other point.
     */
    plus(other: Point): Point {
        return new Point(this.left + other.left, this.top + other.top);
    }

    toString(): string {
        return `${this.left}|${this.top}`;
    }

    // TODO: implement serialize and deserialize
}

export enum Direction {
    HORIZONTAL,
    VERTICAL,
}

export class DirectedPoint implements Comparable {
    static of(direction: Direction, left: number, top: number): DirectedPoint {
        return new DirectedPoint(direction, new Point(left, top));
    }

    constructor(
        public readonly direction: Direction,
        public readonly point: Point,
    ) {}

    get left(): number {
        return this.point.left;
    }

    get top(): number {
        return this.point.top;
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

    // TODO: implement serialize and deserialize
}
