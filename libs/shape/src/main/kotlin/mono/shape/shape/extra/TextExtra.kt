package mono.shape.shape.extra

import mono.shape.extra.manager.model.TextAlign
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

    companion object {
        val DEFAULT = TextExtra(
            boundExtra = RectangleExtra.DEFAULT,
            textAlign = TextAlign(TextAlign.HorizontalAlign.LEFT, TextAlign.VerticalAlign.TOP)
        )

        val NO_BOUND = TextExtra(
            boundExtra = RectangleExtra.DEFAULT.copy(isBorderEnabled = false),
            textAlign = TextAlign(TextAlign.HorizontalAlign.LEFT, TextAlign.VerticalAlign.TOP)
        )
    }
}
