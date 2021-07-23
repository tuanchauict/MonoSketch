package mono.shape.extra.manager.model

import mono.graphics.bitmap.drawable.Drawable

/**
 * A class for defining a border style for rectangle.
 *
 * @param id is the key for retrieving predefined [RectangleBorderStyle] when serialization.
 * @param displayName is the text visible on the UI tool for selection.
 */
class RectangleBorderStyle(
    val id: String,
    val displayName: String,
    val drawable: Drawable
)
