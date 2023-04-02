/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.bitmap.manager.factory

import kotlin.math.abs

/**
 * A class representing a char at a point for generating bitmap.
 */
class PointChar private constructor(val left: Int, val top: Int, val char: Char) {

    companion object {
        fun point(left: Int, top: Int, char: Char): Sequence<PointChar> =
            sequenceOf(PointChar(left, top, char))

        fun horizontalLine(
            beginExclusive: Int,
            endExclusive: Int,
            top: Int,
            char: Char
        ): Sequence<PointChar> {
            if (abs(beginExclusive - endExclusive) <= 1) {
                return emptySequence()
            }
            val delta = if (beginExclusive < endExclusive) 1 else -1
            val begin = beginExclusive + delta
            val end = endExclusive - delta
            val range = if (begin > end) begin downTo end else begin..end
            return range.asSequence().map { PointChar(it, top, char) }
        }

        fun verticalLine(
            left: Int,
            beginExclusive: Int,
            endExclusive: Int,
            char: Char
        ): Sequence<PointChar> {
            if (abs(beginExclusive - endExclusive) <= 1) {
                return emptySequence()
            }
            val delta = if (beginExclusive < endExclusive) 1 else -1
            val begin = beginExclusive + delta
            val end = endExclusive - delta
            val range = if (begin > end) begin downTo end else begin..end
            return range.asSequence().map { PointChar(left, it, char) }
        }
    }
}
