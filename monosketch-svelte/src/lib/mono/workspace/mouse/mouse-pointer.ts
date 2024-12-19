import { Point, PointF } from '$libs/graphics-geo/point';
import type { Comparable } from '$libs/comparable';

export enum MousePointerType {
    IDLE,
    MOVE,
    DOWN,
    UP,
    DRAG,
    CLICK,
    DOUBLE_CLICK,
}

export interface MousePointerInfo extends Comparable {
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

    equals(other: unknown): boolean {
        return (
            isMousePointerInfo(other) &&
            this.type === other.type &&
            this.boardCoordinate.equals(other.boardCoordinate) &&
            this.clientCoordinate.equals(other.clientCoordinate) &&
            this.boardCoordinateF.equals(other.boardCoordinateF) &&
            this.mouseDownBoardCoordinate.equals(other.mouseDownBoardCoordinate) &&
            this.isWithShiftKey === other.isWithShiftKey
        );
    },
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
        boardCoordinateF: PointF,
        mouseDownBoardCoordinate: Point,
        isWithShiftKey: boolean,
    ) => {
        return {
            ...MousePointerInfoDefaults,
            type: MousePointerType.UP,
            boardCoordinate,
            boardCoordinateF,
            mouseDownBoardCoordinate,
            isWithShiftKey,
        };
    },

    drag: (
        boardCoordinate: Point,
        boardCoordinateF: PointF,
        mouseDownBoardCoordinate: Point,
        isWithShiftKey: boolean,
    ) => {
        return {
            ...MousePointerInfoDefaults,
            type: MousePointerType.DRAG,
            boardCoordinate,
            boardCoordinateF,
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

const isMousePointerInfo = (value: unknown): value is MousePointerInfo =>
    typeof value === 'object' &&
    value !== null &&
    'type' in value &&
    (value as MousePointerInfo).type in MousePointerType;
