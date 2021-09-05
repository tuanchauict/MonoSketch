package mono.shape.extra

import mono.shape.ShapeExtraManager
import mono.shape.extra.style.AnchorChar
import mono.shape.extra.style.StraightStrokeStyle
import mono.shape.serialization.SerializableLine
import mono.shape.extra.style.StraightStrokeDashPattern

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
    val isDashEnabled: Boolean,
    val userDefinedDashPattern: StraightStrokeDashPattern = StraightStrokeDashPattern.SOLID
) : ShapeExtra() {

    val startAnchor: AnchorChar?
        get() = userSelectedStartAnchor.takeIf { isStartAnchorEnabled }
    val endAnchor: AnchorChar?
        get() = userSelectedEndAnchor.takeIf { isEndAnchorEnabled }

    val strokeStyle: StraightStrokeStyle?
        get() = userSelectedStrokeStyle.takeIf { isStrokeEnabled }

    val dashPattern: StraightStrokeDashPattern
        get() = if (isDashEnabled) userDefinedDashPattern else StraightStrokeDashPattern.SOLID

    constructor(serializableExtra: SerializableLine.SerializableExtra) : this(
        serializableExtra.isStrokeEnabled,
        ShapeExtraManager.getLineStrokeStyle(serializableExtra.userSelectedStrokeStyleId),

        serializableExtra.isStartAnchorEnabled,
        ShapeExtraManager.getStartHeadAnchorChar(serializableExtra.userSelectedStartAnchorId),

        serializableExtra.isEndAnchorEnabled,
        ShapeExtraManager.getEndHeadAnchorChar(serializableExtra.userSelectedEndAnchorId),

        serializableExtra.isDashEnabled,
        StraightStrokeDashPattern.fromSerializableValue(serializableExtra.dashPattern)
    )

    fun toSerializableExtra(): SerializableLine.SerializableExtra =
        SerializableLine.SerializableExtra(
            isStrokeEnabled = isStrokeEnabled,
            userSelectedStrokeStyleId = userSelectedStrokeStyle.id,
            isStartAnchorEnabled = isStartAnchorEnabled,
            userSelectedStartAnchorId = userSelectedStartAnchor.id,
            isEndAnchorEnabled = isEndAnchorEnabled,
            userSelectedEndAnchorId = userSelectedEndAnchor.id,
            isDashEnabled = isDashEnabled,
            dashPattern = dashPattern.toSerializableValue()
        )
}
