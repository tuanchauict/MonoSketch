import { Point, PointF } from '$libs/graphics-geo/point';

export enum MousePointerType {
    IDLE,
    MOVE,
    DOWN,
    UP,
    DRAG,
    CLICK,
    DOUBLE_CLICK,
}

export type MousePointerIdle = { type: MousePointerType.IDLE };
export type MousePointerMove = { type: MousePointerType.MOVE; boardCoordinate: Point; clientCoordinate: Point };
export type MousePointerDown = {
    type: MousePointerType.DOWN;
    boardCoordinate: Point;
    clientCoordinate: Point;
    isWithShiftKey: boolean
};
export type MousePointerDrag = {
    type: MousePointerType.DRAG;
    boardCoordinate: Point;
    boardCoordinateF: PointF;
    mouseDownBoardCoordinate: Point;
    isWithShiftKey: boolean
};
export type MousePointerUp = {
    type: MousePointerType.UP;
    boardCoordinate: Point;
    boardCoordinateF: PointF;
    mouseDownBoardCoordinate: Point;
    isWithShiftKey: boolean
};
export type MousePointerClick = { type: MousePointerType.CLICK; boardCoordinate: Point; isWithShiftKey: boolean };
export type MousePointerDoubleClick = { type: MousePointerType.DOUBLE_CLICK; boardCoordinate: Point };


/**
 * Type representing different mouse event pointer states
 */
export type MousePointer =
    | MousePointerIdle
    | MousePointerMove
    | MousePointerDown
    | MousePointerUp
    | MousePointerDrag
    | MousePointerClick
    | MousePointerDoubleClick;


export namespace MousePointer {
    export const idle: MousePointerIdle = { type: MousePointerType.IDLE };

    export function move(boardCoordinate: Point, clientCoordinate: Point): MousePointerMove {
        return { type: MousePointerType.MOVE, boardCoordinate, clientCoordinate };
    }

    export function down(boardCoordinate: Point, clientCoordinate: Point, isWithShiftKey: boolean): MousePointerDown {
        return { type: MousePointerType.DOWN, boardCoordinate, clientCoordinate, isWithShiftKey };
    }

    export function up(
        boardCoordinate: Point,
        boardCoordinateF: PointF,
        mouseDownBoardCoordinate: Point,
        isWithShiftKey: boolean,
    ): MousePointerUp {
        return {
            type: MousePointerType.UP,
            boardCoordinate,
            boardCoordinateF,
            mouseDownBoardCoordinate,
            isWithShiftKey,
        };
    }

    export function drag(
        boardCoordinate: Point,
        boardCoordinateF: PointF,
        mouseDownBoardCoordinate: Point,
        isWithShiftKey: boolean,
    ): MousePointerDrag {
        return {
            type: MousePointerType.DRAG,
            boardCoordinate,
            boardCoordinateF,
            mouseDownBoardCoordinate,
            isWithShiftKey,
        };
    }

    export function click(boardCoordinate: Point, isWithShiftKey: boolean): MousePointerClick {
        return { type: MousePointerType.CLICK, boardCoordinate, isWithShiftKey };
    }

    export function doubleClick(boardCoordinate: Point): MousePointerDoubleClick {
        return { type: MousePointerType.DOUBLE_CLICK, boardCoordinate };
    }
}
