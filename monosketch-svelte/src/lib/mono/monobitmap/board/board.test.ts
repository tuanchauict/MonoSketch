import { beforeEach, describe, expect, test } from 'vitest';
import { MonoBoard } from '$mono/monobitmap/board/board';
import { HighlightType, Pixel } from '$mono/monobitmap/board/pixel';
import { Point } from '$libs/graphics-geo/point';
import { Rect } from '$libs/graphics-geo/rect';
import { StringExt } from '$libs/string';
import trimMargin = StringExt.trimMargin;

describe('MonoBoard', () => {
    let target: MonoBoard;

    beforeEach(() => {
        target = new MonoBoard();
        target.clearAndSetWindow(Rect.byLTWH(-100, -100, 200, 200));
    });

    test('getSet', () => {
        const points = [-48, -32, -18, -16, 0, 16, 18, 32, 48].map(
            (value) => new Point(value, value),
        );

        points.forEach((point) => {
            expect(target.get(point.left, point.top)).toBe(Pixel.TRANSPARENT);
        });

        const chars = '012345678';
        chars.split('').forEach((char, index) => {
            target.setPoint(points[index], char, HighlightType.NO);
        });
        chars.split('').forEach((char, index) => {
            expect(target.getPoint(points[index]).visualChar).toBe(char);
        });

        expect(target.boardCount).toBe(7);
    });

    test('fillRect', () => {
        target.fillRect(Rect.byLTWH(1, 1, 3, 3), 'A', HighlightType.NO);

        expect(target.toString()).toStrictEqual(
            trimMargin(`
                |                x
                | AAA            x
                | AAA            x
                | AAA            x
                |                x
                |                x
                |                x
                |                x
                |                x
                |                x
                |                x
                |                x
                |                x
                |                x
                |                x
                |                x
            `.replaceAll('x', '')), // Use x to keep the extra spaces. Otherwise, the spaces will be trimmed due to lint.
        );

        expect(target.boardCount).toBe(1);

        target.fillRect(Rect.byLTWH(-3, -3, 3, 3), 'B', HighlightType.NO);
        expect(target.toString()).toStrictEqual(
            trimMargin(`
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |             BBB                x
                |             BBB                x
                |             BBB                x
                |                                x
                |                 AAA            x
                |                 AAA            x
                |                 AAA            x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
            `.replaceAll('x', '')), // Use x to keep the extra spaces. Otherwise, the spaces will be trimmed due to lint.
        );

        expect(target.boardCount).toBe(2);

        target.fillRect(Rect.byLTWH(-1, 0, 3, 1), 'C', HighlightType.NO);
        expect(target.toString()).toStrictEqual(
            trimMargin(`
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |             BBB                x
                |             BBB                x
                |             BBB                x
                |               CCC              x
                |                 AAA            x
                |                 AAA            x
                |                 AAA            x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                |                                x
                `.replaceAll('x', '')), // Use x to keep the extra spaces. Otherwise, the spaces will be trimmed due to lint.
        );

        expect(target.boardCount).toBe(3);
    });
});
