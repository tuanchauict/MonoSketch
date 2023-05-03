/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.graphics.bitmap.drawable

import kotlin.test.Test
import kotlin.test.assertEquals
import mono.graphics.bitmap.drawable.NinePatchDrawable.RepeatableRange

/**
 * A test for [NinePatchDrawable]
 */
class NinePatchDrawableTest {
    private val pattern = NinePatchDrawable.Pattern.fromText(
        """
        +-~+
        | •|
        |• |
        +~-+
        """.trimIndent()
    )

    @Test
    fun testToBitmap_NoRanges() {
        val target = NinePatchDrawable(pattern)
        assertEquals(
            """
            +++--~~~++
            +++--~~~++
            +++--~~~++
            |||  •••||
            |||  •••||
            |||••   ||
            |||••   ||
            |||••   ||
            +++~~---++
            +++~~---++
            """.trimIndent(),
            target.toBitmap(10, 10).toString()
        )
    }

    @Test
    fun testToBitmap_Range1() {
        val pattern = NinePatchDrawable.Pattern.fromText(
            """
            +-+
            | |
            +-+
            """.trimIndent()
        )
        val target = NinePatchDrawable(
            pattern,
            RepeatableRange.Repeat(1, 1),
            RepeatableRange.Repeat(1, 1)
        )
        assertEquals(
            """
            +---+
            |   |
            |   |
            |   |
            +---+
            """.trimIndent(),
            target.toBitmap(5, 5).toString()
        )
    }

    @Test
    fun testToBitmap_ScaleRepeat() {
        val target = NinePatchDrawable(
            pattern,
            RepeatableRange.Scale(1, 2),
            RepeatableRange.Repeat(1, 2)
        )

        assertEquals(
            """
            +----~~~~+
            |    ••••|
            |••••    |
            |    ••••|
            |••••    |
            |    ••••|
            |••••    |
            |    ••••|
            |••••    |
            +~~~~----+
            """.trimIndent(),
            target.toBitmap(10, 10).toString()
        )
    }

    @Test
    fun testToBitmap_RepeatScale() {
        val target = NinePatchDrawable(
            pattern,
            RepeatableRange.Repeat(1, 2),
            RepeatableRange.Scale(1, 2)
        )

        assertEquals(
            """
            +-~-~-~-~+
            | • • • •|
            | • • • •|
            | • • • •|
            | • • • •|
            |• • • • |
            |• • • • |
            |• • • • |
            |• • • • |
            +~-~-~-~-+
            """.trimIndent(),
            target.toBitmap(10, 10).toString()
        )
    }
}
