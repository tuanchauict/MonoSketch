/*
 * Copyright (c) 2024, tuanchauict
 */

import { Point, PointF } from "$libs/graphics-geo/point";
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
    }
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
    }
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
    }
}
