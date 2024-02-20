import type { Char } from '$libs/char';

/**
 * An interface for the pixel of the Mono Bitmap.
 */
export interface Pixel {
    visualChar: Char;
    directionChar: Char;
    highlight: HighlightType;

    isTransparent: boolean;
}

export enum HighlightType {
    NO,
    SELECTED,
    TEXT_EDITING,
    LINE_CONNECT_FOCUSING
}
