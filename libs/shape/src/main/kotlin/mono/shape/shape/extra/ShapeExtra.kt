package mono.shape.shape.extra

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("be")
    val boundExtra: RectangleExtra?
) : ShapeExtra() {

    companion object {
        val DEFAULT = TextExtra(boundExtra = RectangleExtra.DEFAULT)
    }
}
