package mono.shape.shape.extra

import mono.shape.extra.manager.ShapeExtraManager
import mono.shape.extra.manager.model.RectangleFillStyle
import mono.shape.extra.manager.model.StraightStrokeDashPattern
import mono.shape.extra.manager.model.StraightStrokeStyle
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle
import mono.shape.serialization.SerializableRectangle

/**
 * A [ShapeExtra] for [mono.shape.shape.Rectangle]
 */
data class RectangleExtra(
    val isFillEnabled: Boolean = false,
    val userSelectedFillStyle: RectangleFillStyle,
    val isBorderEnabled: Boolean,
    val userSelectedBorderStyle: StraightStrokeStyle,
    // TODO: Store dash pattern
    val dashPattern: StraightStrokeDashPattern = StraightStrokeDashPattern.SOLID
) : ShapeExtra() {
    val fillStyle: RectangleFillStyle
        get() =
            if (isFillEnabled) {
                userSelectedFillStyle
            } else {
                ShapeExtraManager.RECTANGLE_STYLE_NO_FILLED
            }

    val strokeStyle: StraightStrokeStyle
        get() =
            if (isBorderEnabled) {
                userSelectedBorderStyle
            } else {
                PredefinedStraightStrokeStyle.NO_STROKE
            }

    constructor(serializableExtra: SerializableRectangle.SerializableExtra) : this(
        serializableExtra.isFillEnabled,
        ShapeExtraManager.getRectangleFillStyle(serializableExtra.userSelectedFillStyleId),
        serializableExtra.isBorderEnabled,
        ShapeExtraManager.getRectangleBorderStyle(serializableExtra.userSelectedBorderStyleId)
    )

    fun toSerializableExtra(): SerializableRectangle.SerializableExtra =
        SerializableRectangle.SerializableExtra(
            isFillEnabled = isFillEnabled,
            userSelectedFillStyleId = userSelectedFillStyle.id,
            isBorderEnabled = isBorderEnabled,
            userSelectedBorderStyleId = userSelectedBorderStyle.id
        )

    companion object {
        fun withDefault(): RectangleExtra {
            val defaultExtraState = ShapeExtraManager.defaultExtraState
            return RectangleExtra(
                defaultExtraState.isFillEnabled,
                defaultExtraState.fillStyle,
                defaultExtraState.isBorderEnabled,
                defaultExtraState.borderStyle
            )
        }
    }
}
