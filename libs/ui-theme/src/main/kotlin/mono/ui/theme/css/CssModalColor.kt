/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.ui.theme.css

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.CSSStyleVariable
import org.jetbrains.compose.web.css.value

/**
 * An enum for CSS colors of Modal.
 */
enum class CssModalColor(private val reference: String) : CSSColorValue {
    Background("modal-background-bg"),
    ContainerBackground("modal-container-bg"),
    Divider("mainmenu-divider-color"),
    ContentColor("mainmenu-item-color"),
    TextFieldBorder("text-input-border-color"),
    TextFieldFocusBorder("text-input-border-focus-color"),
    TextFieldColor("text-input-value-color"),
    ItemHoverBackground("mainmenu-item-hover-bg"),
    IndicatorColor("shapetool-indicator-color")
    ;

    override fun toString(): String {
        return CSSStyleVariable<CSSColorValue>(reference).value().toString()
    }
}
