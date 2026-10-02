/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.extra.manager.predefined

import mono.shape.extra.style.ShadowStyle

/**
 * An object for listing all predefined shadow styles.
 */
internal object PredefinedShadowStyle {
    val PREDEFINED_STYLES = listOf(
        ShadowStyle(
            id = "SH1",
            displayName = "░",
            char = '░'
        ),
        ShadowStyle(
            id = "SH2",
            displayName = "▒",
            char = '▒'
        ),
        ShadowStyle(
            id = "SH3",
            displayName = "█",
            char = '█'
        )
    )

    val PREDEFINED_STYLE_MAP = PREDEFINED_STYLES.associateBy { it.id }
}
