export class IntRange implements Iterable<number> {
    constructor(
        public start: number,
        public endExclusive: number,
        public step: number = 1,
    ) {
        if (start > endExclusive) {
            throw Error('start must be less than or equal to end');
        }
        if (step === 0) {
            throw Error('step must not be zero');
        }
    }

    [Symbol.iterator](): Iterator<number> {
        return range(this.start, this.endExclusive, this.step);
    }

    contains(value: number): boolean {
        return value >= this.start && value < this.endExclusive;
    }
}

export function* range(start: number, end: number, step: number = 1): Generator<number> {
    if (step === 0) {
        throw Error('step must not be zero');
    }
    if (step < 0) {
        for (let i = start; i > end; i += step) {
            yield i;
        }
    } else {
        for (let i = start; i < end; i += step) {
            yield i;
        }
    }
}

export function* zip<T1, T2>(iter1: Iterable<T1>, iter2: Iterable<T2>): Generator<[T1, T2]> {
    const iterator1 = iter1[Symbol.iterator]();
    const iterator2 = iter2[Symbol.iterator]();
    let index = 0;
    while (true) {
        const result1 = iterator1.next();
        const result2 = iterator2.next();
        if (result1.done || result2.done) {
            break;
        }
        yield [result1.value, result2.value];
        index++;
    }
}

export function map<T, U>(iterable: Iterable<T>, func: (value: T) => U): U[] {
    const result: U[] = [];
    for (const value of iterable) {
        result.push(func(value));
    }
    return result;
}

export function mapNotNull<T, U>(iterable: Iterable<T>, func: (value: T) => U | null): U[] {
    const result: U[] = [];
    for (const value of iterable) {
        const mapped = func(value);
        if (mapped !== null) {
            result.push(mapped);
        }
    }
    return result;
}

export function mapIndexed<T, U>(iterable: Iterable<T>, func: (index: number, value: T) => U): U[] {
    const result: U[] = [];
    let index = 0;
    for (const value of iterable) {
        result.push(func(index, value));
        index++;
    }
    return result;
}

export function mapIndexedNotNull<T, U>(
    iterable: Iterable<T>,
    func: (index: number, value: T) => U | null,
): U[] {
    const result: U[] = [];
    let index = 0;
    for (const value of iterable) {
        const mapped = func(index, value);
        if (mapped !== null) {
            result.push(mapped);
        }
        index++;
    }
    return result;
}

export function filter<T>(iterable: Iterable<T>, func: (value: T) => boolean): T[] {
    const result: T[] = [];
    for (const value of iterable) {
        if (func(value)) {
            result.push(value);
        }
    }
    return result;
}

/**
 * Find the index of the first element that satisfies the predicate.
 * @param array
 * @param comparator A function that returns a negative number if the value is less than the target,
 * a positive number if the value is greater than the target, or zero if the value is equal to the
 * target.
 * @param withIn The range to search within.
 * @returns @return the index of the found element, if it is contained in the list within the specified range;
 *  otherwise, the inverted insertion point `(-insertion point - 1)`.
 *  The insertion point is defined as the index at which the element should be inserted,
 *  so that the list (or the specified subrange of list) still remains sorted.
 */
export function binarySearch<T, X>(
    array: T[],
    comparator: (value: T) => number,
    withIn: number[] = [0, array.length],
): number {
    let low = withIn[0];
    let high = withIn[1] - 1;
    while (low <= high) {
        const mid = Math.floor((low + high) / 2);
        const compareResult = comparator(array[mid]);
        if (compareResult < 0) {
            low = mid + 1;
        } else if (compareResult > 0) {
            high = mid - 1;
        } else {
            return mid; // key found
        }
    }
    return -(low + 1); // key not found
}

export function getOrNull<T>(array: T[], index: number): T | null {
    return index >= 0 && index < array.length ? array[index] : null;
}

export function getOrDefault<T>(array: T[], index: number, defaultValue: T): T {
    return index >= 0 && index < array.length ? array[index] : defaultValue;
}

export namespace ListExt {
    /**
     * Create a list of the specified size with the specified value.
     * @param size
     * @param value
     */
    export function list<T>(size: number, value: () => T): T[] {
        const result: T[] = [];
        for (let i = 0; i < size; i++) {
            result.push(value());
        }
        return result;
    }
}

export namespace MapExt {
    /**
     * Get the value from the map with the specified key. If the key is not found, put the value
     * created by the specified function into the map and return it.
     * @param map
     * @param key
     * @param value
     */
    export function getOrPut<K, V>(map: Map<K, V>, key: K, value: () => V): V {
        let result = map.get(key);
        if (result === undefined) {
            result = value();
            map.set(key, result);
        }
        return result;
    }
}
