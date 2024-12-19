/*
 * Copyright (c) 2024, tuanchauict
 */

import { Rect } from "$libs/graphics-geo/rect";
import { StringExt } from "$libs/string";
import { describe, expect, test } from "vitest";
import { RectangleBitmapFactory } from './rectangle-bitmap-factory';
import { Rectangle } from '$mono/shape/shape/rectangle';
import trimIndent = StringExt.trimIndent;

describe('RectangleBitmapFactory', () => {
    test('toBitmap', () => {
        const shape = Rectangle.fromRect({ rect: Rect.byLTWH(10, 10, 5, 5) });
        const bitmap = RectangleBitmapFactory.toBitmap(shape.bound.size, shape.extra);
        expect(bitmap.size.width).toBe(shape.bound.width);
        expect(bitmap.size.height).toBe(shape.bound.height);
        console.log(bitmap.toString());
        expect(bitmap.toString().trim()).toBe(
            trimIndent(`
            ┌───┐
            │   │
            │   │
            │   │
            └───┘
            `),
        );
    });
});
