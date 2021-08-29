package mono.bitmap.manager.factory

/**
 * A class representing a char at a point for generating bitmap.
 */
class PointChar private constructor(val left: Int, val top: Int, val char: Char) {

    companion object {
        fun point(left: Int, top: Int, char: Char): Sequence<PointChar> =
            sequenceOf(PointChar(left, top, char))

        fun horizontalLine(begin: Int, end: Int, top: Int, char: Char): Sequence<PointChar> {
            val range = if (begin > end) begin downTo end else begin..end
            return range.asSequence().map { PointChar(it, top, char) }
        }

        fun verticalLine(left: Int, begin: Int, end: Int, char: Char): Sequence<PointChar> {
            val range = if (begin > end) begin downTo end else begin..end
            return range.asSequence().map { PointChar(left, it, char) }
        }
    }
}
