/*
 * Copyright (c) 2024, tuanchauict
 */

import { RectangleBitmapFactory } from './rectangle-bitmap-factory';
import type { Size } from '$libs/graphics-geo/size';
import { MonoBitmap } from '$mono/monobitmap/bitmap/monobitmap';
import type { TextExtra } from '$mono/shape/extra/shape-extra';
import { TextHorizontalAlign, TextVerticalAlign } from '$mono/shape/extra/style';

/**
 * A factory class to create a bitmap for a text shape.
 */
export class TextBitmapFactory {
    static toBitmap(
        boundSize: Size,
        renderableText: string[],
        extra: TextExtra,
        isTextEditingMode: boolean,
    ): MonoBitmap.Bitmap {
        const bgBitmap = RectangleBitmapFactory.toBitmap(boundSize, extra.boundExtra);
        const bitmapBuilder = new MonoBitmap.Builder(boundSize.width, boundSize.height);
        if (!(boundSize.width === 1 && boundSize.height === 1 && isTextEditingMode)) {
            bitmapBuilder.fillBitmap(0, 0, bgBitmap);
        }

        const adjustedRenderableText = isTextEditingMode ? [] : renderableText;
        this.fillText(bitmapBuilder, adjustedRenderableText, boundSize, extra);
        return bitmapBuilder.toBitmap();
    }

    private static fillText(
        bitmapBuilder: MonoBitmap.Builder,
        renderableText: string[],
        boundSize: Size,
        extra: TextExtra,
    ) {
        const hasBorder = extra.hasBorder();
        const rowOffset = hasBorder ? 1 : 0;
        const colOffset = hasBorder ? 1 : 0;

        const maxTextWidth = boundSize.width - colOffset * 2;
        const maxTextHeight = Math.max(0, boundSize.height - rowOffset * 2);

        const row0 = this.getRow0(extra, rowOffset, maxTextHeight, renderableText);

        const horizontalAlign = extra.textAlign.horizontalAlign;
        const maxRowIndex = Math.min(renderableText.length, maxTextHeight);
        for (let rowIndex = 0; rowIndex < maxRowIndex; rowIndex++) {
            const row = renderableText[rowIndex];
            const col0 = this.getCol0(horizontalAlign, colOffset, maxTextWidth, row);
            for (let colIndex = 0; colIndex < row.length; colIndex++) {
                const char = row[colIndex];
                if (char !== ' ') {
                    bitmapBuilder.put(row0 + rowIndex, col0 + colIndex, char, char);
                }
            }
        }
    }

    private static getCol0(horizontalAlign: TextHorizontalAlign, colOffset: number, maxTextWidth: number, row: string) {
        switch (horizontalAlign) {
            case TextHorizontalAlign.LEFT:
                return colOffset;
            case TextHorizontalAlign.MIDDLE:
                return Math.floor((maxTextWidth - row.length) / 2) + colOffset;
            case TextHorizontalAlign.RIGHT:
                return maxTextWidth - row.length + colOffset;
        }
    }

    private static getRow0(extra: TextExtra, rowOffset: number, maxTextHeight: number, renderableText: string[]) {
        switch (extra.textAlign.verticalAlign) {
            case TextVerticalAlign.TOP:
                return rowOffset;
            case TextVerticalAlign.MIDDLE:
                return maxTextHeight < renderableText.length
                    ? rowOffset
                    : Math.floor((maxTextHeight - renderableText.length) / 2) + rowOffset;
            case TextVerticalAlign.BOTTOM:
                return maxTextHeight < renderableText.length
                    ? rowOffset
                    : maxTextHeight - renderableText.length + rowOffset;
        }
    };
}
