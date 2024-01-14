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
