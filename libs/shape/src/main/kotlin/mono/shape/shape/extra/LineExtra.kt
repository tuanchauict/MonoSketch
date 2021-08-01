package mono.shape.shape.extra

import mono.shape.extra.manager.ShapeExtraManager
import mono.shape.extra.manager.model.AnchorChar
import mono.shape.serialization.SerializableLine

/**
 * A [ShapeExtra] for [mono.shape.shape.Line].
 */
data class LineExtra(
    val isStartAnchorEnabled: Boolean = false,
    val userSelectedStartAnchor: AnchorChar,
    val isEndAnchorEnabled: Boolean = false,
    val userSelectedEndAnchor: AnchorChar
) : ShapeExtra() {

    val startAnchor: AnchorChar?
        get() = userSelectedStartAnchor.takeIf { isStartAnchorEnabled }
    val endAnchor: AnchorChar?
        get() = userSelectedEndAnchor.takeIf { isEndAnchorEnabled }

    constructor(serializableExtra: SerializableLine.SerializableExtra) : this(
        serializableExtra.isStartAnchorEnabled,
        ShapeExtraManager.getStartHeadAnchorChar(serializableExtra.userSelectedStartAnchorId),
        serializableExtra.isEndAnchorEnabled,
        ShapeExtraManager.getEndHeadAnchorChar(serializableExtra.userSelectedEndAnchorId)
    )

    fun toSerializableExtra(): SerializableLine.SerializableExtra =
        SerializableLine.SerializableExtra(
            isStartAnchorEnabled,
            userSelectedStartAnchor.id,
            isEndAnchorEnabled,
            userSelectedEndAnchor.id
        )

    companion object {
        val DEFAULT = LineExtra(
            isStartAnchorEnabled = false,
            userSelectedStartAnchor = ShapeExtraManager.getStartHeadAnchorChar(null),
            isEndAnchorEnabled = false,
            userSelectedEndAnchor = ShapeExtraManager.getEndHeadAnchorChar(null)
        )
    }
}
