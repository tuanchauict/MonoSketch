package mono.shape.extra

import mono.shape.ShapeExtraManager
import mono.shape.extra.style.RectangleFillStyle
import mono.shape.extra.style.StraightStrokeStyle
import mono.shape.serialization.SerializableRectangle
import mono.shape.extra.style.StraightStrokeDashPattern

/**
 * A [ShapeExtra] for [mono.shape.shape.Rectangle]
 */
data class RectangleExtra(
    val isFillEnabled: Boolean,
    val userSelectedFillStyle: RectangleFillStyle,
    val isBorderEnabled: Boolean,
    val userSelectedBorderStyle: StraightStrokeStyle,
    val dashPattern: StraightStrokeDashPattern
) : ShapeExtra() {
    val fillStyle: RectangleFillStyle?
        get() = if (isFillEnabled) userSelectedFillStyle else null

    val strokeStyle: StraightStrokeStyle?
        get() = if (isBorderEnabled) userSelectedBorderStyle else null

    constructor(serializableExtra: SerializableRectangle.SerializableExtra) : this(
        serializableExtra.isFillEnabled,
        ShapeExtraManager.getRectangleFillStyle(serializableExtra.userSelectedFillStyleId),
        serializableExtra.isBorderEnabled,
        ShapeExtraManager.getRectangleBorderStyle(serializableExtra.userSelectedBorderStyleId),
        StraightStrokeDashPattern.fromSerializableValue(serializableExtra.dashPattern)
    )

    fun toSerializableExtra(): SerializableRectangle.SerializableExtra =
        SerializableRectangle.SerializableExtra(
            isFillEnabled = isFillEnabled,
            userSelectedFillStyleId = userSelectedFillStyle.id,
            isBorderEnabled = isBorderEnabled,
            userSelectedBorderStyleId = userSelectedBorderStyle.id,
            dashPattern = dashPattern.toSerializableValue()
        )
}
