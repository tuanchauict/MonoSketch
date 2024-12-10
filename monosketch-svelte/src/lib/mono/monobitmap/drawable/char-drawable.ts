import { MonoBitmap } from "$mono/monobitmap/bitmap/monobitmap";
import type { Drawable } from "$mono/monobitmap/drawable/drawable";

/**
 * A drawable which simplify fills with [char].
 */
export class CharDrawable implements Drawable {
    constructor(private readonly char: string) {
    }

    toBitmap(width: number, height: number): MonoBitmap.Bitmap {
        const builder = new MonoBitmap.Builder(width, height);
        builder.fillAll(this.char);
        return builder.toBitmap();
    }
}