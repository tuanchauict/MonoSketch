import { describe, expect, test } from 'vitest';
import { MousePointer, MousePointerType } from '$mono/workspace/mouse/mouse-pointer';
import { Point } from '$libs/graphics-geo/point';

describe('equals test', () => {
    describe('idle', () => {
        const idle = MousePointer.idle;
        test('idle', () => {
            expect(idle.equals(MousePointer.idle)).toBe(true);
        });

        test('with other types', () => {
            expect(
                idle.equals(
                    MousePointer.down(
                        idle.boardCoordinate,
                        idle.clientCoordinate,
                        idle.isWithShiftKey,
                    ),
                ),
            ).toBe(false);
        });
    });

    describe('mouse down', () => {
        const mouseDown = MousePointer.down(Point.of(1, 2), Point.of(3, 4), false);
        test('with mouse down', () => {
            expect(mouseDown.equals(mouseDown)).toBe(true);
            expect(mouseDown.equals(MousePointer.down(Point.of(1, 2), Point.of(3, 4), false))).toBe(
                true,
            );
            expect(mouseDown.equals(MousePointer.down(Point.of(1, 2), Point.of(3, 4), true))).toBe(
                false,
            );
            expect(
                mouseDown.equals({
                    ...mouseDown,
                    type: MousePointerType.DRAG,
                }),
            ).toBe(false);
            expect(
                    mouseDown.equals({
                        ...mouseDown,
                        isWithShiftKey: true,
                    }),
            ).toBe(false);
        });
    });
});
