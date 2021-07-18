package mono.shape.shape.extra

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mono.common.Characters.TRANSPARENT_CHAR
import mono.graphics.bitmap.drawable.CharDrawable
import mono.graphics.bitmap.drawable.Drawable
import mono.graphics.bitmap.drawable.NinePatchDrawable
import mono.graphics.bitmap.drawable.NinePatchDrawable.Pattern

/**
 * A [ShapeExtra] for [mono.shape.shape.Rectangle]
 */
@Serializable
data class RectangleExtra(
    @SerialName("fe")
    val isFillEnabled: Boolean = false,
    @SerialName("fu")
    val userSelectedFillStyle: FillStyle,
    @SerialName("be")
    val isBorderEnabled: Boolean,
    @SerialName("bu")
    val userSelectedBorderStyle: BorderStyle
) : ShapeExtra() {
    val fillStyle: FillStyle
        get() = if (isFillEnabled) userSelectedFillStyle else FillStyle.NOFILLED_STYLE

    val borderStyle: BorderStyle
        get() = if (isBorderEnabled) userSelectedBorderStyle else BorderStyle.NO_BORDER

    /**
     * A class for defining a fill style for rectangle.
     *
     * @param id is the key for retrieving predefined [FillStyle] when serialization. If id is not
     * defined ([NO_ID]), serializer will use the other information for marshal and unmarshal. [id]
     * cannot be set from outside.
     *
     * @param displayName is the text visible on the UI tool for selection.
     */
    @Serializable(with = FillStyle.FillStyleSerializer::class)
    class FillStyle private constructor(
        private val id: String,
        val displayName: String,
        val drawable: Drawable
    ) {

        internal object FillStyleSerializer : KSerializer<FillStyle> {
            private val predefinedMap: Map<String, FillStyle> =
                PREDEFINED_FILL_STYLE.associateBy { it.id }

            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("FillStyle", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: FillStyle) {
                val marshaledValue = value.id.ifEmpty {
                    TODO("Implement marshal-algorithm for general fill style")
                }
                encoder.encodeString(marshaledValue)
            }

            override fun deserialize(decoder: Decoder): FillStyle {
                val marshaledValue = decoder.decodeString()
                return predefinedMap[marshaledValue]
                    ?: TODO("Implement unmarshal-algorithm for general fill style")
            }
        }

        companion object {
            private const val NO_ID = ""

            internal val NOFILLED_STYLE = FillStyle(
                id = "F0",
                displayName = "No Fill",
                CharDrawable(TRANSPARENT_CHAR)
            )

            val PREDEFINED_FILL_STYLE = listOf(
                FillStyle(
                    id = "F1",
                    displayName = " ",
                    CharDrawable(' ')
                ),
                FillStyle(
                    id = "F2",
                    displayName = "█",
                    CharDrawable('█')
                ),
                FillStyle(
                    id = "F3",
                    displayName = "▒",
                    CharDrawable('▒')
                ),
                FillStyle(
                    id = "F4",
                    displayName = "░",
                    CharDrawable('░')
                ),
                FillStyle(
                    id = "F5",
                    displayName = "▚",
                    CharDrawable('▚')
                )
            )
        }
    }

    /**
     * A class for defining a fill style for rectangle.
     *
     * @param id is the key for retrieving predefined [FillStyle] when serialization. If id is not
     * defined ([NO_ID]), serializer will use the other information for marshal and unmarshal. [id]
     * cannot be set from outside.
     *
     * @param displayName is the text visible on the UI tool for selection.
     */
    @Serializable(with = BorderStyle.BorderStyleSerializer::class)
    class BorderStyle private constructor(
        private val id: String,
        val displayName: String,
        val drawable: Drawable
    ) {
        internal object BorderStyleSerializer : KSerializer<BorderStyle> {
            private val predefinedMap: Map<String, BorderStyle> =
                PREDEFINED_FILL_STYLE.associateBy { it.id }

            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("BorderStyle", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: BorderStyle) {
                val marshaledValue = value.id.ifEmpty {
                    TODO("Implement marshal-algorithm for general border style")
                }
                encoder.encodeString(marshaledValue)
            }

            override fun deserialize(decoder: Decoder): BorderStyle {
                val marshaledValue = decoder.decodeString()
                return predefinedMap[marshaledValue]
                    ?: TODO("Implement unmarshal-algorithm for general border style")
            }
        }

        companion object {
            private const val NO_ID = ""

            private val PATTERN_TEXT_0 = """
                ┌─┐
                │ │
                └─┘
            """.trimIndent()
            private val REPEATABLE_RANGE_0 = NinePatchDrawable.RepeatableRange.Repeat(1, 1)

            internal val NO_BORDER = BorderStyle(
                id = "B0",
                displayName = "No border",
                NinePatchDrawable(Pattern.fromText(" "))
            )

            val PREDEFINED_FILL_STYLE = listOf(
                BorderStyle(
                    id = "B1",
                    displayName = "─",
                    NinePatchDrawable(
                        Pattern.fromText(PATTERN_TEXT_0),
                        REPEATABLE_RANGE_0,
                        REPEATABLE_RANGE_0
                    )
                )
            )
        }
    }

    companion object {
        val DEFAULT = RectangleExtra(
            isFillEnabled = false,
            FillStyle.PREDEFINED_FILL_STYLE[0],
            isBorderEnabled = true,
            BorderStyle.PREDEFINED_FILL_STYLE[0]
        )
    }
}
