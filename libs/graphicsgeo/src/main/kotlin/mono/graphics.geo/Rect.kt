package mono.graphics.geo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Serializable(with = Rect.RectSerializer::class)
data class Rect(
    val position: Point,
    val size: Size
) {
    val width: Int = size.width
    val height: Int = size.height

    val left: Int = position.left
    val right: Int = position.left + width - 1
    val top: Int = position.top
    val bottom: Int = position.top + height - 1

    private val validHorizontalRange = left..right
    private val validVerticalRange = top..bottom

    operator fun contains(point: Point): Boolean =
        point.left in validHorizontalRange && point.top in validVerticalRange

    fun getOverlappedRect(rect: Rect): Rect? {
        if (!isOverlapped(rect)) {
            return null
        }
        val offset = rect.position - position
        val top = max(offset.top, 0)
        val bottom = min(offset.top + rect.height, height) - 1
        val left = max(offset.left, 0)
        val right = min(offset.left + rect.width, width) - 1
        return byLTRB(
            left + position.left,
            top + position.top,
            right + position.left,
            bottom + position.top
        )
    }

    fun isOverlapped(rect: Rect): Boolean {
        val isHorizontalOverlap = left in rect.left..rect.right || rect.left in left..right
        val isVerticalOverlap = top in rect.top..rect.bottom || rect.top in top..bottom
        return isHorizontalOverlap && isVerticalOverlap
    }

    override fun toString(): String = "[$left, $top] - [$width x $height]"

    internal object RectSerializer : KSerializer<Rect> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Rect", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Rect) {
            encoder.encodeString("${value.left}|${value.top}|${value.width}|${value.height}")
        }

        override fun deserialize(decoder: Decoder): Rect {
            val marshaledValue = decoder.decodeString()
            val (left, top, width, height) = marshaledValue.split("|")
            return byLTWH(left.toInt(), top.toInt(), width.toInt(), height.toInt())
        }
    }

    companion object {
        val ZERO = byLTWH(0, 0, 0, 0)

        fun byLTRB(left: Int, top: Int, right: Int, bottom: Int): Rect = Rect(
            Point(min(left, right), min(top, bottom)),
            Size(abs(right - left) + 1, abs(bottom - top) + 1)
        )

        fun byLTWH(left: Int, top: Int, width: Int, height: Int): Rect = Rect(
            Point(left, top),
            Size(width, height)
        )

        fun boundOf(rectangles: Sequence<Rect>): Rect {
            val firstRect = rectangles.first()
            var left = firstRect.left
            var right = firstRect.right
            var top = firstRect.top
            var bottom = firstRect.bottom

            for (rect in rectangles) {
                left = min(rect.left, left)
                right = max(rect.right, right)
                top = min(rect.top, top)
                bottom = max(rect.bottom, bottom)
            }
            return byLTRB(left, top, right, bottom)
        }
    }
}
