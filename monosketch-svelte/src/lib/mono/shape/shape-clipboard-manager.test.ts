/*
 * Copyright (c) 2024, tuanchauict
 */
import { Point, PointF } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { SerializableLineConnector } from "$mono/shape/serialization/connector";
import { SerializableRectExtra } from "$mono/shape/serialization/extras";
import { SerializableRectangle } from "$mono/shape/serialization/shapes";
import { ClipboardObject } from "$mono/shape/shape-clipboard-manager";
import { LineAnchor } from "$mono/shape/shape/linehelper";
import { describe, expect, test } from "vitest";

describe('ClipboardObjectTest', () => {
    test('testJsonize', () => {
        const shape = SerializableRectangle.create(
            {
                id: '1',
                isIdTemporary: false,
                versionCode: 0,
                bound: Rect.ZERO,
                extra: SerializableRectExtra.EMPTY,
            },
        );
        const connector = SerializableLineConnector.create(
            {
                lineId: '1',
                anchor: LineAnchor.START,
                targetId: '2',
                ratio: PointF.create({ left: 0, top: 0 }),
                offset: Point.ZERO,
            },
        );

        const clipboardObject = ClipboardObject.create([shape], [connector]);
        // @ts-expect-error toJson is attached by Jsonizable
        const json = clipboardObject.toJson();
        console.log(json);
        expect(json).toEqual({
            // @ts-expect-error toJson is attached by Jsonizable
            shapes: [shape.toJson()],
            // @ts-expect-error toJson is attached by Jsonizable
            connectors: [connector.toJson()],
        });

        // @ts-expect-error fromJson is attached by Jsonizable
        const deserializedClipboardObject = ClipboardObject.fromJson(json);
        expect(deserializedClipboardObject).toStrictEqual(clipboardObject);
    });
});
