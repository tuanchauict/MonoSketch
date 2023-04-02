/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.bitmap.manager.factory

import mono.graphics.geo.Rect
import mono.shape.shape.Text
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [TextBitmapFactory]
 */
class TextBitmapFactoryTest {
    @Test
    fun testToBitmap() {
        val text = Text(Rect.byLTWH(0, 0, 7, 5))
        text.setText("012345678\nabc")
        val bitmap = TextBitmapFactory.toBitmap(
            text.bound.size,
            text.renderableText.getRenderableText(),
            text.extra,
            isTextEditingMode = false
        )
        assertEquals(
            """
                |┌─────┐
                |│01234│
                |│5678 │
                |│ abc │
                |└─────┘
            """.trimMargin(),
            bitmap.toString()
        )
    }
}
