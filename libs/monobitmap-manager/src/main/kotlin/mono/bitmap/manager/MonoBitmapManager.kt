package mono.bitmap.manager

import mono.bitmap.manager.factory.LineBitmapFactory
import mono.bitmap.manager.factory.RectangleBitmapFactory
import mono.bitmap.manager.factory.TextBitmapFactory
import mono.graphics.bitmap.MonoBitmap
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text

/**
 * A model class which manages and caches bitmap of shapes.
 * Cache-hit when both id and version of the shape valid in the cache, otherwise, cache-miss.
 */
class MonoBitmapManager {
    private val idToBitmapMap: MutableMap<String, VersionizedBitmap> = mutableMapOf()

    fun getBitmap(shape: AbstractShape): MonoBitmap? {
        val cachedBitmap = getCacheBitmap(shape)
        if (cachedBitmap != null) {
            return cachedBitmap
        }

        val bitmap = when (shape) {
            is Rectangle -> RectangleBitmapFactory.toBitmap(
                shape.bound.size,
                shape.extra
            )
            is Text -> TextBitmapFactory.toBitmap(
                shape.bound.size,
                shape.renderableText.getRenderableText(),
                shape.extra,
                shape.isTextEditing
            )
            is Line -> LineBitmapFactory.toBitmap(
                shape.reducedJoinPoints,
                shape.extra,
            )

            is Group -> null // No draw group since it change very frequently.
            is MockShape -> null // Only for testing.
        } ?: return null
        idToBitmapMap[shape.id] = VersionizedBitmap(shape.versionCode, bitmap)
        return bitmap
    }

    private fun getCacheBitmap(shape: AbstractShape): MonoBitmap? =
        idToBitmapMap[shape.id]?.takeIf { it.versionCode == shape.versionCode }?.bitmap

    private class VersionizedBitmap(val versionCode: Int, val bitmap: MonoBitmap)
}
