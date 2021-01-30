package mono.graphics.bitmap

import mono.graphics.bitmap.drawable.GroupDrawable
import mono.graphics.bitmap.drawable.RectangleDrawable
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Rectangle

/**
 * A model class which manages and caches bitmap of shapes.
 * Cache-hit when both id and version of the shape valid in the cache, otherwise, cache-miss.
 *
 * Call [invalidate] to clean up cache of a shape.
 */
class MonoBitmapManager {
    private val idToBitmapMap: MutableMap<Int, VersionizedBitmap> = mutableMapOf()

    fun getBitmap(shape: AbstractShape): MonoBitmap? {
        val cachedBitmap = getCacheBitmap(shape)
        if (cachedBitmap != null) {
            return cachedBitmap
        }

        val bitmap = when (shape) {
            is Rectangle -> RectangleDrawable.toBitmap(shape)
            is Group -> null // No draw group since it change very frequently.
            else -> null
        } ?: return null
        idToBitmapMap[shape.id] = VersionizedBitmap(shape.version, bitmap)
        return bitmap
    }

    fun invalidate(shape: AbstractShape) {
        idToBitmapMap.remove(shape.id)
    }

    private fun getCacheBitmap(shape: AbstractShape): MonoBitmap? =
        idToBitmapMap[shape.id]?.takeIf { it.version == shape.version }?.bitmap

    private class VersionizedBitmap(val version: Int, val bitmap: MonoBitmap)
}
