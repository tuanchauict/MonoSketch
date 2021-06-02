package mono.graphics.geo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Serializable
data class Rect(
    @SerialName("p")
    val position: Point,
    @SerialName("s")
    val size: Size
) {
    @Transient
    val width: Int = size.width

    @Transient
    val height: Int = size.height

    @Transient
    val left: Int = position.left

    @Transient
    val right: Int = position.left + width - 1

    @Transient
    val top: Int = position.top

    @Transient
    val bottom: Int = position.top + height - 1

    @Transient
    private val validHorizontalRange = left..right

    @Transient
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
