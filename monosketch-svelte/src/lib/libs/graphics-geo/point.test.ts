import { describe, expect, test } from 'vitest';
import { DirectedPoint, Direction, Point } from '$libs/graphics-geo/point';

describe('Point test', () => {
    test('row column', () => {
        const a = new Point(1, 2);
        expect(a.row).toBe(2);
        expect(a.column).toBe(1);
    });

    test('equals', () => {
        const a = new Point(1, 2);
        expect(a.equals(new Point(1, 2))).toBe(true);
        expect(a.equals(new Point(2, 1))).toBe(false);
        expect(a.equals({ left: 1, top: 2 })).toBe(false);
        expect(a.equals(null)).toBe(false);
        expect(a.equals(undefined)).toBe(false);
    });

    test('minus', () => {
        const a = new Point(1, 2);
        const b = new Point(3, 4);
        expect(a.minus(b)).toEqual(new Point(-2, -2));
    });

    test('plus', () => {
        const a = new Point(1, 2);
        const b = new Point(3, 4);
        expect(a.plus(b)).toEqual(new Point(4, 6));
    });

    test('cannot create point with float', () => {
        expect(() => {
            new Point(1.2, 0);
        }).toThrow(Error('location must be integer'));

        expect(() => {
            new Point(1, 1.5);
        }).toThrow(Error('location must be integer'));
    });
});

describe('DirectedPoint test', () => {
    test('equals', () => {
        const a = new DirectedPoint(Direction.HORIZONTAL, new Point(1, 2));
        expect(a.equals(new DirectedPoint(Direction.HORIZONTAL, new Point(1, 2)))).toBe(true);
        expect(a.equals(new DirectedPoint(Direction.VERTICAL, new Point(1, 2)))).toBe(false);
        expect(a.equals(new DirectedPoint(Direction.HORIZONTAL, new Point(2, 1)))).toBe(false);
    });

    test('plus', () => {
        const a = new DirectedPoint(Direction.HORIZONTAL, new Point(1, 2));
        expect(a.plus(new Point(3, 4))).toEqual(
            new DirectedPoint(Direction.HORIZONTAL, new Point(4, 6)),
        );
    });
});
