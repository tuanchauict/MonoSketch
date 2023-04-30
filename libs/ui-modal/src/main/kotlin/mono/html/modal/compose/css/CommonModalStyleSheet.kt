/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.modal.compose.css

import mono.ui.theme.css.CssModalColor
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.bottom
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.left
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.right
import org.jetbrains.compose.web.css.top
import org.jetbrains.compose.web.css.width

/**
 * A [StyleSheet] of common-use modal styles.
 */
internal object CommonModalStyleSheet : StyleSheet() {
    val fullscreenModal by style {
        property("z-index", 1000)
        position(Position.Fixed)
        left(0.px)
        top(0.px)
        right(0.px)
        bottom(0.px)

        width(100.percent)
        height(100.percent)

        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.Center)
        alignItems(AlignItems.Start)

        backgroundColor(CssModalColor.Background)
    }

    val blurBackdrop by style {
        property("backdrop-filter", "blur(3px)")
    }

    val container by style {
        backgroundColor(CssModalColor.ContainerBackground)
        borderRadius(6.px)
        border(1.px, LineStyle.Solid, CssModalColor.Divider)
        padding(8.px, 6.px)
        color(CssModalColor.ContentColor)

        property("box-shadow", "0 8px 20px rgba(0, 0, 0, 0.25)")
    }
}
