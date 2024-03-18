import { TODO } from "$libs/todo";

/**
 * An interface for extra properties of a shape.
 */
export interface ShapeExtra {
}

export const NoExtra: ShapeExtra = {};

/**
 * A {@link ShapeExtra} for a line.
 */
export class LineExtra implements ShapeExtra {
    constructor() {
        TODO();
    }
}

/**
 * A {@link ShapeExtra} for a rectangle box.
 */
export class RectangleExtra implements ShapeExtra {
    constructor() {
        TODO();
    }
}

/**
 * A {@link ShapeExtra} for a text.
 */
export class TextExtra implements ShapeExtra {
    constructor() {
        TODO();
    }
}
