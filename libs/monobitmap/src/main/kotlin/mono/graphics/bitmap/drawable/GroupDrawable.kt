package mono.graphics.bitmap.drawable

import mono.graphics.bitmap.MonoBitmap
import mono.graphics.bitmap.MonoBitmapManager
import mono.shape.shape.Group

object GroupDrawable {
    fun toBitmap(bitmapManager: MonoBitmapManager, group: Group): MonoBitmap {
        val bound = group.bound
        val builder = MonoBitmap.Builder(bound.width, bound.height)
        for (shape in group.items) {
            val bitmap = bitmapManager.getBitmap(shape) ?: continue
            val shapeBound = shape.bound
            val offsetRow = shapeBound.top - bound.top
            val offsetCol = shapeBound.left - bound.left
            builder.fill(offsetRow, offsetCol, bitmap)
        }
        return builder.toBitmap()
    }
}
