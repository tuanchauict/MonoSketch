package mono.shape.shape

import mono.graphics.geo.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [Text]
 */
class TextTest {
    private val target = Text(Rect.byLTWH(0, 0, 5, 5))

    @Test
    fun testConvertRenderableText() {
        target.setText("0 1234 12345\n1   2 3 4 5678901 23")
        target.setExtra(Text.Extra.Updater.Bound(null))
        assertEquals(
            listOf("0", "1234", "12345", "1   2", "3 4", "56789", "01 23"),
            target.renderableText.getRenderableText()
        )
    }
}
