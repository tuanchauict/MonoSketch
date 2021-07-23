package mono.shape.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.shape.extra.LineExtra

@Serializable
sealed class AbstractSerializableShape {
    // null for not having id.
    abstract val id: String?
}

@Serializable
@SerialName("R")
data class SerializableRectangle(
    @SerialName("i")
    override val id: String? = null,
    @SerialName("b")
    val bound: Rect,
    @SerialName("e")
    val extra: SerializableExtra
) : AbstractSerializableShape() {

    @Serializable
    data class SerializableExtra(
        @SerialName("fe")
        val isFillEnabled: Boolean = false,
        @SerialName("fu")
        val userSelectedFillStyleId: String,
        @SerialName("be")
        val isBorderEnabled: Boolean,
        @SerialName("bu")
        val userSelectedBorderStyleId: String
    )
}

@Serializable
@SerialName("T")
data class SerializableText(
    @SerialName("i")
    override val id: String? = null,
    @SerialName("b")
    val bound: Rect,
    @SerialName("t")
    val text: String,
    @SerialName("e")
    val extra: SerializableExtra
) : AbstractSerializableShape() {

    @Serializable
    data class SerializableExtra(
        @SerialName("be")
        val boundExtra: SerializableRectangle.SerializableExtra,
        @SerialName("tha")
        val textHorizontalAlign: Int,
        @SerialName("tva")
        val textVerticalAlign: Int
    )
}

@Serializable
@SerialName("L")
data class SerializableLine(
    @SerialName("i")
    override val id: String? = null,
    @SerialName("ps")
    val startPoint: DirectedPoint,
    @SerialName("pe")
    val endPoint: DirectedPoint,
    @SerialName("jps")
    val jointPoints: List<Point>,
    @SerialName("e")
    val extra: LineExtra,
    @SerialName("em")
    val wasMovingEdge: Boolean
) : AbstractSerializableShape()

@Serializable
@SerialName("G")
data class SerializableGroup(
    @SerialName("i")
    override val id: String? = null,
    @SerialName("ss")
    val shapes: List<AbstractSerializableShape>
) : AbstractSerializableShape()
