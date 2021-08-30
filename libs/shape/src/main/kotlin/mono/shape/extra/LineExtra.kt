package mono.shape.extra

import mono.shape.ShapeExtraManager
import mono.shape.extra.style.AnchorChar
import mono.shape.extra.style.StraightStrokeStyle
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle
import mono.shape.serialization.SerializableLine
import mono.shape.extra.style.StraightStrokeDashPattern

/**
 * A [ShapeExtra] for [mono.shape.shape.Line].
 */
data class LineExtra(
    val isStartAnchorEnabled: Boolean = false,
    val userSelectedStartAnchor: AnchorChar,
    val isEndAnchorEnabled: Boolean = false,
    val userSelectedEndAnchor: AnchorChar,
    val isDashEnabled: Boolean = false,
    val userDefinedDashPattern: StraightStrokeDashPattern = StraightStrokeDashPattern.SOLID
) : ShapeExtra() {

    val startAnchor: AnchorChar?
        get() = userSelectedStartAnchor.takeIf { isStartAnchorEnabled }
    val endAnchor: AnchorChar?
        get() = userSelectedEndAnchor.takeIf { isEndAnchorEnabled }

    val strokeStyle: StraightStrokeStyle = PredefinedStraightStrokeStyle.PREDEFINED_STYLES[0]

    val dashPattern: StraightStrokeDashPattern
        get() = if (isDashEnabled) userDefinedDashPattern else StraightStrokeDashPattern.SOLID

    constructor(serializableExtra: SerializableLine.SerializableExtra) : this(
        serializableExtra.isStartAnchorEnabled,
        ShapeExtraManager.getStartHeadAnchorChar(serializableExtra.userSelectedStartAnchorId),
        serializableExtra.isEndAnchorEnabled,
        ShapeExtraManager.getEndHeadAnchorChar(serializableExtra.userSelectedEndAnchorId)
    )

    fun toSerializableExtra(): SerializableLine.SerializableExtra =
        SerializableLine.SerializableExtra(
            isStartAnchorEnabled = isStartAnchorEnabled,
            userSelectedStartAnchorId = userSelectedStartAnchor.id,
            isEndAnchorEnabled = isEndAnchorEnabled,
            userSelectedEndAnchorId = userSelectedEndAnchor.id,
            strokeStyleId = strokeStyle.id,
            isDashEnabled = isDashEnabled,
            dashPattern = dashPattern.toSerializableValue()
        )

    companion object {
        fun createDefault(): LineExtra {
            val defaultExtraState = ShapeExtraManager.defaultExtraState
            return LineExtra(
                defaultExtraState.isStartHeadAnchorCharEnabled,
                defaultExtraState.startHeadAnchorChar,
                defaultExtraState.isEndHeadAnchorCharEnabled,
                defaultExtraState.endHeadAnchorChar
            )
        }
    }
}
