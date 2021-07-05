package mono.shape.shape.extra

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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

