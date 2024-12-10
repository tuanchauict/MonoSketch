/*
 * Copyright (c) 2024, tuanchauict
 */

import { Point, PointF } from "$libs/graphics-geo/point";
import type { LineAnchor } from "$mono/shape/shape/linehelper";

export class SerializableLineConnector {
    constructor(
        public lineId: string,
        public anchor: LineAnchor,
        public targetId: string,
        public ratio: PointF,
        public offset: Point
    ) {}
}