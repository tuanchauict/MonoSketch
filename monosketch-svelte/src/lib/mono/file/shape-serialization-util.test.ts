/*
 * Copyright (c) 2024, tuanchauict
 */

import { describe, expect, test } from "vitest";
import { ShapeSerializationUtil } from './shape-serialization-util';
import { SerializableGroup, SerializableRectangle } from '$mono/shape/serialization/shapes';
import { SerializableLineConnector } from '$mono/shape/serialization/connector';
import { Point, PointF } from '$libs/graphics-geo/point';
import { MonoFile, Extra } from './mono-file';

describe('ShapeSerializationUtil', () => {
    const root = SerializableGroup.create({
        id: "root",
        isIdTemporary: false,
        versionCode: 2,
        shapes: [
            SerializableRectangle.EMPTY,
            SerializableRectangle.EMPTY,
        ]
    });
    const connectors: SerializableLineConnector[] = [
        SerializableLineConnector.create({
            lineId: "lineId",
            anchor: 1,
            targetId: "targetId",
            ratio: PointF.create({ left: 0.5, top: 0.6 }),
            offset: Point.of(10, 20),
        }),
        SerializableLineConnector.create({
            lineId: "lineId1",
            anchor: 0,
            targetId: "targetId",
            ratio: PointF.create({ left: 0.5, top: 0.6 }),
            offset: Point.of(10, 20),
        }),
    ];
    const extra = Extra.create("Test", Point.of(30, 50));
    const modifiedTimestampMillis = 1000;

    test('should serialize and deserialize a shape correctly', () => {
        const json = ShapeSerializationUtil.toShapeJson(root);
        console.log(json);
        const deserializedShape = ShapeSerializationUtil.fromShapeJson(json);
        expect(deserializedShape).toEqual(root);
    });

    test('should serialize and deserialize connectors correctly', () => {
        const json = ShapeSerializationUtil.toConnectorsJson(connectors);
        console.log(json);
        const deserializedConnectors = ShapeSerializationUtil.fromConnectorsJson(json);
        expect(deserializedConnectors).toEqual(connectors);
    });

    test('should serialize and deserialize a MonoFile correctly', () => {
        const monoFile = MonoFile.create(root, connectors, extra, modifiedTimestampMillis);
        const json = ShapeSerializationUtil.toMonoFileJson(monoFile);
        console.log(json);
        const deserializedMonoFile = ShapeSerializationUtil.fromMonoFileJson(json);
        expect(deserializedMonoFile).toEqual(monoFile);
    });
});
