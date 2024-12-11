/*
 * Copyright (c) 2024, tuanchauict
 */

import { describe, expect, it } from "vitest";
import { SerializableRectExtra } from './extras';

describe('SerializableRectExtra', () => {
    it('should create a SerializableRectExtra instance correctly', () => {
        const original = SerializableRectExtra.create({
            isFillEnabled: true,
            userSelectedFillStyleId: 'fillStyle1',
            isBorderEnabled: true,
            userSelectedBorderStyleId: 'borderStyle1',
            dashPattern: 'dashPattern1',
            corner: 'corner1',
        });

        // @ts-ignore
        const json = original.toJson();
        console.log(json);
        expect(json).toStrictEqual({
            fe: true,
            fu: 'fillStyle1',
            be: true,
            bu: 'borderStyle1',
            du: 'dashPattern1',
            rc: 'corner1',
        });
    });

    it('should serialize and deserialize correctly', () => {
        const json = {
            fe: false,
            fu: 'fillStyle2',
            be: false,
            bu: 'borderStyle2',
            du: 'dashPattern3',
            rc: 'corner4',
        }

        // @ts-ignore
        const deserialized = SerializableRectExtra.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableRectExtra.create({
                isFillEnabled: false,
                userSelectedFillStyleId: 'fillStyle2',
                isBorderEnabled: false,
                userSelectedBorderStyleId: 'borderStyle2',
                dashPattern: 'dashPattern3',
                corner: 'corner4',
            })
        );
    });
});