import type { Rect } from '$libs/graphics-geo/rect';

export enum TooltipDirection {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT,
}

export interface Tooltip {
    text: string;
    direction: TooltipDirection;
    targetBounds: Rect;
}

export const directionToIconPathMap = {
    [TooltipDirection.TOP]: 'M10 10L0 0H20L10 10Z',
    [TooltipDirection.BOTTOM]: 'M10 0L20 10H0L10 0Z',
    [TooltipDirection.LEFT]: 'M10 10L0 20V0L10 10Z',
    [TooltipDirection.RIGHT]: 'M0 10L10 0V20L0 10Z',
};
