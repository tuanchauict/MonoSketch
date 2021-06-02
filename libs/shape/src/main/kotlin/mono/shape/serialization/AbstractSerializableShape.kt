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
sealed class AbstractSerializableShape

@Serializable
@SerialName("R")
data class SerializableRectangle(
    @SerialName("b")
    val bound: Rect,
    @SerialName("e")
    val extra: Rectangle.Extra
) : AbstractSerializableShape()

@Serializable
@SerialName("T")
data class SerializableText(
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
    @SerialName("ss")
    val shapes: List<AbstractSerializableShape>
) : AbstractSerializableShape()
