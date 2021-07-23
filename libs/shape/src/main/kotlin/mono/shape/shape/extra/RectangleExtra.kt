package mono.shape.shape.extra

import mono.shape.extra.manager.ShapeExtraManager
import mono.shape.extra.manager.model.RectangleBorderStyle
import mono.shape.extra.manager.model.RectangleFillStyle
import mono.shape.serialization.SerializableRectangle

/**
 * A [ShapeExtra] for [mono.shape.shape.Rectangle]
 */
data class RectangleExtra(
    val isFillEnabled: Boolean = false,
    val userSelectedFillStyle: RectangleFillStyle,
    val isBorderEnabled: Boolean,
    val userSelectedBorderStyle: RectangleBorderStyle
) : ShapeExtra() {
    val fillStyle: RectangleFillStyle
        get() =
            if (isFillEnabled) {
                userSelectedFillStyle
            } else {
                ShapeExtraManager.RECTANGLE_STYLE_NO_FILLED
            }

    val borderStyle: RectangleBorderStyle
        get() =
            if (isBorderEnabled) {
                userSelectedBorderStyle
            } else {
                ShapeExtraManager.RECTANGLE_STYLE_NO_BORDER
            }

    constructor(serializableExtra: SerializableRectangle.SerializableExtra) : this(
        serializableExtra.isFillEnabled,
        ShapeExtraManager.getRectangleFillStyle(serializableExtra.userSelectedFillStyleId)
            ?: ShapeExtraManager.DEFAULT_RECTANGLE_FILL_STYLE,
        serializableExtra.isBorderEnabled,
        ShapeExtraManager.getRectangleBorderStyle(serializableExtra.userSelectedBorderStyleId)
            ?: ShapeExtraManager.DEFAULT_RECTANGLE_BORDER_STYLE
    )

    fun toSerializableExtra(): SerializableRectangle.SerializableExtra =
        SerializableRectangle.SerializableExtra(
            isFillEnabled = isFillEnabled,
            userSelectedFillStyleId = userSelectedFillStyle.id,
            isBorderEnabled = isBorderEnabled,
            userSelectedBorderStyleId = userSelectedBorderStyle.id
        )

    companion object {
        val DEFAULT = RectangleExtra(
            isFillEnabled = false,
            ShapeExtraManager.DEFAULT_RECTANGLE_FILL_STYLE,
            isBorderEnabled = true,
            ShapeExtraManager.DEFAULT_RECTANGLE_BORDER_STYLE
        )
    }
}
