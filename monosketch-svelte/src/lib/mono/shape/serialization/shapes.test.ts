/*
 * Copyright (c) 2024, tuanchauict
 */

import { DirectedPoint, Direction, Point } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { SerializableLineExtra, SerializableRectExtra, SerializableTextExtra } from "$mono/shape/serialization/extras";
import {
    SerializableGroup,
    SerializableLine,
    SerializableRectangle,
    SerializableText,
} from "$mono/shape/serialization/shapes";
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
            }),
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
            }),
        );
    });
});

describe('SerializableText', () => {
    it('should serialize correctly', () => {
        const original = SerializableText.create({
            id: 'text1',
            isIdTemporary: false,
            versionCode: 1,
            bound: Rect.byLTWH(10, 20, 100, 100),
            text: 'Hello, world!',
            extra: SerializableTextExtra.create({
                boundExtra: SerializableRectExtra.create(
                    {
                        isFillEnabled: false,
                        userSelectedFillStyleId: 'fillStyle1',
                        isBorderEnabled: false,
                        userSelectedBorderStyleId: 'borderStyle1',
                        dashPattern: 'dashPattern1',
                        corner: 'corner1',
                    },
                ),
                textHorizontalAlign: 0,
                textVerticalAlign: 1,
            }),
            isTextEditable: true,
        });

        // @ts-ignore
        const json = original.toJson();
        console.log(json);
        expect(json).toStrictEqual({
            type: 'T',
            i: 'text1',
            idtemp: false,
            v: 1,
            b: "10|20|100|100",
            t: 'Hello, world!',
            e: {
                be: {
                    fe: false,
                    fu: 'fillStyle1',
                    be: false,
                    bu: 'borderStyle1',
                    du: 'dashPattern1',
                    rc: 'corner1',
                },
                tha: 0,
                tva: 1,
            },
            te: true,
        });
    });

    it('should deserialize correctly', () => {
        const json = {
            type: 'T',
            i: 'text2',
            idtemp: true,
            v: 2,
            b: "33|44|55|66",
            t: 'Goodbye, world!',
            e: {
                be: {
                    fe: false,
                    fu: 'fillStyle2',
                    be: false,
                    bu: 'borderStyle2',
                    du: 'dashPattern2',
                    rc: 'corner2',
                },
                tha: 2,
                tva: 1,
            },
            te: false,
        };

        // @ts-ignore
        const deserialized = SerializableText.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableText.create({
                id: 'text2',
                isIdTemporary: true,
                versionCode: 2,
                bound: Rect.byLTWH(33, 44, 55, 66),
                text: 'Goodbye, world!',
                extra: SerializableTextExtra.create({
                    boundExtra: SerializableRectExtra.create(
                        {
                            isFillEnabled: false,
                            userSelectedFillStyleId: 'fillStyle2',
                            isBorderEnabled: false,
                            userSelectedBorderStyleId: 'borderStyle2',
                            dashPattern: 'dashPattern2',
                            corner: 'corner2',
                        },
                    ),
                    textHorizontalAlign: 2,
                    textVerticalAlign: 1,
                }),
                isTextEditable: false,
            }),
        );
    });

    it("should deserialize with null id correctly", () => {
        const json = {
            type: 'T',
            i: null,
            idtemp: true,
            v: 2,
            b: "33|44|55|66",
            t: 'Sample text',
            e: {
                be: {
                    fe: false,
                    fu: 'fillStyle2',
                    be: false,
                    bu: 'borderStyle2',
                    du: 'dashPattern2',
                    rc: 'corner2',
                },
                tha: 2,
                tva: 1,
            },
            te: true,
        };
        // @ts-ignore
        const deserialized = SerializableText.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableText.create({
                id: null,
                isIdTemporary: true,
                versionCode: 2,
                bound: Rect.byLTWH(33, 44, 55, 66),
                text: 'Sample text',
                extra: SerializableTextExtra.create({
                    boundExtra: SerializableRectExtra.create(
                        {
                            isFillEnabled: false,
                            userSelectedFillStyleId: 'fillStyle2',
                            isBorderEnabled: false,
                            userSelectedBorderStyleId: 'borderStyle2',
                            dashPattern: 'dashPattern2',
                            corner: 'corner2',
                        },
                    ),
                    textHorizontalAlign: 2,
                    textVerticalAlign: 1,
                }),
                isTextEditable: true,
            }),
        );
    });
});

