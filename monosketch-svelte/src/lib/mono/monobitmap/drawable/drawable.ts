import { MonoBitmap } from "$mono/monobitmap/bitmap/monobitmap";

/**
 * An interface for drawable which is the minimal version of a bitmap. A drawable contains enough
 * information for generating any-size bitmap.
 */
export interface Drawable {
    toBitmap(width: number, height: number): MonoBitmap.Bitmap;
}
