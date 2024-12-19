/*
 * Copyright (c) 2024, tuanchauict
 */

import { Rect } from "$libs/graphics-geo/rect";
import { StringExt } from "$libs/string";
import { describe, expect, test } from "vitest";
import { TextBitmapFactory } from './text-bitmap-factory';
import { Text } from '$mono/shape/shape/text';
import trimIndent = StringExt.trimIndent;

describe('TextBitmapFactory', () => {
    test('toBitmap', () => {
        const text = Text.fromRect({ rect: Rect.byLTWH(0, 0, 7, 5) });
        text.setText('012345678\nabc');
        const bitmap = TextBitmapFactory.toBitmap(
            text.bound.size,
            text.renderableText.getRenderableText(),
            text.extra,
            false,
        );
        expect(bitmap.toString().trim()).toBe(
            trimIndent(`
            ┌─────┐
            │01234│
            │5678 │
            │ abc │
            └─────┘
            `),
        );
    });
});
