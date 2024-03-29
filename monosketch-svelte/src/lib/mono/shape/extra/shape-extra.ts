import type { Comparable } from "$libs/comparable";
import { TODO } from "$libs/todo";
import type { SerializableRectExtra } from "$mono/shape/serialization/serializable-shape";

/**
 * An interface for extra properties of a shape.
 */
export interface ShapeExtra extends Comparable{
}

export const NoExtra: ShapeExtra = {
    equals(other: unknown): boolean {
        return other === NoExtra;
    }
};

/**
 * A {@link ShapeExtra} for a line.
 */
export class LineExtra implements ShapeExtra {
    constructor() {
        TODO();
    }

    equals(other: unknown): boolean {
        TODO("Method not implemented.");
        return false;
    }
}

/**
 * A {@link ShapeExtra} for a rectangle box.
 */
export class RectangleExtra implements ShapeExtra, Comparable{
    constructor() {
        TODO();
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    equals(other: unknown): boolean {
        TODO("Method not implemented.");
        return false;
    }

    toSerializableExtra(): SerializableRectExtra {
        throw new Error("Method not implemented.");
    }

    static fromSerializable(extra: SerializableRectExtra): RectangleExtra {
        return new RectangleExtra();
    }
}

/**
 * A {@link ShapeExtra} for a text.
 */
export class TextExtra implements ShapeExtra {
    constructor() {
        TODO();
    }

    equals(other: unknown): boolean {
        TODO("Method not implemented.");
        return false;
    }
}
