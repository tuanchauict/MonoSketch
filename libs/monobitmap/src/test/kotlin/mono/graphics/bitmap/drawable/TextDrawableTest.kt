package mono.graphics.bitmap.drawable

import mono.graphics.geo.Rect
import mono.shape.shape.Text
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [TextDrawableTest]
 */
class TextDrawableTest {
    @Test
    fun testToBitmap() {
        val text = Text(Rect.byLTWH(0, 0, 7, 5))
        text.setExtra(Text.Extra.Updater.Text(text = "012345678\nabc"))
        val bitmap = TextDrawable.toBitmap(
            text.bound.size,
            text.renderableText.getRenderableText(),
            text.extra
        )
        assertEquals(
            """
                |┌─────┐
                ||01234|
                ||5678 |
                ||abc  |
                |└─────┘
            """.trimMargin(),
            bitmap.toString()
        )
    }
}
