package mono.shape.shape.extra

import mono.shape.serialization.SerializableText

/**
 * A [ShapeExtra] for [mono.shape.shape.Text].
 */
data class TextExtra(
    val boundExtra: RectangleExtra,
    val textAlign: TextAlign
) : ShapeExtra() {

    constructor(serializableExtra: SerializableText.SerializableExtra) : this(
        RectangleExtra(serializableExtra.boundExtra),
        TextAlign(serializableExtra.textHorizontalAlign, serializableExtra.textVerticalAlign)
    )

    fun toSerializableExtra(): SerializableText.SerializableExtra =
        SerializableText.SerializableExtra(
            boundExtra.toSerializableExtra(),
            textAlign.horizontalAlign.ordinal,
            textAlign.verticalAlign.ordinal
        )

    fun hasBorder(): Boolean = boundExtra.isBorderEnabled

    data class TextAlign(val horizontalAlign: HorizontalAlign, val verticalAlign: VerticalAlign) {
        constructor(
            textHorizontalAlign: Int,
            textVerticalAlign: Int
        ) : this(HorizontalAlign.ALL[textHorizontalAlign], VerticalAlign.ALL[textVerticalAlign])
    }

    enum class HorizontalAlign {
        LEFT, MIDDLE, RIGHT;

        companion object {
            val ALL = values()
        }
    }

    enum class VerticalAlign {
        TOP, MIDDLE, BOTTOM;

        companion object {
            val ALL = values()
        }
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
