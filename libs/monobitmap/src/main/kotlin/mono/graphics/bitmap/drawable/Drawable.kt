package mono.graphics.bitmap.drawable

import mono.graphics.bitmap.MonoBitmap

/**
 * An interface for drawable which is the minimal version of a bitmap. A drawable contains enough
 * information for generating any-size bitmap.
 */
interface Drawable {
    fun toBitmap(width: Int, height: Int): MonoBitmap
}
