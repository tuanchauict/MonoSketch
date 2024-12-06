/*
 * Copyright (c) 2024, tuanchauict
 */

import { StraightStrokeDashPattern } from "$mono/shape/extra/style";
import { describe, it, expect } from 'vitest';

describe('StraightStrokeDashPattern', () => {
    it('testIsGap_solid', () => {
        const pattern = new StraightStrokeDashPattern(1, 0, 0);
        expect(pattern.isGap(0)).toBe(false);
        expect(pattern.isGap(1)).toBe(false);
        expect(pattern.isGap(2)).toBe(false);
        expect(pattern.isGap(3)).toBe(false);
    });

    it('testIsGap_1segment_1gap_0offset', () => {
        const pattern = new StraightStrokeDashPattern(1, 1, 0);
        expect(pattern.isGap(0)).toBe(false);
        expect(pattern.isGap(1)).toBe(true);
        expect(pattern.isGap(2)).toBe(false);
        expect(pattern.isGap(3)).toBe(true);
        expect(pattern.isGap(4)).toBe(false);
        expect(pattern.isGap(5)).toBe(true);
    });

    it('testIsGap_1segment_1gap_1offset', () => {
        const pattern = new StraightStrokeDashPattern(1, 1, 1);
        expect(pattern.isGap(0)).toBe(true);
        expect(pattern.isGap(1)).toBe(false);
        expect(pattern.isGap(2)).toBe(true);
        expect(pattern.isGap(3)).toBe(false);
        expect(pattern.isGap(4)).toBe(true);
        expect(pattern.isGap(5)).toBe(false);
    });

    it('testIsGap_1segment_1gap_minus1offset', () => {
        const pattern = new StraightStrokeDashPattern(1, 1, -1);
        expect(pattern.isGap(0)).toBe(true);
        expect(pattern.isGap(1)).toBe(false);
        expect(pattern.isGap(2)).toBe(true);
        expect(pattern.isGap(3)).toBe(false);
        expect(pattern.isGap(4)).toBe(true);
        expect(pattern.isGap(5)).toBe(false);
    });

    it('testIsGap_2segment_1gap_0offset', () => {
        const pattern = new StraightStrokeDashPattern(2, 1, 0);
        expect(pattern.isGap(0)).toBe(false);
        expect(pattern.isGap(1)).toBe(false);
        expect(pattern.isGap(2)).toBe(true);
        expect(pattern.isGap(3)).toBe(false);
        expect(pattern.isGap(4)).toBe(false);
        expect(pattern.isGap(5)).toBe(true);
    });

    it('testIsGap_2segment_1gap_1offset', () => {
        const pattern = new StraightStrokeDashPattern(2, 1, 1);
        expect(pattern.isGap(0)).toBe(false);
        expect(pattern.isGap(1)).toBe(true);
        expect(pattern.isGap(2)).toBe(false);
        expect(pattern.isGap(3)).toBe(false);
        expect(pattern.isGap(4)).toBe(true);
        expect(pattern.isGap(5)).toBe(false);
    });
});
