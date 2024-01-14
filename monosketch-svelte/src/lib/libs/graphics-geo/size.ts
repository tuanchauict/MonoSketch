import type { Comparable } from '$libs/comparable';

export interface ISize extends Comparable {
    width: number;
    height: number;
}

/**
 * A size class with integer width and height.
 */
export class Size implements ISize {
    static readonly ZERO = new Size(0, 0);

    static of(width: number, height: number): Size {
        return new Size(width, height);
    }

    static from(size: { width: number; height: number }): Size {
        return Size.of(size.width, size.height);
    }

    constructor(
        public readonly width: number,
        public readonly height: number,
    ) {
        if (!(Number.isInteger(width) && Number.isInteger(height))) {
            throw Error('size must be integer');
        }
    }

    equals(other: unknown): boolean {
        if (!(other instanceof Size)) {
            return false;
        }
        return this.width === other.width && this.height === other.height;
    }

    toString() {
        return `Size(${this.width}, ${this.height})`;
    }
}

/**
 * A size class with float width and height.
 */
export class SizeF implements ISize {
    static readonly ZERO = new SizeF(0, 0);

    static of(width: number, height: number): SizeF {
        return new SizeF(width, height);
    }

    static from(size: { width: number; height: number }): SizeF {
        return SizeF.of(size.width, size.height);
    }

    constructor(
        public width: number,
        public height: number,
    ) {}

    equals(other: unknown): boolean {
        if (!(other instanceof SizeF)) {
            return false;
        }
        return this.width === other.width && this.height === other.height;
    }

    toString() {
        return `SizeF(${this.width}, ${this.height})`;
    }
}