describe('SerializableLine', () => {
    it('should serialize correctly', () => {
        const original = SerializableLine.create({
            id: 'line1',
            isIdTemporary: false,
            versionCode: 1,
            startPoint: DirectedPoint.of(Direction.HORIZONTAL, 10, 20),
            endPoint: DirectedPoint.of(Direction.VERTICAL, 30, 40),
            jointPoints: [Point.of(15, 25), Point.of(20, 30)],
            extra: SerializableLineExtra.create({
                isStrokeEnabled: true,
                userSelectedStrokeStyleId: 'strokeStyle1',
                isStartAnchorEnabled: true,
                userSelectedStartAnchorId: 'startAnchor1',
                isEndAnchorEnabled: true,
                userSelectedEndAnchorId: 'endAnchor1',
                dashPattern: 'dashPattern1',
                isRoundedCorner: true,
            }),
            wasMovingEdge: false,
        });

        // @ts-ignore
        const json = original.toJson();
        console.log(json);
        expect(json).toStrictEqual({
            type: 'L',
            i: 'line1',
            idtemp: false,
            v: 1,
            ps: 'H|10|20',
            pe: 'V|30|40',
            jps: ['15|25', '20|30'],
            e: {
                se: true,
                su: 'strokeStyle1',
                ase: true,
                asu: 'startAnchor1',
                aee: true,
                aeu: 'endAnchor1',
                du: 'dashPattern1',
                rc: true,
            },
            em: false,
        });
    });

    it('should deserialize correctly', () => {
        const json = {
            type: 'L',
            i: 'line2',
            idtemp: true,
            v: 2,
            ps: 'H|10|20',
            pe: 'V|30|40',
            jps: ['15|25', '20|30'],
            e: {
                se: true,
                su: 'strokeStyle1',
                ase: true,
                asu: 'startAnchor1',
                aee: true,
                aeu: 'endAnchor1',
                du: 'dashPattern1',
                rc: true,
            },
            em: true,
        };

        // @ts-ignore
        const deserialized = SerializableLine.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableLine.create({
                id: 'line2',
                isIdTemporary: true,
                versionCode: 2,
                startPoint: DirectedPoint.of(Direction.HORIZONTAL, 10, 20),
                endPoint: DirectedPoint.of(Direction.VERTICAL, 30, 40),
                jointPoints: [Point.of(15, 25), Point.of(20, 30)],
                extra: SerializableLineExtra.create({
                    isStrokeEnabled: true,
                    userSelectedStrokeStyleId: 'strokeStyle1',
                    isStartAnchorEnabled: true,
                    userSelectedStartAnchorId: 'startAnchor1',
                    isEndAnchorEnabled: true,
                    userSelectedEndAnchorId: 'endAnchor1',
                    dashPattern: 'dashPattern1',
                    isRoundedCorner: true,
                }),
                wasMovingEdge: true,
            }),
        );
    });

    it("should deserialize with null id correctly", () => {
        const json = {
            type: 'L',
            i: null,
            idtemp: true,
            v: 2,
            ps: 'H|10|20',
            pe: 'V|30|40',
            jps: ['15|25', '20|30'],
            e: {
                se: true,
                su: 'strokeStyle1',
                ase: true,
                asu: 'startAnchor1',
                aee: true,
                aeu: 'endAnchor1',
                du: 'dashPattern1',
                rc: true,
            },
            em: true,
        };
        // @ts-ignore
        const deserialized = SerializableLine.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableLine.create({
                id: null,
                isIdTemporary: true,
                versionCode: 2,
                startPoint: DirectedPoint.of(Direction.HORIZONTAL, 10, 20),
                endPoint: DirectedPoint.of(Direction.VERTICAL, 30, 40),
                jointPoints: [Point.of(15, 25), Point.of(20, 30)],
                extra: SerializableLineExtra.create({
                    isStrokeEnabled: true,
                    userSelectedStrokeStyleId: 'strokeStyle1',
                    isStartAnchorEnabled: true,
                    userSelectedStartAnchorId: 'startAnchor1',
                    isEndAnchorEnabled: true,
                    userSelectedEndAnchorId: 'endAnchor1',
                    dashPattern: 'dashPattern1',
                    isRoundedCorner: true,
                }),
                wasMovingEdge: true,
            }),
        );
    });
});

