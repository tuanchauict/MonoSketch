import { Point, PointF } from '$mono/graphics-geo/point';

export enum MousePointerType {
    IDLE,
    MOVE,
    DOWN,
    UP,
    DRAG,
    CLICK,
    DOUBLE_CLICK,
}

export interface MousePointerInfo {
    type: MousePointerType;
    boardCoordinate: Point;
    clientCoordinate: Point;
    boardCoordinateF: PointF;
    mouseDownBoardCoordinate: Point;
    isWithShiftKey: boolean;
}

const MousePointerInfoDefaults: MousePointerInfo = {
    type: MousePointerType.IDLE,
    boardCoordinate: Point.ZERO,
    clientCoordinate: Point.ZERO,
    boardCoordinateF: new PointF(0, 0),
    mouseDownBoardCoordinate: Point.ZERO,
    isWithShiftKey: false,
};

const Idle = {
    ...MousePointerInfoDefaults,
};

export const MousePointer = {
    idle: Idle,
    move: (boardCoordinate: Point, clientCoordinate: Point) => {
        return {
            ...MousePointerInfoDefaults,
            type: MousePointerType.MOVE,
            boardCoordinate,
            clientCoordinate,
        };
    },

    down: (boardCoordinate: Point, clientCoordinate: Point, isWithShiftKey: boolean) => {
        return {
            ...MousePointerInfoDefaults,
            type: MousePointerType.DOWN,
            boardCoordinate,
            clientCoordinate,
            isWithShiftKey,
        };
    },

    up: (
        boardCoordinate: Point,
        clientCoordinate: Point,
        mouseDownBoardCoordinate: Point,
        isWithShiftKey: boolean,
    ) => {
        return {
            ...MousePointerInfoDefaults,
            type: MousePointerType.UP,
            boardCoordinate,
            clientCoordinate,
            mouseDownBoardCoordinate,
            isWithShiftKey,
        };
    },

    drag: (
        boardCoordinate: Point,
        clientCoordinate: Point,
        mouseDownBoardCoordinate: Point,
        isWithShiftKey: boolean,
    ) => {
        return {
            ...MousePointerInfoDefaults,
            type: MousePointerType.DRAG,
            boardCoordinate,
            clientCoordinate,
            mouseDownBoardCoordinate,
            isWithShiftKey,
        };
    },

    click: (boardCoordinate: Point, isWithShiftKey: boolean) => {
        return {
            ...MousePointerInfoDefaults,
            type: MousePointerType.CLICK,
            boardCoordinate,
            isWithShiftKey,
        };
    },

    doubleClick: (boardCoordinate: Point) => {
        return {
            ...MousePointerInfoDefaults,
            type: MousePointerType.DOUBLE_CLICK,
            boardCoordinate,
        };
    },
};
