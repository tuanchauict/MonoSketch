/*
 * Copyright (c) 2024, tuanchauict
 */

import { describe, expect, it } from "vitest";
import { SerializableLineConnector } from "./connector";
import { LineAnchor } from "$mono/shape/shape/linehelper";
import { Point, PointF } from "$libs/graphics-geo/point";

describe("SerializableLineConnector", () => {
    it("should serialize to JSON correctly", () => {
        const original = SerializableLineConnector.create({
            lineId: "line-1",
            anchor: LineAnchor.START,
            targetId: "shape-1",
            ratio: new PointF(0.5, 0.5),
            offset: new Point(10, 20),
        });

        // @ts-ignore
        const json = original.toJson();
        console.log(json);
        expect(json).toStrictEqual({ i: 'line-1', a: 1, t: 'shape-1', r: '0.5|0.5', o: '10|20' })
    });

    it("should deserialize from JSON correctly", () => {
        const json = { i: 'line-2', a: 1, t: 'shape-2', r: '0.25|0.25', o: '30|40' };
        // @ts-ignore
        const deserialized = SerializableLineConnector.fromJson(json);

        expect(deserialized).toStrictEqual(
            SerializableLineConnector.create(
                {
                    lineId: "line-2",
                    anchor: LineAnchor.START,
                    targetId: "shape-2",
                    ratio: new PointF(0.25, 0.25),
                    offset: new Point(30, 40),
                }
            )
        )
    });
});