describe('SerializableGroup', () => {
    const dummyRect = SerializableRectangle.create({
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
    const dummyText = SerializableText.create(
        {
            id: 'text1',
            isIdTemporary: false,
            versionCode: 1,
            bound: Rect.byLTWH(10, 20, 100, 100),
            text: 'Hello, world!',
            extra: SerializableTextExtra.create({
                boundExtra: SerializableRectExtra.create(
                    {
                        isFillEnabled: false,
                        userSelectedFillStyleId: 'fillStyle1',
                        isBorderEnabled: false,
                        userSelectedBorderStyleId: 'borderStyle1',
                        dashPattern: 'dashPattern1',
                        corner: 'corner1',
                    },
                ),
                textHorizontalAlign: 0,
                textVerticalAlign: 1,
            }),
            isTextEditable: true,
        },
    );
    const dummyLine = SerializableLine.create(
        {
            id: 'line1',
            isIdTemporary: false,
            versionCode: 1,
            startPoint: DirectedPoint.of(Direction.HORIZONTAL, 10, 20),
            endPoint: DirectedPoint.of(Direction.VERTICAL, 30, 40),
            jointPoints: [Point.of(15, 25), Point.of(20, 30)],
            extra: SerializableLineExtra.create({
                isStrokeEnabled: true,
                userSelectedStrokeStyleId: 'strokeStyle1',
                isStartAnchorEnabled: true,
                userSelectedStartAnchorId: 'startAnchor1',
                isEndAnchorEnabled: true,
                userSelectedEndAnchorId: 'endAnchor1',
                dashPattern: 'dashPattern1',
                isRoundedCorner: true,
            }),
            wasMovingEdge: false,
        }
    );

    it('should serialize correctly', () => {
        const original = SerializableGroup.create({
            id: 'group1',
            isIdTemporary: false,
            versionCode: 1,
            shapes: [dummyRect, dummyText, dummyLine],
        });

        // @ts-ignore
        const json = original.toJson();
        console.log(json);
        expect(json).toStrictEqual({
            type: 'G',
            i: 'group1',
            idtemp: false,
            v: 1,
            ss: [
                // @ts-ignore
                dummyRect.toJson(),
                // @ts-ignore
                dummyText.toJson(),
                // @ts-ignore
                dummyLine.toJson(),
            ],
        });
    });

    it('should deserialize correctly', () => {
        const json = {
            type: 'G',
            i: 'group2',
            idtemp: true,
            v: 2,
            ss: [
                // @ts-ignore
                dummyText.toJson(),
                // @ts-ignore
                dummyLine.toJson(),
                // @ts-ignore
                dummyRect.toJson(),
            ],
        };

        // @ts-ignore
        const deserialized = SerializableGroup.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableGroup.create({
                id: 'group2',
                isIdTemporary: true,
                versionCode: 2,
                shapes: [
                    dummyText,
                    dummyLine,
                    dummyRect,
                ],
            }),
        );
    });

    it("should deserialize with null id correctly", () => {
        const json = {
            type: 'G',
            i: null,
            idtemp: true,
            v: 2,
            ss: [
                // @ts-ignore
                dummyRect.toJson(),
                // @ts-ignore
                dummyRect.toJson(),
                // @ts-ignore
                dummyLine.toJson(),
                // @ts-ignore
                dummyText.toJson(),
            ],
        };
        // @ts-ignore
        const deserialized = SerializableGroup.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableGroup.create({
                id: null,
                isIdTemporary: true,
                versionCode: 2,
                shapes: [
                    dummyRect,
                    dummyRect,
                    dummyLine,
                    dummyText,
                ],
            }),
        );
    });

    it("should handle serialize nested groups correctly", () => {
        const nestedGroup = SerializableGroup.create({
            id: 'nestedGroup1',
            isIdTemporary: false,
            versionCode: 1,
            shapes: [dummyRect, dummyText],
        });
        const original = SerializableGroup.create({
            id: 'group1',
            isIdTemporary: false,
            versionCode: 1,
            shapes: [nestedGroup, dummyLine],
        });

        // @ts-ignore
        const json = original.toJson();
        console.log(json);
        expect(json).toStrictEqual({
            type: 'G',
            i: 'group1',
            idtemp: false,
            v: 1,
            ss: [
                {
                    type: 'G',
                    i: 'nestedGroup1',
                    idtemp: false,
                    v: 1,
                    ss: [
                        // @ts-ignore
                        dummyRect.toJson(),
                        // @ts-ignore
                        dummyText.toJson(),
                    ],
                },
                // @ts-ignore
                dummyLine.toJson(),
            ],
        });
    });

    it("should deserialize nested groups correctly", () => {
        const json = {
            type: 'G',
            i: 'group1',
            idtemp: false,
            v: 1,
            ss: [
                {
                    type: 'G',
                    i: 'nestedGroup1',
                    idtemp: false,
                    v: 1,
                    ss: [
                        // @ts-ignore
                        dummyRect.toJson(),
                        // @ts-ignore
                        dummyText.toJson(),
                    ],
                },
                // @ts-ignore
                dummyLine.toJson(),
            ],
        };

        // @ts-ignore
        const deserialized = SerializableGroup.fromJson(json);
        expect(deserialized).toStrictEqual(
            SerializableGroup.create({
                id: 'group1',
                isIdTemporary: false,
                versionCode: 1,
                shapes: [
                    SerializableGroup.create({
                        id: 'nestedGroup1',
                        isIdTemporary: false,
                        versionCode: 1,
                        shapes: [dummyRect, dummyText],
                    }),
                    dummyLine,
                ],
            }),
        );
    });
});