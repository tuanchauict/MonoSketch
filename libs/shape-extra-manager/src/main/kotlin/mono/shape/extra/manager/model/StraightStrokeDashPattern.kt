package mono.shape.extra.manager.model

/**
 * A class to define dash pattern of straight stroke.
 * @param segment is the solid of dash, min value is 1
 */
data class StraightStrokeDashPattern(val segment: Byte, val gap: Byte, val offset: Byte) {

    private val adjustedSegment: Int = segment.toInt().coerceAtLeast(1)
    private val adjustedGap: Int = gap.toInt().coerceAtLeast(0)
    private val totalLength: Int = adjustedSegment + adjustedGap

    // Adjust offset to be in [0, length). Calculation for `isGap` does not work well with 
    // negative number
    private val adjustedOffset: Int = ((offset % totalLength) + totalLength) % totalLength

    fun isGap(index: Int): Boolean =
        if (adjustedGap != 0) (index + adjustedOffset) % totalLength >= adjustedSegment else false

    fun toSerializableValue(): Int =
        (segment.toInt() shl 16) or (gap.toInt() shl 8) or offset.toInt()

    companion object {
        val SOLID = StraightStrokeDashPattern(segment = 1, gap = 0, offset = 0)

        fun fromSerializableValue(value: Int): StraightStrokeDashPattern =
            StraightStrokeDashPattern(
                ((value shr 16) and 0x00FF).toByte(),
                ((value shr 8) and 0x00FF).toByte(),
                (value and 0x00FF).toByte()
            )
    }
}
