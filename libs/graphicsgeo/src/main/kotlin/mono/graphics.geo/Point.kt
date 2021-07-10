package mono.graphics.geo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Point.PointSerializer::class)
data class Point(val left: Int, val top: Int) {
    val row: Int get() = top
    val column: Int get() = left

    operator fun minus(base: Point): Point = Point(left - base.left, top - base.top)

    operator fun plus(base: Point): Point = Point(left + base.left, top + base.top)

    internal object PointSerializer : KSerializer<Point> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Point", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Point) {
            encoder.encodeString("${value.left}|${value.top}")
        }

        override fun deserialize(decoder: Decoder): Point {
            val marshaledValue = decoder.decodeString()
            val (left, top) = marshaledValue.split("|")
            return Point(left = left.toInt(), top = top.toInt())
        }
    }

    companion object {
        val ZERO = Point(0, 0)
    }
}

@Serializable(with = DirectedPoint.DirectedPointSerializer::class)
data class DirectedPoint(val direction: Direction, val left: Int, val top: Int) {
    val point: Point
        get() = Point(left, top)

    constructor(direction: Direction, point: Point) : this(direction, point.left, point.top)

    operator fun plus(base: Point): DirectedPoint =
        copy(left = left + base.left, top = top + base.top)

    enum class Direction {
        HORIZONTAL,
        VERTICAL;

        val normalizedDirection: Direction
            get() = when (this) {
                VERTICAL -> HORIZONTAL
                HORIZONTAL -> VERTICAL
            }
    }

    internal object DirectedPointSerializer : KSerializer<DirectedPoint> {
        private const val MARSHAL_HORIZONTAL = "H"
        private const val MARSHAL_VERTICAL = "V"

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("DirectedPoint", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: DirectedPoint) {
            val direction = when (value.direction) {
                Direction.HORIZONTAL -> MARSHAL_HORIZONTAL
                Direction.VERTICAL -> MARSHAL_VERTICAL
            }
            encoder.encodeString("$direction|${value.left}|${value.top}")
        }

        override fun deserialize(decoder: Decoder): DirectedPoint {
            val marshaledValue = decoder.decodeString()
            val (marshaledDirection, left, top) = marshaledValue.split("|")
            val direction =
                if (marshaledDirection == MARSHAL_HORIZONTAL) {
                    Direction.HORIZONTAL
                } else {
                    Direction.VERTICAL
                }
            return DirectedPoint(
                direction,
                left = left.toInt(),
                top = top.toInt()
            )
        }
    }
}
