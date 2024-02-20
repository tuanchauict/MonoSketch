import type { Pixel } from '$mono/monobitmap/board/pixel';

/**
 * An interface for the board of the Mono Bitmap.
 */
export interface MonoBoard {
    get(left: number, top: number): Pixel;
}
