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
 * A [ShapeExtra] for [mono.shape.shape.Line].
 */
@Serializable
data class LineExtra(
    @SerialName("as")
    val startAnchor: AnchorChar,
    @SerialName("ae")
    val endAnchor: AnchorChar
) : ShapeExtra() {

    /**
     * A data class for defining a anchor end char.
     *
     * @param id is the key for retrieving predefined [AnchorChar] when serialization. If id is not
     * defined (empty), serializer will use the other information for marshal and unmarshal. [id]
     * cannot be set from outside.
     *
     * @param displayName is the text visible on the UI tool for selection.
     */
    @Serializable(with = AnchorChar.AnchorCharSerializer::class)
    class AnchorChar private constructor(
        private val id: String,
        val displayName: String,
        val left: Char,
        val right: Char,
        val top: Char,
        val bottom: Char
    ) {

        constructor(
            left: Char,
            right: Char,
            top: Char,
            bottom: Char
        ) : this(NO_ID, "", left, right, top, bottom)

        constructor(all: Char) : this(all, all, all, all)

        private constructor(id: String, displayName: String, all: Char) :
            this(id, displayName, all, all, all, all)

        private constructor(id: String, displayName: String, horizontal: Char, vertical: Char) :
            this(id, displayName, horizontal, horizontal, vertical, vertical)

        internal object AnchorCharSerializer : KSerializer<AnchorChar> {
            private val predefinedMap: Map<String, AnchorChar> =
                PREDEFINED_ANCHOR_CHARS.associateBy { it.id }

            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("AnchorChar", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: AnchorChar) {
                val marshaledValue = value.id.ifEmpty {
                    listOf(value.displayName, value.left, value.right, value.top, value.bottom)
                        .joinToString("|")
                }

                encoder.encodeString(marshaledValue)
            }

            override fun deserialize(decoder: Decoder): AnchorChar {
                val marshaledValue = decoder.decodeString()
                return predefinedMap[marshaledValue] ?: decode(marshaledValue)
            }

            private fun decode(marshaledString: String): AnchorChar {
                val (displayName, left, right, top, bottom) = marshaledString.split("|")
                return AnchorChar(
                    id = NO_ID,
                    displayName,
                    left.first(),
                    right.first(),
                    top.first(),
                    bottom.first()
                )
            }
        }

        companion object {
            private const val NO_ID = ""

            val PREDEFINED_ANCHOR_CHARS = listOf(
                AnchorChar(id = "A0", displayName = "─", '─', '│'),
                AnchorChar(id = "A1", displayName = "▶", '◀', '▶', '▲', '▼'),
                AnchorChar(id = "A2", displayName = "■", '■')
            )
        }
    }

    companion object {
        val DEFAULT = LineExtra(
            startAnchor = AnchorChar.PREDEFINED_ANCHOR_CHARS[0],
            endAnchor = AnchorChar.PREDEFINED_ANCHOR_CHARS[1]
        )
    }
}
