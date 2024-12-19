/*
 * Copyright (c) 2024, tuanchauict
 */

import { Point } from "$libs/graphics-geo/point";
import { StringExt } from "$libs/string";
import { PredefinedStraightStrokeStyle } from "$mono/shape/extra/predefined-styles";
import { describe, expect, test } from "vitest";
import { LineBitmapFactory } from './line-bitmap-factory';
import { LineExtra } from '$mono/shape/extra/shape-extra';
import { AnchorChar, StraightStrokeDashPattern } from '$mono/shape/extra/style';
import trimMargin = StringExt.trimMargin;
import trimIndent = StringExt.trimIndent;

const LINE_EXTRA = new LineExtra(
    true,
    PredefinedStraightStrokeStyle.PREDEFINED_STYLES[0],
    true,
    AnchorChar.create({ id: 'id', displayName: 'name', all: '0' }),
    true,
    AnchorChar.create({ id: 'id', displayName: 'name', all: '1' }),
    StraightStrokeDashPattern.SOLID,
    false,
);

describe('LineBitmapFactory.toBitmap', () => {
    describe('simpleHorizontalLine', () => {
        test('simpleHorizontalLine', () => {
            const points = [
                new Point(2, 0),
                new Point(6, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe('0───1');
        });

        test('simpleHorizontalLine_reversed', () => {
            const points = [
                new Point(6, 0),
                new Point(2, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe('1───0');
        });
    });

    describe('3 straight horizontal points', () => {
        test('MonotonicPoints', () => {
            const points = [
                new Point(0, 0),
                new Point(2, 0),
                new Point(4, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe('0───1');
        });

        test('MonotonicPoints_reversed', () => {
            const points = [
                new Point(4, 0),
                new Point(2, 0),
                new Point(0, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe('1───0');
        });

        test('NonMonotonicPoints', () => {
            const points = [
                new Point(0, 0),
                new Point(4, 0),
                new Point(2, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe('0─1──');
        });

        test('NonMonotonicPoints_reversed', () => {
            const points = [
                new Point(4, 0),
                new Point(0, 0),
                new Point(2, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe('──1─0');
        });
    });

    describe('simpleVerticalLine', () => {
        test('simpleVerticalLine', () => {
            const points = [
                new Point(0, 2),
                new Point(0, 6),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimIndent(`
                0
                │
                │
                │
                1
            `),
            );
        });

        test('simpleVerticalLine_reversed', () => {
            const points = [
                new Point(0, 6),
                new Point(0, 2),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimIndent(`
                1
                │
                │
                │
                0
            `),
            );
        });
    });

    describe("3 straight vertical points", () => {
        test('MonotonicPoints', () => {
            const points = [
                new Point(0, 0),
                new Point(0, 2),
                new Point(0, 4),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimIndent(`
                0
                │
                │
                │
                1
        `),
            );
        });

        test('MonotonicPoints_reversed', () => {
            const points = [
                new Point(0, 4),
                new Point(0, 2),
                new Point(0, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimIndent(`
                1
                │
                │
                │
                0
            `),
            );
        });

        test('NonMonotonicPoints', () => {
            const points = [
                new Point(0, 0),
                new Point(0, 4),
                new Point(0, 2),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimIndent(`
                0
                │
                1
                │
                │
            `),
            );
        });

        test('NonMonotonicPoints_reversed', () => {
            const points = [
                new Point(0, 4),
                new Point(0, 0),
                new Point(0, 2),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimIndent(`
                │
                │
                1
                │
                0
            `),
            );
        });
    });

    describe('upperLeft', () => {
        test('upperLeft', () => {
            const points = [
                new Point(0, 4),
                new Point(4, 4),
                new Point(4, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimMargin(`
                |    1
                |    │
                |    │
                |    │
                |0───┘
            `),
            );
        });

        test('upperLeft_reversed', () => {
            const points = [
                new Point(4, 0),
                new Point(4, 4),
                new Point(0, 4),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimMargin(`
                |    0
                |    │
                |    │
                |    │
                |1───┘
            `),
            );
        });
    });

    describe('lowerLeft', () => {
        test('lowerLeft', () => {
            const points = [
                new Point(0, 0),
                new Point(4, 0),
                new Point(4, 4),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimMargin(`
                |0───┐
                |    │
                |    │
                |    │
                |    1
            `),
            );
        });

        test('lowerLeft_reversed', () => {
            const points = [
                new Point(4, 4),
                new Point(4, 0),
                new Point(0, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimMargin(`
                |1───┐
                |    │
                |    │
                |    │
                |    0
            `),
            );
        });
    });

    describe('upperRight', () => {
        test('upperRight', () => {
            const points = [
                new Point(4, 4),
                new Point(0, 4),
                new Point(0, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimMargin(`
                |1    x
                |│    x
                |│    x
                |│    x
                |└───0
            `.replaceAll('x', '')), // x is added to keep the space chars at the end of the line. Stupid lint.
            );
        });

        test('upperRight_reversed', () => {
            const points = [
                new Point(0, 0),
                new Point(0, 4),
                new Point(4, 4),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimMargin(`
                |0    x
                |│    x
                |│    x
                |│    x
                |└───1
            `.replaceAll('x', '')), // x is added to keep the space chars at the end of the line. Stupid lint.
            );
        });
    });

    describe('lowerRight', () => {
        test('lowerRight', () => {
            const points = [
                new Point(4, 0),
                new Point(0, 0),
                new Point(0, 4),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimMargin(`
                |┌───0
                |│    x
                |│    x
                |│    x
                |1    x
            `.replaceAll('x', '')), // x is added to keep the space chars at the end of the line. Stupid lint.
            );
        });

        test('lowerRight_reversed', () => {
            const points = [
                new Point(0, 4),
                new Point(0, 0),
                new Point(4, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, LINE_EXTRA);
            expect(bitmap.toString()).toBe(
                trimMargin(`
                |┌───1
                |│    x
                |│    x
                |│    x
                |0    x
            `.replaceAll('x', '')), // x is added to keep the space chars at the end of the line. Stupid lint.
            );
        });
    });

    describe('anchorChar', () => {
        const anchorCharStart = AnchorChar.create({
            id: 'id',
            displayName: 'name',
            left: 'L',
            right: 'R',
            top: 'T',
            bottom: 'B',
        });

        const anchorCharEnd = AnchorChar.create({
            id: 'id',
            displayName: 'name',
            left: 'l',
            right: 'r',
            top: 't',
            bottom: 'b',
        });

        test('leftToRight', () => {
            const lineExtra = LINE_EXTRA.copy({
                userSelectedStartAnchor: anchorCharStart,
                userSelectedEndAnchor: anchorCharEnd,
            });
            const points = [
                new Point(0, 0),
                new Point(4, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, lineExtra);
            expect(bitmap.toString()).toBe('L───r');
        });

        test('rightToLeft', () => {
            const lineExtra = LINE_EXTRA.copy({
                userSelectedStartAnchor: anchorCharStart,
                userSelectedEndAnchor: anchorCharEnd,
            });
            const points = [
                new Point(4, 0),
                new Point(0, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, lineExtra);
            expect(bitmap.toString()).toBe('l───R');
        });

        test('topToBottom', () => {
            const lineExtra = LINE_EXTRA.copy({
                userSelectedStartAnchor: anchorCharStart,
                userSelectedEndAnchor: anchorCharEnd,
            });
            const points = [
                new Point(0, 0),
                new Point(0, 4),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, lineExtra);
            expect(bitmap.toString()).toBe(
                trimIndent(`
                T
                │
                │
                │
                b
            `),
            );
        });

        test('bottomToTop', () => {
            const lineExtra = LINE_EXTRA.copy({
                userSelectedStartAnchor: anchorCharStart,
                userSelectedEndAnchor: anchorCharEnd,
            });
            const points = [
                new Point(0, 4),
                new Point(0, 0),
            ];
            const bitmap = LineBitmapFactory.toBitmap(points, lineExtra);
            expect(bitmap.toString()).toBe(
                trimIndent(`
                t
                │
                │
                │
                B
            `),
            );
        });
    });
});
