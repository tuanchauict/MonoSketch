import { describe, expect, test } from 'vitest';
import {
    binarySearch,
    getOrDefault,
    getOrNull,
    IntRange,
    map,
    mapIndexed,
    mapIndexedNotNull,
    mapNotNull,
    range,
    zip,
} from '$libs/sequence';

describe('test IntRange', () => {
    test('invalid constructions', () => {
        expect(() => {
            new IntRange(1, 0);
        }).toThrow(Error('start must be less than or equal to end'));

        expect(() => {
            new IntRange(1, 2, 0);
        }).toThrow(Error('step must not be zero'));
    });

    test('contains', () => {
        const r = new IntRange(1, 3);
        expect(r.contains(1)).toBe(true);
        expect(r.contains(2)).toBe(true);
        expect(r.contains(3)).toBe(false);
        expect(r.contains(0)).toBe(false);
        expect(r.contains(4)).toBe(false);
    });

    test('iterator', () => {
        const r = new IntRange(1, 4);
        const result = [];
        for (const i of r) {
            result.push(i);
        }
        expect(result).toEqual([1, 2, 3]);
    });
});

describe('test range', () => {
    test('range', () => {
        const result = [];
        for (const i of range(1, 5)) {
            result.push(i);
        }
        expect(result).toEqual([1, 2, 3, 4]);
    });

    test('range with step', () => {
        const result = [];
        for (const i of range(1, 5, 2)) {
            result.push(i);
        }
        expect(result).toEqual([1, 3]);
    });

    test('start is less than end', () => {
        const result = [];
        for (const i of range(5, 1)) {
            result.push(i);
        }
        expect(result).toEqual([]);
    });

    test('range with negative step', () => {
        const result = [];
        for (const i of range(5, 1, -1)) {
            result.push(i);
        }
        expect(result).toEqual([5, 4, 3, 2]);
    });

    test('range with negative step and start is greater than end', () => {
        const result = [];
        for (const i of range(1, 5, -1)) {
            result.push(i);
        }
        expect(result).toEqual([]);
    });

    test('range with zero step', () => {
        expect(() => {
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
            for (const _ of range(1, 5, 0)) {
                // do nothing
            }
        }).toThrow(Error('step must not be zero'));
    });
});

describe('test zip', () => {
    test('zip', () => {
        const result = [];
        for (const [a, b] of zip([1, 2, 3], [4, 5, 6])) {
            result.push([a, b]);
        }
        expect(result).toEqual([
            [1, 4],
            [2, 5],
            [3, 6],
        ]);
    });

    test('zip with different lengths', () => {
        const result = [];
        for (const [a, b] of zip([1, 2, 3], [4, 5])) {
            result.push([a, b]);
        }
        expect(result).toEqual([
            [1, 4],
            [2, 5],
        ]);
    });
});

describe('test map', () => {
    test('map', () => {
        expect(map([1, 2, 3], (value) => value * 2)).toEqual([2, 4, 6]);
    });

    test('mapNotNull', () => {
        const result = mapNotNull([1, 2, 3], (value) => (value % 2 === 0 ? value * 2 : null));
        expect(result).toEqual([4]);
    });

    test('mapIndexed', () => {
        const result = mapIndexed([1, 2, 3], (index, value) => [index, value]);
        expect(result).toEqual([
            [0, 1],
            [1, 2],
            [2, 3],
        ]);
    });

    test('mapIndexedNotNull', () => {
        const result = mapIndexedNotNull([1, 2, 3], (index, value) =>
            index % 2 === 0 ? value : null,
        );
        expect(result).toEqual([1, 3]);
    });
});

describe('test filter', () => {
    test('filter', () => {
        expect([1, 2, 3].filter((value) => value % 2 !== 0)).toEqual([1, 3]);
    });
});

describe('test binarySearch', () => {
    test('happy', () => {
        const array = [2, 3, 4, 5];

        expect(binarySearch(array, (value) => value - 1)).toBe(-1);
        expect(binarySearch(array, (value) => value - 2)).toBe(0);
        expect(binarySearch(array, (value) => value - 3)).toBe(1);
        expect(binarySearch(array, (value) => value - 4)).toBe(2);
        expect(binarySearch(array, (value) => value - 5)).toBe(3);
        expect(binarySearch(array, (value) => value - 6)).toBe(-5);
    });

    test('happy withIn range', () => {
        const array = [2, 3, 4, 5, 6, 7, 8, 9, 10];

        expect(binarySearch(array, (value) => value - 1, [1, 4])).toBe(-2);
        expect(binarySearch(array, (value) => value - 2, [1, 4])).toBe(-2);
        expect(binarySearch(array, (value) => value - 3, [1, 4])).toBe(1);
        expect(binarySearch(array, (value) => value - 4, [1, 4])).toBe(2);
        expect(binarySearch(array, (value) => value - 5, [1, 4])).toBe(3);
        expect(binarySearch(array, (value) => value - 6, [1, 4])).toBe(-5);
    });
});

describe('test getOrNull', () => {
    test('happy', () => {
        const array = [1, 2, 3, 4];
        expect(getOrNull(array, 0)).toBe(1);
        expect(getOrNull(array, 1)).toBe(2);
        expect(getOrNull(array, 2)).toBe(3);
        expect(getOrNull(array, 3)).toBe(4);
        expect(getOrNull(array, 4)).toBeNull();
        expect(getOrNull(array, -1)).toBeNull();
    });
});

describe('test getOrDefault', () => {
    test('happy', () => {
        const array = [1, 2, 3, 4];
        expect(getOrDefault(array, 0, 0)).toBe(1);
        expect(getOrDefault(array, 1, 0)).toBe(2);
        expect(getOrDefault(array, 2, 0)).toBe(3);
        expect(getOrDefault(array, 3, 0)).toBe(4);
        expect(getOrDefault(array, 4, 0)).toBe(0);
        expect(getOrDefault(array, -1, 0)).toBe(0);
    });
});
