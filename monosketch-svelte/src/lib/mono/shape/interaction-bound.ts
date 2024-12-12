import type { Rect } from '$libs/graphics-geo/rect';
import { MouseCursor } from '$mono/workspace/mouse/cursor-type';

export enum InteractionBoundType {
    LINE,
    SCALABLE_SHAPE,
}

/**
 * An interface to define all possible interaction bound types
 */
export interface InteractionBound {
    readonly type: InteractionBoundType;
    readonly interactionPoints: InteractionPoint[];
}

/**
 * A class which defines interaction bound for scalable shapes.
 */
export class ScalableInteractionBound implements InteractionBound {
    readonly type = InteractionBoundType.SCALABLE_SHAPE;

    constructor(
        public readonly interactionPoints: InteractionPoint[],
        public readonly left: number,
        public readonly top: number,
        public readonly right: number,
        public readonly bottom: number,
    ) {}

    static of = (targetedShapeId: string, shapeBound: Rect): ScalableInteractionBound => {
        const left = shapeBound.left - 0.25;
        const top = shapeBound.top - 0.25;
        const right = shapeBound.right + 0.25;
        const bottom = shapeBound.bottom + 0.25;
        const horizontalMiddle = (left + right) / 2;
        const verticalMiddle = (top + bottom) / 2;
        return new ScalableInteractionBound(
            [
                {
                    shapeId: targetedShapeId,
                    type: InteractionPointType.TOP_LEFT,
                    left: left,
                    top: top,
                },
                {
                    shapeId: targetedShapeId,
                    type: InteractionPointType.TOP_MIDDLE,
                    left: horizontalMiddle,
                    top: top,
                },
                {
                    shapeId: targetedShapeId,
                    type: InteractionPointType.TOP_RIGHT,
                    left: right,
                    top: top,
                },
                {
                    shapeId: targetedShapeId,
                    type: InteractionPointType.MIDDLE_LEFT,
                    left: left,
                    top: verticalMiddle,
                },
                {
                    shapeId: targetedShapeId,
                    type: InteractionPointType.MIDDLE_RIGHT,
                    left: right,
                    top: verticalMiddle,
                },
                {
                    shapeId: targetedShapeId,
                    type: InteractionPointType.BOTTOM_LEFT,
                    left: left,
                    top: bottom,
                },
                {
                    shapeId: targetedShapeId,
                    type: InteractionPointType.BOTTOM_MIDDLE,
                    left: horizontalMiddle,
                    top: bottom,
                },
                {
                    shapeId: targetedShapeId,
                    type: InteractionPointType.BOTTOM_RIGHT,
                    left: right,
                    top: bottom,
                },
            ],
            left,
            top,
            right,
            bottom,
        );
    };
}

export class LineInteractionBound implements InteractionBound {
    readonly type = InteractionBoundType.LINE;

    constructor(public readonly interactionPoints: InteractionPoint[]) {}
}

/**
 * An interface to define the interaction point for a shape and common APIs.
 *
 * @left the center position of the interaction point in the x-axis, in board-related unit.
 * @top the center position of the interaction point in the y-axis, in board-related unit.
 */
export interface InteractionPoint {
    shapeId: string;
    type: InteractionPointType;
    left: number;
    top: number;
}

/**
 * An enum to define the type of the interaction point for a shape.
 */
export enum InteractionPointType {
    TOP_LEFT,
    TOP_MIDDLE,
    TOP_RIGHT,
    MIDDLE_LEFT,
    MIDDLE_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_MIDDLE,
    BOTTOM_RIGHT,
    LINE_HORIZONTAL,
    LINE_VERTICAL,
    LINE_ANCHOR,
}

export const scalableInteractionPointTypeToCursor = (type: InteractionPointType): MouseCursor => {
    switch (type) {
        case InteractionPointType.TOP_LEFT:
        case InteractionPointType.BOTTOM_RIGHT:
            return MouseCursor.RESIZE_NWSE;
        case InteractionPointType.TOP_MIDDLE:
        case InteractionPointType.BOTTOM_MIDDLE:
            return MouseCursor.RESIZE_NS;
        case InteractionPointType.TOP_RIGHT:
        case InteractionPointType.BOTTOM_LEFT:
            return MouseCursor.RESIZE_NESW;
        case InteractionPointType.MIDDLE_LEFT:
        case InteractionPointType.MIDDLE_RIGHT:
            return MouseCursor.RESIZE_EW;
        default:
            return MouseCursor.DEFAULT;
    }
};
