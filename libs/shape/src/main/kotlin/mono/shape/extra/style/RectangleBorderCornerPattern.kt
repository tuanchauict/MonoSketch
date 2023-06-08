/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.extra.style

/**
 * A data class for rectangle border corner pattern.
 */
data class RectangleBorderCornerPattern(
    val leftTopRounded: Boolean,
    val leftBottomRounded: Boolean,
    val rightTopRounded: Boolean,
    val rightBottomRounded: Boolean
) {

    fun toSerializableValue(): String =
        listOf(
            leftTopRounded,
            leftBottomRounded,
            rightTopRounded,
            rightBottomRounded
        ).joinToString(separator = "") { if (it) "Y" else "N" }

    companion object {
        val DISABLED = RectangleBorderCornerPattern(
            leftTopRounded = false,
            leftBottomRounded = false,
            rightTopRounded = false,
            rightBottomRounded = false
        )

        val ENABLED = RectangleBorderCornerPattern(
            leftTopRounded = true,
            leftBottomRounded = true,
            rightTopRounded = true,
            rightBottomRounded = true
        )

        fun fromSerializableValue(value: String): RectangleBorderCornerPattern {
            return RectangleBorderCornerPattern(
                leftTopRounded = value.getOrNull(0) == 'Y',
                leftBottomRounded = value.getOrNull(1) == 'Y',
                rightTopRounded = value.getOrNull(2) == 'Y',
                rightBottomRounded = value.getOrNull(3) == 'Y'

            )
        }
    }
}
