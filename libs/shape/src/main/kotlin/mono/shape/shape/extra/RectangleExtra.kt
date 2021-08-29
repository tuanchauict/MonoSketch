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
    val isFillEnabled: Boolean,
    val userSelectedFillStyle: RectangleFillStyle,
    val isBorderEnabled: Boolean,
    val userSelectedBorderStyle: StraightStrokeStyle,
    val isDashEnabled: Boolean = false,
    val userDefinedDashPattern: StraightStrokeDashPattern = StraightStrokeDashPattern.SOLID
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

    val dashPattern: StraightStrokeDashPattern
        get() =
            if (isDashEnabled) {
                userDefinedDashPattern
            } else {
                StraightStrokeDashPattern.SOLID
            }

    constructor(serializableExtra: SerializableRectangle.SerializableExtra) : this(
        serializableExtra.isFillEnabled,
        ShapeExtraManager.getRectangleFillStyle(serializableExtra.userSelectedFillStyleId),
        serializableExtra.isBorderEnabled,
        ShapeExtraManager.getRectangleBorderStyle(serializableExtra.userSelectedBorderStyleId),
        serializableExtra.isDashPatternEnabled,
        createDashPatternFromCombinedNumber(serializableExtra.dashPattern)
    )

    fun toSerializableExtra(): SerializableRectangle.SerializableExtra =
        SerializableRectangle.SerializableExtra(
            isFillEnabled = isFillEnabled,
            userSelectedFillStyleId = userSelectedFillStyle.id,
            isBorderEnabled = isBorderEnabled,
            userSelectedBorderStyleId = userSelectedBorderStyle.id,
            isDashEnabled,
            userDefinedDashPattern.toCombinedNumber()
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

        private fun StraightStrokeDashPattern.toCombinedNumber(): Int =
            (segment.toInt() shl 16) or (gap.toInt() shl 8) or offset.toInt()

        private fun createDashPatternFromCombinedNumber(
            combinedNumber: Int
        ): StraightStrokeDashPattern = StraightStrokeDashPattern(
            ((combinedNumber shr 16) and 0x00FF).toByte(),
            ((combinedNumber shr 8) and 0x00FF).toByte(),
            (combinedNumber and 0x00FF).toByte()
        )
    }
}
