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
internal sealed class AbstractSerializableShape

@Serializable
@SerialName("R")
internal data class SerializableRectangle(
    @SerialName("b")
    val bound: Rect,
    @SerialName("e")
    val extra: Rectangle.Extra
) : AbstractSerializableShape()

@Serializable
@SerialName("T")
internal data class SerializableText(
    @SerialName("b")
    val bound: Rect,
    @SerialName("t")
    val text: String,
    @SerialName("e")
    val extra: Text.Extra
) : AbstractSerializableShape()

@Serializable
@SerialName("L")
internal data class SerializableLine(
    @SerialName("sp")
    val startPoint: DirectedPoint,
    @SerialName("ep")
    val endPoint: DirectedPoint,
    @SerialName("ps")
    val jointPoints: List<Point>,
    @SerialName("sc")
    val anchorCharStart: Line.AnchorChar,
    @SerialName("ec")
    val anchorCharEnd: Line.AnchorChar
) : AbstractSerializableShape()

@Serializable
@SerialName("G")
internal data class SerializableGroup(
    @SerialName("ss")
    val shapes: List<AbstractSerializableShape>
) : AbstractSerializableShape()
