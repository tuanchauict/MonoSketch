import { describe, expect, test } from 'vitest';
import { Rect } from '$mono/graphics-geo/rect';
import { Point } from '$mono/graphics-geo/point';

describe('Rect test', () => {
    test('byLTRB', () => {
        const r = Rect.byLTRB(1, 2, 3, 4);
        expect(r.left).toBe(1);
        expect(r.top).toBe(2);
        expect(r.right).toBe(3);
        expect(r.bottom).toBe(4);
        expect(r.width).toBe(3);
        expect(r.height).toBe(3);
    });

    test('byLTRB with flipped', () => {
        const r = Rect.byLTRB(5, 10, 4, 6);
        expect(r.left).toBe(4);
        expect(r.top).toBe(6);
        expect(r.right).toBe(5);
        expect(r.bottom).toBe(10);
        expect(r.width).toBe(2);
        expect(r.height).toBe(5);
    });

    test('byLTWH', () => {
        const r = Rect.byLTWH(4, 6, 10, 42);
        expect(r.left).toBe(4);
        expect(r.top).toBe(6);
        expect(r.right).toBe(13);
        expect(r.bottom).toBe(47);
        expect(r.width).toBe(10);
        expect(r.height).toBe(42);
    });

    test('properties', () => {
        const r = Rect.byLTRB(1, 2, 3, 4);
        expect(r.width).toBe(3);
        expect(r.height).toBe(3);
        expect(r.left).toBe(1);
        expect(r.top).toBe(2);
        expect(r.right).toBe(3);
    });

    test('equals', () => {
        const r = Rect.byLTRB(1, 2, 3, 4);
        expect(r.equals(Rect.byLTRB(1, 2, 3, 4))).toBe(true);
        expect(r.equals(Rect.byLTRB(1, 2, 3, 5))).toBe(false);
        expect(r.equals(Rect.byLTRB(1, 2, 4, 4))).toBe(false);
        expect(r.equals(Rect.byLTRB(1, 3, 3, 4))).toBe(false);
        expect(r.equals(Rect.byLTRB(2, 2, 3, 4))).toBe(false);
        expect(r.equals({ left: 1, top: 2, right: 3, bottom: 4 })).toBe(false);
        expect(r.equals(null)).toBe(false);
        expect(r.equals(undefined)).toBe(false);
    });

    test('contains', () => {
        const r = Rect.byLTRB(1, 2, 3, 4);
        expect(r.contains(Point.of(1, 2))).toBe(true);
        expect(r.contains(Point.of(2, 3))).toBe(true);
        expect(r.contains(Point.of(3, 4))).toBe(true);
        expect(r.contains(Point.of(0, 0))).toBe(false);
        expect(r.contains(Point.of(1, 1))).toBe(false);
        expect(r.contains(Point.of(3, 5))).toBe(false);
        expect(r.contains(Point.of(4, 4))).toBe(false);
    });

    test('isOverlapped', () => {
        const r = Rect.byLTRB(1, 2, 3, 4);
        expect(r.isOverlapped(Rect.byLTRB(1, 2, 3, 4))).toBe(true);
        expect(r.isOverlapped(Rect.byLTRB(0, 1, 2, 3))).toBe(true);
        expect(r.isOverlapped(Rect.byLTRB(2, 3, 4, 5))).toBe(true);
        expect(r.isOverlapped(Rect.byLTRB(0, 1, 4, 5))).toBe(true);
        expect(r.isOverlapped(Rect.byLTRB(0, 1, 1, 2))).toBe(true);
        expect(r.isOverlapped(Rect.byLTRB(3, 4, 4, 5))).toBe(true);
        expect(r.isOverlapped(Rect.byLTRB(0, 1, 1, 3))).toBe(true);
        expect(r.isOverlapped(Rect.byLTRB(2, 3, 4, 6))).toBe(true);
        expect(r.isOverlapped(Rect.byLTRB(0, 1, 4, 6))).toBe(true);

        expect(r.isOverlapped(Rect.byLTRB(0, 1, 1, 1))).toBe(false);
        expect(r.isOverlapped(Rect.byLTRB(4, 5, 6, 7))).toBe(false);
        expect(r.isOverlapped(Rect.byLTRB(0, 1, 0, 1))).toBe(false);
        expect(r.isOverlapped(Rect.byLTRB(4, 5, 4, 5))).toBe(false);
    });

    test('getOverlappedRect', () => {
        const r = Rect.byLTRB(1, 2, 3, 4);
        expect(r.getOverlappedRect(Rect.byLTRB(1, 2, 3, 4))).toEqual(Rect.byLTRB(1, 2, 3, 4));
        expect(r.getOverlappedRect(Rect.byLTRB(0, 1, 2, 3))).toEqual(Rect.byLTRB(1, 2, 2, 3));
        expect(r.getOverlappedRect(Rect.byLTRB(2, 3, 4, 5))).toEqual(Rect.byLTRB(2, 3, 3, 4));
        expect(r.getOverlappedRect(Rect.byLTRB(0, 1, 4, 5))).toEqual(Rect.byLTRB(1, 2, 3, 4));
        expect(r.getOverlappedRect(Rect.byLTRB(0, 1, 1, 2))).toEqual(Rect.byLTRB(1, 2, 1, 2));
        expect(r.getOverlappedRect(Rect.byLTRB(3, 4, 4, 5))).toEqual(Rect.byLTRB(3, 4, 3, 4));
        expect(r.getOverlappedRect(Rect.byLTRB(0, 1, 1, 3))).toEqual(Rect.byLTRB(1, 2, 1, 3));
        expect(r.getOverlappedRect(Rect.byLTRB(2, 3, 4, 6))).toEqual(Rect.byLTRB(2, 3, 3, 4));
        expect(r.getOverlappedRect(Rect.byLTRB(0, 1, 4, 6))).toEqual(Rect.byLTRB(1, 2, 3, 4));

        expect(r.getOverlappedRect(Rect.byLTRB(0, 1, 1, 1))).toBe(null);
        expect(r.getOverlappedRect(Rect.byLTRB(4, 5, 6, 7))).toBe(null);
        expect(r.getOverlappedRect(Rect.byLTRB(0, 1, 0, 1))).toBe(null);
        expect(r.getOverlappedRect(Rect.byLTRB(4, 5, 4, 5))).toBe(null);
    });

    test('isVertex', () => {
        const r = Rect.byLTRB(1, 2, 3, 4);
        expect(r.isVertex(Point.of(1, 2))).toBe(true);
        expect(r.isVertex(Point.of(3, 2))).toBe(true);
        expect(r.isVertex(Point.of(1, 4))).toBe(true);
        expect(r.isVertex(Point.of(3, 4))).toBe(true);
        expect(r.isVertex(Point.of(2, 2))).toBe(false);
        expect(r.isVertex(Point.of(1, 3))).toBe(false);
        expect(r.isVertex(Point.of(3, 3))).toBe(false);
        expect(r.isVertex(Point.of(2, 4))).toBe(false);
    });
});
