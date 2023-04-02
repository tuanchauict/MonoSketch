/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.extra.style

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
