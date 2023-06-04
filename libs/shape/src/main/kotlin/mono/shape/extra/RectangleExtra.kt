/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.extra

import mono.shape.ShapeExtraManager
import mono.shape.extra.style.RectangleFillStyle
import mono.shape.extra.style.StraightStrokeDashPattern
import mono.shape.extra.style.StraightStrokeStyle
import mono.shape.serialization.SerializableRectangle

/**
 * A [ShapeExtra] for [mono.shape.shape.Rectangle]
 */
data class RectangleExtra(
    val isFillEnabled: Boolean,
    val userSelectedFillStyle: RectangleFillStyle,
    val isBorderEnabled: Boolean,
    val userSelectedBorderStyle: StraightStrokeStyle,
    val dashPattern: StraightStrokeDashPattern,
    val isRoundedCorner: Boolean
) : ShapeExtra() {
    val fillStyle: RectangleFillStyle?
        get() = if (isFillEnabled) userSelectedFillStyle else null

    val strokeStyle: StraightStrokeStyle?
        get() = if (isBorderEnabled) userSelectedBorderStyle else null

    constructor(serializableExtra: SerializableRectangle.SerializableExtra) : this(
        isFillEnabled = serializableExtra.isFillEnabled,
        ShapeExtraManager.getRectangleFillStyle(serializableExtra.userSelectedFillStyleId),
        isBorderEnabled = serializableExtra.isBorderEnabled,
        ShapeExtraManager.getRectangleBorderStyle(serializableExtra.userSelectedBorderStyleId),
        StraightStrokeDashPattern.fromSerializableValue(serializableExtra.dashPattern),
        isRoundedCorner = serializableExtra.isRoundedCorner
    )

    fun toSerializableExtra(): SerializableRectangle.SerializableExtra =
        SerializableRectangle.SerializableExtra(
            isFillEnabled = isFillEnabled,
            userSelectedFillStyleId = userSelectedFillStyle.id,
            isBorderEnabled = isBorderEnabled,
            userSelectedBorderStyleId = userSelectedBorderStyle.id,
            dashPattern = dashPattern.toSerializableValue(),
            isRoundedCorner = isRoundedCorner
        )
}
