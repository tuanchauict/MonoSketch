import { describe, expect, test } from 'vitest';
import { Size, SizeF } from '$mono/graphics-geo/size';

describe('Size test', () => {
    test('constructor', () => {
        expect(() => {
            new Size(1.2, 0);
        }).toThrow(Error('size must be integer'));

        expect(() => {
            new Size(1, 1.5);
        }).toThrow(Error('size must be integer'));
    });

    test('equals', () => {
        const a = new Size(1, 2);
        expect(a.equals(new Size(1, 2))).toBe(true);
        expect(a.equals(new Size(2, 1))).toBe(false);
        expect(a.equals({ width: 1, height: 2 })).toBe(false);
        expect(a.equals(null)).toBe(false);
        expect(a.equals(undefined)).toBe(false);
    });
});

describe('SizeF test', () => {
    test('equals', () => {
        const a = new SizeF(1.1, 2.2);
        expect(a.equals(new SizeF(1.1, 2.2))).toBe(true);
        expect(a.equals(new SizeF(1.2, 2))).toBe(false);
        expect(a.equals(new SizeF(1.1, 2.1))).toBe(false);
        expect(a.equals({ width: 1.1, height: 2.2 })).toBe(false);
        expect(a.equals(null)).toBe(false);
        expect(a.equals(undefined)).toBe(false);
    });
});
