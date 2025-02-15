import { Point } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { LineAnchor } from "$mono/shape/shape/linehelper";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";

/**
 * A sealed class for defining all possible interaction point types for a shape and common apis.
 *
 * [left] and [top] are the center position of the interaction point, with board-related unit.
 */
export abstract class InteractionPoint {
    protected constructor(
        readonly shapeId: string,
        readonly left: number,
        readonly top: number,
        readonly mouseCursor: MouseCursor,
    ) {
    }
}

/**
 * A namespace for defining all possible scale interaction point types for a shape.
 */
export namespace ScaleInteractionPoint {
    export abstract class Base extends InteractionPoint {
        protected constructor(
            shapeId: string,
            left: number,
            top: number,
            mouseCursor: MouseCursor,
        ) {
            super(shapeId, left, top, mouseCursor);
        }

        abstract createNewShapeBound(currentBound: Rect, newPoint: Point): Rect;
    }

    // Helper functions (similar to companion object in Kotlin)
    export function adjustUpperBound(value: number): number {
        return Math.floor(value + 0.8);
    }

    export function adjustLowerBound(value: number): number {
        return Math.floor(value - 0.8);
    }

    export class TopLeft extends Base {
        constructor(shapeId: string, left: number, top: number) {
            super(shapeId, left, top, MouseCursor.RESIZE_NWSE);
        }

        createNewShapeBound(currentBound: Rect, newPoint: Point): Rect {
            return Rect.byLTRB(
                adjustUpperBound(newPoint.left),
                adjustUpperBound(newPoint.top),
                currentBound.right,
                currentBound.bottom,
            );
        }
    }

    export class TopMiddle extends Base {
        constructor(shapeId: string, left: number, top: number) {
            super(shapeId, left, top, MouseCursor.RESIZE_NS);
        }

        createNewShapeBound(currentBound: Rect, newPoint: Point): Rect {
            return Rect.byLTRB(
                currentBound.left,
                adjustUpperBound(newPoint.top),
                currentBound.right,
                currentBound.bottom,
            );
        }
    }

    export class TopRight extends Base {
        constructor(shapeId: string, left: number, top: number) {
            super(shapeId, left, top, MouseCursor.RESIZE_NESW);
        }

        createNewShapeBound(currentBound: Rect, newPoint: Point): Rect {
            return Rect.byLTRB(
                currentBound.left,
                adjustUpperBound(newPoint.top),
                adjustLowerBound(newPoint.left),
                currentBound.bottom,
            );
        }
    }

    export class MiddleLeft extends Base {
        constructor(shapeId: string, left: number, top: number) {
            super(shapeId, left, top, MouseCursor.RESIZE_EW);
        }

        createNewShapeBound(currentBound: Rect, newPoint: Point): Rect {
            return Rect.byLTRB(
                adjustUpperBound(newPoint.left),
                currentBound.top,
                currentBound.right,
                currentBound.bottom,
            );
        }
    }

    export class MiddleRight extends Base {
        constructor(shapeId: string, left: number, top: number) {
            super(shapeId, left, top, MouseCursor.RESIZE_EW);
        }

        createNewShapeBound(currentBound: Rect, newPoint: Point): Rect {
            return Rect.byLTRB(
                currentBound.left,
                currentBound.top,
                adjustLowerBound(newPoint.left),
                currentBound.bottom,
            );
        }
    }

    export class BottomLeft extends Base {
        constructor(shapeId: string, left: number, top: number) {
            super(shapeId, left, top, MouseCursor.RESIZE_NESW);
        }

        createNewShapeBound(currentBound: Rect, newPoint: Point): Rect {
            return Rect.byLTRB(
                adjustUpperBound(newPoint.left),
                currentBound.top,
                currentBound.right,
                adjustLowerBound(newPoint.top),
            );
        }
    }

    export class BottomMiddle extends Base {
        constructor(shapeId: string, left: number, top: number) {
            super(shapeId, left, top, MouseCursor.RESIZE_NS);
        }

        createNewShapeBound(currentBound: Rect, newPoint: Point): Rect {
            return Rect.byLTRB(
                currentBound.left,
                currentBound.top,
                currentBound.right,
                adjustLowerBound(newPoint.top),
            );
        }
    }

    export class BottomRight extends Base {
        constructor(shapeId: string, left: number, top: number) {
            super(shapeId, left, top, MouseCursor.RESIZE_NWSE);
        }

        createNewShapeBound(currentBound: Rect, newPoint: Point): Rect {
            return Rect.byLTRB(
                currentBound.left,
                currentBound.top,
                adjustLowerBound(newPoint.left),
                adjustLowerBound(newPoint.top),
            );
        }
    }
}

/**
 * A namespace for defining all possible scale interaction point types for a line.
 */
export namespace LineInteractionPoint {
    export abstract class Base extends InteractionPoint {
        protected constructor(
            shapeId: string,
            left: number,
            top: number,
            mouseCursor: MouseCursor,
        ) {
            super(shapeId, left, top, mouseCursor);
        }
    }

    export class Anchor extends Base {
        constructor(
            shapeId: string,
            readonly anchor: LineAnchor,
            left: number,
            top: number,
        ) {
            super(shapeId, left, top, MouseCursor.MOVE);
        }
    }

    export class Edge extends Base {
        constructor(
            shapeId: string,
            readonly edgeId: number,
            left: number,
            top: number,
            isHorizontal: boolean,
        ) {
            super(shapeId, left, top, isHorizontal ? MouseCursor.RESIZE_ROW : MouseCursor.RESIZE_COL);
        }
    }
}
