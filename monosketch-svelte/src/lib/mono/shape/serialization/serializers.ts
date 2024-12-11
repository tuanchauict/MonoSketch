/*
 * Copyright (c) 2024, tuanchauict
 */

import { DirectedPoint, Direction, Point, PointF } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { LineAnchor } from "$mono/shape/shape/linehelper";

/**
 * A serializer for [LineAnchor]
 */
export const AnchorSerializer = {
    START_VALUE: 1,
    END_VALUE: 2,

    serialize(value: LineAnchor): number {
        switch (value) {
            case LineAnchor.START:
                return AnchorSerializer.START_VALUE;
            case LineAnchor.END:
                return AnchorSerializer.END_VALUE;
            default:
                throw new Error(`Unrecognizable value ${value}`);
        }
    },

    deserialize(value: number): LineAnchor {
        switch (value) {
            case AnchorSerializer.START_VALUE:
                return LineAnchor.START;
            case AnchorSerializer.END_VALUE:
                return LineAnchor.END;
            default:
                throw new Error(`Unrecognizable value ${value}`);
        }
    },
}

export const PointSerializer = {
    serialize(value: Point): string {
        return `${value.left}|${value.top}`;
    },

    deserialize(value: string): Point {
        const [x, y] = value.split("|").map(Number);
        if (isNaN(x) || isNaN(y)) {
            throw new Error(`Invalid Point format: ${value}`);
        }
        return new Point(x, y);
    },
}

export const PointArraySerializer = {
    serialize(value: Point[]): string[] {
        return value.map(PointSerializer.serialize);
    },

    deserialize(value: string[]): Point[] {
        return value.map(PointSerializer.deserialize);
    },
}

/**
 * A serializer for [PointF]
 */
export const PointFSerializer = {
    serialize(value: PointF): string {
        return `${value.left}|${value.top}`;
    },

    deserialize(value: string): PointF {
        const [x, y] = value.split("|").map(Number);
        if (isNaN(x) || isNaN(y)) {
            throw new Error(`Invalid PointF format: ${value}`);
        }
        return new PointF(x, y);
    },
}

export const DirectedPointSerializer = {
    MARSHAL_HORIZONTAL: "H",
    MARSHAL_VERTICAL: "V",

    serialize(value: DirectedPoint): string {
        const serializedDirection = value.direction === Direction.HORIZONTAL
            ? DirectedPointSerializer.MARSHAL_HORIZONTAL
            : DirectedPointSerializer.MARSHAL_VERTICAL;
        return `${serializedDirection}|${value.point.left}|${value.point.top}`;
    },

    deserialize(value: string): DirectedPoint {
        const [serializedDirection, left, top] = value.split("|");
        const direction = serializedDirection === DirectedPointSerializer.MARSHAL_HORIZONTAL
            ? Direction.HORIZONTAL
            : Direction.VERTICAL;

        return DirectedPoint.of(direction, parseInt(left), parseInt(top));
    },
}

export const RectSerializer = {
    serialize(value: Rect): string {
        return `${value.left}|${value.top}|${value.width}|${value.height}`;
    },

    deserialize(value: string): Rect {
        const [left, top, width, height] = value.split("|").map(Number);
        if (isNaN(left) || isNaN(top) || isNaN(width) || isNaN(width)) {
            throw new Error(`Invalid Rect format: ${value}`);
        }
        return Rect.byLTWH(left, top, width, height);
    },
}