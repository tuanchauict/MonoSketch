package mono.shape.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.shape.Line
import mono.shape.shape.Rectangle
import mono.shape.shape.Text

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
    val extra: Rectangle.Extra
) : AbstractSerializableShape()

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
    val extra: Text.Extra
) : AbstractSerializableShape()

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
    @SerialName("cs")
    val anchorCharStart: Line.AnchorChar,
    @SerialName("ce")
    val anchorCharEnd: Line.AnchorChar,
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
