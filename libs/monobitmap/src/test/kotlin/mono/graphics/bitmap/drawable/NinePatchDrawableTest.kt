package mono.graphics.bitmap.drawable

import mono.graphics.bitmap.drawable.NinePatchDrawable.RepeatableRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
    fun testToBitmap() {
        val target = NinePatchDrawable(
            pattern,
            RepeatableRange.scale(1, 2),
            RepeatableRange.repeat(1, 2)
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
    fun testToBitmap_Invalid() {
        assertFailsWith(IllegalArgumentException::class) {
            NinePatchDrawable(pattern, RepeatableRange.repeat(0, 10), null)
        }
        assertFailsWith(IllegalArgumentException::class) {
            NinePatchDrawable(pattern, null, RepeatableRange.repeat(0, 10))
        }

        val target = NinePatchDrawable(
            pattern,
            null,
            null
        )

        assertFailsWith(IllegalArgumentException::class) {
            target.toBitmap(5, 5)
        }
        assertFailsWith(IllegalArgumentException::class) {
            target.toBitmap(4, 5)
        }
    }
}
