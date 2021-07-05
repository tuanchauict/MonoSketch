package mono.shape.shape.extra

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mono.shape.shape.Rectangle

/**
 * An sealed interface for extra attributes for shapes
 */
sealed class ShapeExtra

object NoExtra : ShapeExtra()

/**
 * A [ShapeExtra] for [Rectangle]
 */
@Serializable
data class RectangleExtra(
    @SerialName("sf")
    val fillStyle: FillStyle
) : ShapeExtra() {

    @Serializable
    enum class FillStyle {
        @SerialName("s0f")
        STYLE_0_FILL,

        @SerialName("s0b")
        STYLE_0_BORDER
    }

    companion object {
        val DEFAULT = RectangleExtra(FillStyle.STYLE_0_BORDER)
    }
}

/**
 * A [ShapeExtra] for [mono.shape.shape.Text].
 */
@Serializable
data class TextExtra(
    @SerialName("be")
    val boundExtra: RectangleExtra?
) : ShapeExtra() {

    companion object {
        val DEFAULT = TextExtra(boundExtra = RectangleExtra.DEFAULT)
    }
}

/**
 * A [ShapeExtra] for [mono.shape.shape.Line].
 */
@Serializable
data class LineExtra(
    val startAnchorChar: AnchorChar,
    val endAnchorChar: AnchorChar
) : ShapeExtra() {

    @Serializable
    data class AnchorChar(
        @SerialName("l")
        val left: Char,
        @SerialName("r")
        val right: Char,
        @SerialName("t")
        val top: Char,
        @SerialName("b")
        val bottom: Char
    ) {
        constructor(all: Char) : this(all, all, all, all)
    }

    companion object {
        val DEFAULT = LineExtra(
            startAnchorChar = AnchorChar(left = '─', right = '─', top = '│', bottom = '│'),
            endAnchorChar = AnchorChar(left = '─', right = '─', top = '│', bottom = '│')
        )
    }
}
