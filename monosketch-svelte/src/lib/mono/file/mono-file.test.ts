/*
 * Copyright (c) 2024, tuanchauict
 */

import { Point, PointF } from "$libs/graphics-geo/point";
import { Extra, MonoFile } from "$mono/file/mono-file";
import { SerializableLineConnector } from "$mono/shape/serialization/connector";
import { SerializableGroup, SerializableRectangle } from "$mono/shape/serialization/shapes";
import { describe, expect, test } from "vitest";

describe("MonoFile serialization", () => {
    const root = SerializableGroup.create({
        id: "root",
        isIdTemporary: false,
        versionCode: 1,
        shapes: [
            SerializableRectangle.EMPTY,
            SerializableRectangle.EMPTY,
        ]
    });
    const connectors: SerializableLineConnector[] = [
        SerializableLineConnector.create({
            lineId: "lineId",
            anchor: 0,
            targetId: "targetId",
            ratio: PointF.create({ left: 0.5, top: 0.6 }),
            offset: Point.of(10, 20),
        }),
        SerializableLineConnector.create({
            lineId: "lineId1",
            anchor: 1,
            targetId: "targetId",
            ratio: PointF.create({ left: 0.5, top: 0.6 }),
            offset: Point.of(10, 20),
        }),
    ];
    const extra = Extra.create("Test", Point.of(30, 50));
    const modifiedTimestampMillis = 1000;

    test("should serialize MonoFile correctly", () => {
        const monoFile = MonoFile.create(root, connectors, extra, modifiedTimestampMillis);
        // @ts-expect-error toJson is attached by Jsonizable
        const json = monoFile.toJson();
        expect(json).toEqual({
            // @ts-expect-error toJson is attached by Jsonizable
            root: root.toJson(),
            // @ts-expect-error toJson is attached by Jsonizable
            extra: extra.toJson(),
            version: 2,
            modified_timestamp_millis: modifiedTimestampMillis,
            // @ts-expect-error toJson is attached by Json
            connectors: connectors.map(connector => connector.toJson()),
        });
    });

    test("should deserialize MonoFile correctly", () => {
        const json = {
            // @ts-expect-error toJson is attached by Jsonizable
            root: root.toJson(),
            // @ts-expect-error toJson is attached by Jsonizable
            extra: extra.toJson(),
            version: 2,
            modified_timestamp_millis: modifiedTimestampMillis,
            // @ts-expect-error toJson is attached by Json
            connectors: connectors.map(connector => connector.toJson()),
        };
        // @ts-expect-error fromJson is attached by Jsonizable
        const monoFile = MonoFile.fromJson(json);
        expect(monoFile).toEqual(MonoFile.create(root, connectors, extra, modifiedTimestampMillis));
    });
});