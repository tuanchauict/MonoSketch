/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.extra

import mono.shape.ShapeExtraManager
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle
import mono.shape.extra.style.AnchorChar
import mono.shape.extra.style.StraightStrokeDashPattern
import mono.shape.extra.style.StraightStrokeStyle
import mono.shape.serialization.SerializableLine

/**
 * A [ShapeExtra] for [mono.shape.shape.Line].
 */
data class LineExtra(
    val isStrokeEnabled: Boolean,
    val userSelectedStrokeStyle: StraightStrokeStyle,
    val isStartAnchorEnabled: Boolean,
    val userSelectedStartAnchor: AnchorChar,
    val isEndAnchorEnabled: Boolean,
    val userSelectedEndAnchor: AnchorChar,
    val dashPattern: StraightStrokeDashPattern,
    val isRoundedCorner: Boolean
) : ShapeExtra() {

    val startAnchor: AnchorChar?
        get() = userSelectedStartAnchor.takeIf { isStartAnchorEnabled }
    val endAnchor: AnchorChar?
        get() = userSelectedEndAnchor.takeIf { isEndAnchorEnabled }

    val strokeStyle: StraightStrokeStyle?
        get() = if (isStrokeEnabled) {
            PredefinedStraightStrokeStyle.getStyle(userSelectedStrokeStyle.id, isRoundedCorner)
        } else {
            null
        }

    constructor(serializableExtra: SerializableLine.SerializableExtra) : this(
        isStrokeEnabled = serializableExtra.isStrokeEnabled,
        ShapeExtraManager.getLineStrokeStyle(serializableExtra.userSelectedStrokeStyleId),

        isStartAnchorEnabled = serializableExtra.isStartAnchorEnabled,
        ShapeExtraManager.getStartHeadAnchorChar(serializableExtra.userSelectedStartAnchorId),

        isEndAnchorEnabled = serializableExtra.isEndAnchorEnabled,
        ShapeExtraManager.getEndHeadAnchorChar(serializableExtra.userSelectedEndAnchorId),

        StraightStrokeDashPattern.fromSerializableValue(serializableExtra.dashPattern),

        isRoundedCorner = serializableExtra.isRoundedCorner
    )

    fun toSerializableExtra(): SerializableLine.SerializableExtra =
        SerializableLine.SerializableExtra(
            isStrokeEnabled = isStrokeEnabled,
            userSelectedStrokeStyleId = userSelectedStrokeStyle.id,
            isStartAnchorEnabled = isStartAnchorEnabled,
            userSelectedStartAnchorId = userSelectedStartAnchor.id,
            isEndAnchorEnabled = isEndAnchorEnabled,
            userSelectedEndAnchorId = userSelectedEndAnchor.id,
            dashPattern = dashPattern.toSerializableValue(),
            isRoundedCorner = isRoundedCorner
        )
}
