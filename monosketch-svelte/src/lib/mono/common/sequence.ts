export class IntRange implements Iterable<number> {
    constructor(
        public start: number,
        public end: number,
        public step: number = 1,
    ) {}

    [Symbol.iterator](): Iterator<number> {
        return range(this.start, this.end, this.step);
    }

    contains(value: number): boolean {
        return value >= this.start && value < this.end;
    }
}

export function* range(start: number, end: number, step: number = 1): Generator<number> {
    for (let i = start; i < end; i += step) {
        yield i;
    }
}
