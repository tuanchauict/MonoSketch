package mono.shape.shape.extra

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [ShapeExtra] for [mono.shape.shape.Text].
 */
@Serializable
data class TextExtra(
    @SerialName("re")
    val boundExtra: RectangleExtra,
    @SerialName("ta")
    val textAlign: TextAlign
) : ShapeExtra() {

    fun hasBorder(): Boolean = boundExtra.isBorderEnabled

    @Serializable(with = TextAlign.TextAlignSerializer::class)
    data class TextAlign(val horizontalAlign: HorizontalAlign, val verticalAlign: VerticalAlign) {

        object TextAlignSerializer : KSerializer<TextAlign> {
            private val ORDINAL_TO_HORIZONTAL_VALUE_MAP =
                HorizontalAlign.values().associateBy { it.ordinal }
            private val ORDINAL_TO_VERTICAL_VALUE_MAP =
                VerticalAlign.values().associateBy { it.ordinal }

            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("TextAlign", PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: TextAlign) {
                val marshaledValue =
                    value.horizontalAlign.ordinal * 10 + value.verticalAlign.ordinal
                encoder.encodeInt(marshaledValue)
            }

            override fun deserialize(decoder: Decoder): TextAlign {
                val marshaledValue = decoder.decodeInt()
                val horizontalAlign = marshaledValue / 10
                val verticalAlign = marshaledValue % 10

                return TextAlign(
                    ORDINAL_TO_HORIZONTAL_VALUE_MAP[horizontalAlign]!!,
                    ORDINAL_TO_VERTICAL_VALUE_MAP[verticalAlign]!!
                )
            }
        }
    }

    enum class HorizontalAlign {
        LEFT, MIDDLE, RIGHT;
    }

    enum class VerticalAlign {
        TOP, MIDDLE, BOTTOM
    }

    companion object {
        val DEFAULT = TextExtra(
            boundExtra = RectangleExtra.DEFAULT,
            textAlign = TextAlign(HorizontalAlign.LEFT, VerticalAlign.TOP)
        )

        val NO_BOUND = TextExtra(
            boundExtra = RectangleExtra.DEFAULT.copy(isBorderEnabled = false),
            textAlign = TextAlign(HorizontalAlign.LEFT, VerticalAlign.TOP)
        )
    }
}
