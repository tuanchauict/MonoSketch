package mono.shape.shape

import mono.graphics.geo.Rect
import mono.shape.serialization.SerializableText
import mono.shape.extra.TextExtra
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [Text]
 */
class TextTest {
    @Test
    fun testSerialization_init() {
        val text = Text(Rect.byLTWH(1, 2, 3, 4), parentId = PARENT_ID)

        val serializableText = text.toSerializableShape(true) as SerializableText
        assertEquals(text.text, serializableText.text)
        assertEquals(text.bound, serializableText.bound)
        assertEquals(text.extra.toSerializableExtra(), serializableText.extra)
    }

    @Test
    fun testSerialization_updateBound() {
        val text = Text(Rect.byLTWH(1, 2, 3, 4), parentId = PARENT_ID)
        text.setBound(Rect.byLTWH(5, 6, 7, 8))

        val serializableText = text.toSerializableShape(true) as SerializableText
        assertEquals(text.text, serializableText.text)
        assertEquals(text.bound, serializableText.bound)
        assertEquals(text.extra.toSerializableExtra(), serializableText.extra)
    }

    @Test
    fun testSerialization_updateText() {
        val text = Text(Rect.byLTWH(1, 2, 3, 4), parentId = PARENT_ID)
        text.setText("Hello Hello!")

        val serializableText = text.toSerializableShape(true) as SerializableText
        assertEquals(text.text, serializableText.text)
        assertEquals(text.bound, serializableText.bound)
        assertEquals(text.extra.toSerializableExtra(), serializableText.extra)
    }

    @Test
    fun testSerialization_restore() {
        val text = Text(Rect.byLTWH(1, 2, 3, 4), parentId = PARENT_ID)
        text.setText("Hello Hello!")
        text.setBound(Rect.byLTWH(5, 5, 2, 2))

        val serializableText = text.toSerializableShape(true) as SerializableText
        val text2 = Text(serializableText, parentId = PARENT_ID)
        assertEquals(PARENT_ID, text2.parentId)
        assertEquals(text.text, text2.text)
        assertEquals(text.bound, text2.bound)
        assertEquals(text.extra, text2.extra)
        assertEquals(
            text.renderableText.getRenderableText(),
            text2.renderableText.getRenderableText()
        )
    }

    @Test
    fun testConvertRenderableText() {
        val target = Text(Rect.byLTWH(0, 0, 5, 5))

        target.setText("0 1234 12345\n1   2 3 4 5678901 23")
        target.setExtra(TextExtra.NO_BOUND)
        assertEquals(
            listOf("0", "1234", "12345", "1   2", "3 4", "56789", "01 23"),
            target.renderableText.getRenderableText()
        )
    }

    @Test
    fun testContentBound() {
        val target = Text(Rect.byLTWH(1, 2, 5, 6))

        val defaultBoundExtra = target.extra.boundExtra

        val extraWithBorder =
            target.extra.copy(boundExtra = defaultBoundExtra.copy(isBorderEnabled = true))
        target.setExtra(extraWithBorder)
        assertEquals(
            Rect.byLTWH(2, 3, 3, 4),
            target.contentBound
        )

        val extraWithoutBorder =
            target.extra.copy(boundExtra = defaultBoundExtra.copy(isBorderEnabled = false))
        target.setExtra(extraWithoutBorder)
        assertEquals(
            Rect.byLTWH(1, 2, 5, 6),
            target.contentBound
        )
    }

    companion object {
        private const val PARENT_ID = "1"
    }
}
