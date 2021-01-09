package mono.graphics.geo

data class Point(val left: Int, val top: Int) {
    operator fun minus(base: Point): Point = Point(left - base.left, top - base.top)

    operator fun plus(base: Point): Point = Point(left + base.left, top + base.top)

    companion object {
        val INVALID = Point(Int.MIN_VALUE, Int.MIN_VALUE)
    }
}
