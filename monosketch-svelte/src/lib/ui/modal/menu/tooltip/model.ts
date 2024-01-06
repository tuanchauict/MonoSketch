import type { TargetBounds } from '../../model';

export enum Direction {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT,
}

export class Tooltip {
    constructor(
        public text: string,
        public direction: Direction,
        public targetBounds: TargetBounds,
    ) {}
}

export const directionToIconPathMap = {
    [Direction.TOP]: 'M10 10L0 0H20L10 10Z',
    [Direction.BOTTOM]: 'M10 0L20 10H0L10 0Z',
    [Direction.LEFT]: 'M10 10L0 20V0L10 10Z',
    [Direction.RIGHT]: 'M0 10L10 0V20L0 10Z',
};
