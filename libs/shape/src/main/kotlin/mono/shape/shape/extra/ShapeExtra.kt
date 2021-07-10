package mono.shape.shape.extra

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mono.shape.shape.extra.RectangleExtra.BorderStyle.Companion.NO_BORDER
import mono.shape.shape.extra.RectangleExtra.BorderStyle.Companion.hasBorder

/**
 * An sealed interface for extra attributes for shapes
 */
sealed class ShapeExtra

object NoExtra : ShapeExtra()

/**
 * A [ShapeExtra] for [mono.shape.shape.Text].
 */
@Serializable
data class TextExtra(
    @SerialName("re")
    val boundExtra: RectangleExtra
) : ShapeExtra() {

    fun hasBorder(): Boolean = boundExtra.borderStyle.hasBorder()

    companion object {
        val DEFAULT = TextExtra(
            boundExtra = RectangleExtra.DEFAULT
        )

        val NO_BOUND = TextExtra(
            boundExtra = RectangleExtra.DEFAULT.copy(borderStyle = NO_BORDER)
        )
    }
}
