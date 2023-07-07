/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mono.graphics.geo.Point
import mono.graphics.geo.PointF
import mono.shape.shape.Line

@Serializable
data class SerializableLineConnector(
    @SerialName("i")
    val lineId: String,
    @Serializable(AnchorSerializer::class)
    @SerialName("a")
    val anchor: Line.Anchor,
    @SerialName("t")
    val targetId: String,
    @SerialName("r")
    @Serializable(PointFSerializer::class)
    val ratio: PointF,
    @SerialName("o")
    val offset: Point
)

/**
 * A serializer for [Line.Anchor]
 */
private object AnchorSerializer : KSerializer<Line.Anchor> {
    private const val START_VALUE = 1
    private const val END_VALUE = 2

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Anchor", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Line.Anchor) {
        val marshaledValue = when (value) {
            Line.Anchor.START -> START_VALUE
            Line.Anchor.END -> END_VALUE
        }
        encoder.encodeInt(marshaledValue)
    }

    override fun deserialize(decoder: Decoder): Line.Anchor =
        when (val marshaledValue = decoder.decodeInt()) {
            START_VALUE -> Line.Anchor.START
            END_VALUE -> Line.Anchor.END
            else -> error("Unrecognizable value $marshaledValue")
        }
}

/**
 * A serializer for [PointF]
 */
private object PointFSerializer : KSerializer<PointF> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PointF", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PointF) {
        encoder.encodeString("${value.left}|${value.top}")
    }

    override fun deserialize(decoder: Decoder): PointF {
        val marshaledValue = decoder.decodeString()
        val (left, top) = marshaledValue.split("|")
        return PointF(left = left.toDouble(), top = top.toDouble())
    }
}
