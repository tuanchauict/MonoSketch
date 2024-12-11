/*
 * Copyright (c) 2024, tuanchauict
 */

import { Rect } from "$libs/graphics-geo/rect";
import { TextExtra } from "$mono/shape/extra/shape-extra";
import type { SerializableText } from "$mono/shape/serialization/shapes";
import { describe, it, expect } from 'vitest';
import { Text } from './text';

describe('TextTest', () => {
    const PARENT_ID = '1';

    it('testSerialization_init', () => {
        const text = new Text(Rect.byLTWH(1, 2, 3, 4), PARENT_ID);

        const serializableText = text.toSerializableShape(true) as SerializableText;
        expect(text.text).toBe(serializableText.text);
        expect(text.bound).toEqual(serializableText.bound);
        expect(text.extra.toSerializableExtra()).toEqual(serializableText.extra);
    });

    it('testSerialization_updateBound', () => {
        const text = new Text(Rect.byLTWH(1, 2, 3, 4), PARENT_ID);
        text.setBound(Rect.byLTWH(5, 6, 7, 8));

        const serializableText = text.toSerializableShape(true) as SerializableText;
        expect(text.text).toBe(serializableText.text);
        expect(text.bound).toEqual(serializableText.bound);
        expect(text.extra.toSerializableExtra()).toEqual(serializableText.extra);
    });

    it('testSerialization_updateText', () => {
        const text = new Text(Rect.byLTWH(1, 2, 3, 4), PARENT_ID);
        text.setText('Hello Hello!');

        const serializableText = text.toSerializableShape(true) as SerializableText;
        expect(text.text).toBe(serializableText.text);
        expect(text.bound).toEqual(serializableText.bound);
        expect(text.extra.toSerializableExtra()).toEqual(serializableText.extra);
    });

    it('testSerialization_restore', () => {
        const text = new Text(Rect.byLTWH(1, 2, 3, 4), PARENT_ID);
        text.setText('Hello Hello!');
        text.setBound(Rect.byLTWH(5, 5, 2, 2));

        const serializableText = text.toSerializableShape(true) as SerializableText;
        const text2 = Text.fromSerializable(serializableText, PARENT_ID);
        expect(text2.parentId).toBe(PARENT_ID);
        expect(text2.text).toBe(text.text);
        expect(text2.bound).toEqual(text.bound);
        expect(text2.extra).toEqual(text.extra);
        expect(text2.renderableText.getRenderableText()).toEqual(text.renderableText.getRenderableText());
    });

    it('testConvertRenderableText', () => {
        const target = new Text(Rect.byLTWH(0, 0, 5, 5));

        target.setText('0 1234 12345\n1   2 3 4 5678901 23');
        target.setExtra(TextExtra.NO_BOUND);
        expect(target.renderableText.getRenderableText()).toEqual([
            '0', '1234', '12345', '1   2', '3 4', '56789', '01 23'
        ]);
    });

    it('testContentBound', () => {
        const target = new Text(Rect.byLTWH(1, 2, 5, 6));

        const defaultBoundExtra = target.extra.boundExtra;

        const extraWithBorder = target.extra.copy({ boundExtra: defaultBoundExtra.copy({ isBorderEnabled: true }) });
        target.setExtra(extraWithBorder);
        expect(target.contentBound).toEqual(Rect.byLTWH(2, 3, 3, 4));

        const extraWithoutBorder = target.extra.copy({ boundExtra: defaultBoundExtra.copy({ isBorderEnabled: false }) });
        target.setExtra(extraWithoutBorder);
        expect(target.contentBound).toEqual(Rect.byLTWH(1, 2, 5, 6));
    });
});
