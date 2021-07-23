package mono.shape.shape.extra

import mono.shape.serialization.SerializableLine
import mono.shape.shape.extra.LineExtra.AnchorChar.Companion.NO_ID
import mono.shape.shape.extra.LineExtra.AnchorChar.Companion.PREDEFINED_ANCHOR_CHARS

/**
 * A [ShapeExtra] for [mono.shape.shape.Line].
 */
data class LineExtra(
    val isStartAnchorEnabled: Boolean = false,
    val userSelectedStartAnchor: AnchorChar,
    val isEndAnchorEnabled: Boolean = false,
    val userSelectedEndAnchor: AnchorChar
) : ShapeExtra() {

    val startAnchor: AnchorChar?
        get() = userSelectedStartAnchor.takeIf { isStartAnchorEnabled }
    val endAnchor: AnchorChar?
        get() = userSelectedEndAnchor.takeIf { isEndAnchorEnabled }

    constructor(serializableExtra: SerializableLine.SerializableExtra) : this(
        serializableExtra.isStartAnchorEnabled,
        PREDEFINED_ANCHOR_CHARS.first { it.id == serializableExtra.userSelectedStartAnchorId },
        serializableExtra.isEndAnchorEnabled,
        PREDEFINED_ANCHOR_CHARS.first { it.id == serializableExtra.userSelectedEndAnchorId }
    )

    fun toSerializableExtra(): SerializableLine.SerializableExtra =
        SerializableLine.SerializableExtra(
            isStartAnchorEnabled,
            userSelectedStartAnchor.id,
            isEndAnchorEnabled,
            userSelectedEndAnchor.id
        )

    /**
     * A class for defining an anchor end-char.
     *
     * @param id is the key for retrieving predefined [AnchorChar] when serialization. If id is not
     * defined ([NO_ID]), serializer will use the other information for marshal and unmarshal. [id]
     * cannot be set from outside.
     *
     * @param displayName is the text visible on the UI tool for selection.
     */
    class AnchorChar private constructor(
        val id: String,
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

        companion object {
            private const val NO_ID = ""

            val PREDEFINED_ANCHOR_CHARS = listOf(
                AnchorChar(id = "A1", displayName = "▶", '◀', '▶', '▲', '▼'),
                AnchorChar(id = "A2", displayName = "■", '■'),
                AnchorChar(id = "A3", displayName = "○", '○'),
                AnchorChar(id = "A4", displayName = "◎", '◎'),
                AnchorChar(id = "A5", displayName = "●", '●'),
            )
        }
    }

    companion object {
        val DEFAULT = LineExtra(
            isStartAnchorEnabled = false,
            userSelectedStartAnchor = AnchorChar.PREDEFINED_ANCHOR_CHARS[0],
            isEndAnchorEnabled = false,
            userSelectedEndAnchor = AnchorChar.PREDEFINED_ANCHOR_CHARS[0]
        )
    }
}
