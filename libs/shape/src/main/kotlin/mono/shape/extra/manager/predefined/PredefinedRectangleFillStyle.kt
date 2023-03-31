package mono.shape.extra.manager.predefined

import mono.common.Characters
import mono.graphics.bitmap.drawable.CharDrawable
import mono.shape.extra.style.RectangleFillStyle

/**
 * An object for listing all predefined rectangle fill styles.
 */
internal object PredefinedRectangleFillStyle {
    val NOFILLED_STYLE = RectangleFillStyle(
        id = "F0",
        displayName = "No Fill",
        CharDrawable(Characters.TRANSPARENT_CHAR)
    )

    val PREDEFINED_STYLES = listOf(
        RectangleFillStyle(
            id = "F1",
            displayName = "${Characters.NBSP}",
            CharDrawable(' ')
        ),
        RectangleFillStyle(
            id = "F2",
            displayName = "█",
            CharDrawable('█')
        ),
        RectangleFillStyle(
            id = "F3",
            displayName = "▒",
            CharDrawable('▒')
        ),
        RectangleFillStyle(
            id = "F4",
            displayName = "░",
            CharDrawable('░')
        ),
        RectangleFillStyle(
            id = "F5",
            displayName = "▚",
            CharDrawable('▚')
        )
    )

    val PREDEFINED_STYLE_MAP = PREDEFINED_STYLES.associateBy { it.id }
}
