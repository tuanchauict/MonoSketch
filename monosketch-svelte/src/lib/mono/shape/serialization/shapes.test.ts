/*
 * Copyright (c) 2024, tuanchauict
 */

import { Rect } from "$libs/graphics-geo/rect";
import { SerializableRectExtra } from "$mono/shape/serialization/extras";
import { SerializableRectangle } from "$mono/shape/serialization/shapes";
import { describe, expect, it } from "vitest";

describe('SerializableRectangle', () => {
    it('should serialize correctly', () => {
        const original = SerializableRectangle.create({
            id: 'rect1',
            isIdTemporary: false,
            versionCode: 1,
            bound: Rect.byLTWH(10, 20, 100, 100),
            extra: SerializableRectExtra.create({
                isFillEnabled: true,
                userSelectedFillStyleId: 'fillStyle1',
                isBorderEnabled: true,
                userSelectedBorderStyleId: 'borderStyle1',
                dashPattern: 'dashPattern1',
                corner: 'corner1',
            }),
        });

        // @ts-ignore
        const json = original.toJson();
        console.log(json);
        expect(json).toStrictEqual({
            type: 'R',
            i: 'rect1',
            idtemp: false,
            v: 1,
            b: "10|20|100|100",
            e: {
                fe: true,
                fu: 'fillStyle1',
                be: true,
                bu: 'borderStyle1',
                du: 'dashPattern1',
                rc: 'corner1',
            },
        });
    });

    it('should deserialize correctly', () => {
        const json = {
            type: 'R',
            i: 'rect2',
            idtemp: true,
            v: 2,
            b: "33|44|55|66",
            e: {
                fe: false,
                fu: 'fillStyle2',
                be: false,
                bu: 'borderStyle2',
                du: 'dashPattern2',
                rc: 'corner2',
            },
        };

        // @ts-ignore
        const deserialized = SerializableRectangle.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableRectangle.create({
                id: 'rect2',
                isIdTemporary: true,
                versionCode: 2,
                bound: Rect.byLTWH(33, 44, 55, 66),
                extra: SerializableRectExtra.create({
                    isFillEnabled: false,
                    userSelectedFillStyleId: 'fillStyle2',
                    isBorderEnabled: false,
                    userSelectedBorderStyleId: 'borderStyle2',
                    dashPattern: 'dashPattern2',
                    corner: 'corner2',
                }),
            })
        );
    });

    it("should deserialize with null id correctly", () => {
        const json = {
            type: 'R',
            i: null,
            idtemp: true,
            v: 2,
            b: "33|44|55|66",
            e: {
                fe: false,
                fu: 'fillStyle2',
                be: false,
                bu: 'borderStyle2',
                du: 'dashPattern2',
                rc: 'corner2',
            },
        };
        // @ts-ignore
        const deserialized = SerializableRectangle.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableRectangle.create({
                id: null,
                isIdTemporary: true,
                versionCode: 2,
                bound: Rect.byLTWH(33, 44, 55, 66),
                extra: SerializableRectExtra.create({
                    isFillEnabled: false,
                    userSelectedFillStyleId: 'fillStyle2',
                    isBorderEnabled: false,
                    userSelectedBorderStyleId: 'borderStyle2',
                    dashPattern: 'dashPattern2',
                    corner: 'corner2',
                }),
            })
        );
    });
});