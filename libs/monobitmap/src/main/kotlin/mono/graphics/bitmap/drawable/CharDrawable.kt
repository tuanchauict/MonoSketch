package mono.graphics.bitmap.drawable

import mono.graphics.bitmap.MonoBitmap

/**
 * A drawable which simplify fills with [char].
 */
class CharDrawable(private val char: Char) : Drawable {
    override fun toBitmap(width: Int, height: Int): MonoBitmap {
        val builder = MonoBitmap.Builder(width, height)
        builder.fill(char)
        return builder.toBitmap()
    }
}
