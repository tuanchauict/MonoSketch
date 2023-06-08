/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.extra

import mono.shape.ShapeExtraManager
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle
import mono.shape.extra.style.RectangleBorderCornerPattern
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
    val corner: RectangleBorderCornerPattern
) : ShapeExtra() {
    constructor(
        isFillEnabled: Boolean,
        userSelectedFillStyle: RectangleFillStyle,
        isBorderEnabled: Boolean,
        userSelectedBorderStyle: StraightStrokeStyle,
        dashPattern: StraightStrokeDashPattern,
        isRoundedCorner: Boolean
    ) : this(
        isFillEnabled,
        userSelectedFillStyle,
        isBorderEnabled,
        userSelectedBorderStyle,
        dashPattern,
        if (isRoundedCorner) {
            RectangleBorderCornerPattern.ENABLED
        } else {
            RectangleBorderCornerPattern.DISABLED
        }
    )

    val isRoundedCorner: Boolean = corner == RectangleBorderCornerPattern.ENABLED

    val fillStyle: RectangleFillStyle?
        get() = if (isFillEnabled) userSelectedFillStyle else null

    val strokeStyle: StraightStrokeStyle?
        get() = if (isBorderEnabled) {
            PredefinedStraightStrokeStyle.getStyle(userSelectedBorderStyle.id, isRoundedCorner)
        } else {
            null
        }

    constructor(serializableExtra: SerializableRectangle.SerializableExtra) : this(
        isFillEnabled = serializableExtra.isFillEnabled,
        ShapeExtraManager.getRectangleFillStyle(serializableExtra.userSelectedFillStyleId),
        isBorderEnabled = serializableExtra.isBorderEnabled,
        ShapeExtraManager.getRectangleBorderStyle(serializableExtra.userSelectedBorderStyleId),
        StraightStrokeDashPattern.fromSerializableValue(serializableExtra.dashPattern),
        RectangleBorderCornerPattern.fromSerializableValue(serializableExtra.corner)
    )

    fun toSerializableExtra(): SerializableRectangle.SerializableExtra =
        SerializableRectangle.SerializableExtra(
            isFillEnabled = isFillEnabled,
            userSelectedFillStyleId = userSelectedFillStyle.id,
            isBorderEnabled = isBorderEnabled,
            userSelectedBorderStyleId = userSelectedBorderStyle.id,
            dashPattern = dashPattern.toSerializableValue(),
            corner = corner.toSerializableValue()
        )
}
