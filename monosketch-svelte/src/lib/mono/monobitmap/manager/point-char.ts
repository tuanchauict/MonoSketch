/*
 * Copyright (c) 2024, tuanchauict
 */

import { range } from "$libs/sequence";

/**
 * A class representing a char at a point for generating bitmap.
 */
export class PointChar {
    constructor(public left: number, public top: number, public char: string) {
    }

    static point(left: number, top: number, char: string): PointChar[] {
        return [new PointChar(left, top, char)];
    }

    static horizontalLine(beginExclusive: number, endExclusive: number, top: number, char: string): PointChar[] {
        if (Math.abs(beginExclusive - endExclusive) <= 1) {
            return [];
        }
        const delta = beginExclusive < endExclusive ? 1 : -1;
        const begin = beginExclusive + delta;
        return Array.from(range(begin, endExclusive, delta), (left, _) => new PointChar(left, top, char));
    }

    static verticalLine(left: number, beginExclusive: number, endExclusive: number, char: string): PointChar[] {
        if (Math.abs(beginExclusive - endExclusive) <= 1) {
            return [];
        }
        const delta = beginExclusive < endExclusive ? 1 : -1;
        const begin = beginExclusive + delta;
        return Array.from(range(begin, endExclusive, delta), (top, _) => new PointChar(left, top, char));
    }
}
