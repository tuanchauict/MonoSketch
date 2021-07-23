package mono.shape.extra.manager.model

import mono.graphics.bitmap.drawable.Drawable

/**
 * A class for defining a fill style for rectangle.
 *
 * @param id is the key for retrieving predefined [RectangleFillStyle] when serialization.
 * @param displayName is the text visible on the UI tool for selection.
 */
class RectangleFillStyle(
    val id: String,
    val displayName: String,
    val drawable: Drawable
)
