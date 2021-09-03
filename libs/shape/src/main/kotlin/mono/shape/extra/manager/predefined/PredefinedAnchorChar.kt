package mono.shape.extra.manager.predefined

import mono.shape.extra.style.AnchorChar

/**
 * An object for listing all predefined anchor chars.
 */
internal object PredefinedAnchorChar {
    val PREDEFINED_ANCHOR_CHARS = listOf(
        AnchorChar(id = "A1", displayName = "▶", '◀', '▶', '▲', '▼'),
        AnchorChar(id = "A2", displayName = "■", '■'),
        AnchorChar(id = "A3", displayName = "○", '○'),
        AnchorChar(id = "A4", displayName = "◎", '◎'),
        AnchorChar(id = "A5", displayName = "●", '●'),
    )

    val PREDEFINED_ANCHOR_CHAR_MAP = PREDEFINED_ANCHOR_CHARS.associateBy { it.id }
}
