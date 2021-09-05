package mono.shape.extra.style

/**
 * A class to define dash pattern of straight stroke.
 * @param dash is the solid of dash, min value is 1
 */
data class StraightStrokeDashPattern(val dash: Int, val gap: Int, val offset: Int) {

    private val adjustedSegment: Int = dash.coerceAtLeast(1)
    private val adjustedGap: Int = gap.coerceAtLeast(0)
    private val totalLength: Int = adjustedSegment + adjustedGap

    // Adjust offset to be in [0, length). Calculation for `isGap` does not work well with 
    // negative number
    private val adjustedOffset: Int = ((offset % totalLength) + totalLength) % totalLength

    fun isGap(index: Int): Boolean =
        if (adjustedGap != 0) (index + adjustedOffset) % totalLength >= adjustedSegment else false

    fun toSerializableValue(): String = "$dash|$gap|$offset"

    companion object {
        val SOLID = StraightStrokeDashPattern(dash = 1, gap = 0, offset = 0)

        fun fromSerializableValue(value: String): StraightStrokeDashPattern {
            val (dash, gap, offset) = value.split('|')
            return StraightStrokeDashPattern(
                dash.toIntOrNull() ?: 1,
                gap.toIntOrNull() ?: 0,
                offset.toIntOrNull() ?: 0
            )
        }
    }
}
