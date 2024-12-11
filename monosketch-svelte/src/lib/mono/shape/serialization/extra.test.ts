/*
 * Copyright (c) 2024, tuanchauict
 */

import { describe, expect, it } from "vitest";
import { SerializableRectExtra, SerializableTextExtra } from './extras';

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

describe('SerializableTextExtra', () => {
    it('should create a SerializableTextExtra instance correctly', () => {
        const original = SerializableTextExtra.create({
            boundExtra: SerializableRectExtra.create({
                isFillEnabled: true,
                userSelectedFillStyleId: 'fillStyle1',
                isBorderEnabled: true,
                userSelectedBorderStyleId: 'borderStyle1',
                dashPattern: 'dashPattern1',
                corner: 'corner1',
            }),
            textHorizontalAlign: 1,
            textVerticalAlign: 2,
        });

        // @ts-ignore
        const json = original.toJson();
        console.log(json);
        expect(json).toStrictEqual({
            be: {
                fe: true,
                fu: 'fillStyle1',
                be: true,
                bu: 'borderStyle1',
                du: 'dashPattern1',
                rc: 'corner1',
            },
            tha: 1,
            tva: 2,
        });
    });

    it('should serialize and deserialize correctly', () => {
        const json = {
            be: {
                fe: false,
                fu: 'fillStyle2',
                be: false,
                bu: 'borderStyle2',
                du: 'dashPattern3',
                rc: 'corner4',
            },
            tha: 3,
            tva: 4,
        }

        // @ts-ignore
        const deserialized = SerializableTextExtra.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableTextExtra.create({
                boundExtra: SerializableRectExtra.create({
                    isFillEnabled: false,
                    userSelectedFillStyleId: 'fillStyle2',
                    isBorderEnabled: false,
                    userSelectedBorderStyleId: 'borderStyle2',
                    dashPattern: 'dashPattern3',
                    corner: 'corner4',
                }),
                textHorizontalAlign: 3,
                textVerticalAlign: 4,
            })
        );
    });
});