package mono.graphics.geo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Size.SizeSerializer::class)
data class Size(val width: Int, val height: Int) {

    internal object SizeSerializer : KSerializer<Size> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Size", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Size) {
            encoder.encodeString("${value.width}|${value.height}")
        }

        override fun deserialize(decoder: Decoder): Size {
            val marshaledValue = decoder.decodeString()
            val (width, height) = marshaledValue.split("|")
            return Size(width.toInt(), height.toInt())
        }
    }

    companion object {
        val ZERO = Size(0, 0)
    }
}

data class SizeF(val width: Double, val height: Double)
