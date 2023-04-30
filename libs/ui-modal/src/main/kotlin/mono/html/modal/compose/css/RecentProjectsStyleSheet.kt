/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.modal.compose.css

import mono.ui.theme.css.CssModalColor
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.background
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cursor
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.maxHeight
import org.jetbrains.compose.web.css.minHeight
import org.jetbrains.compose.web.css.outline
import org.jetbrains.compose.web.css.overflowY
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width

/**
 * A [StyleSheet] of recent projects modal styles.
 */
internal object RecentProjectsStyleSheet : StyleSheet() {
    val container by style {
        width(500.px)
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)

        margin(16.px)
    }

    val filterInputContainer by style {
        width(100.percent)
        marginBottom(8.px)

        "input" style {
            width(100.percent)
            padding(6.px, 8.px)
            borderRadius(3.px)

            outline("none")
            border(1.px, LineStyle.Solid, CssModalColor.TextFieldBorder)
            background("none")
            color(CssModalColor.TextFieldColor)

            group(self + hover, self + active, self + focus) style {
                border(1.px, LineStyle.Solid, CssModalColor.TextFieldFocusBorder)
            }
        }
    }

    val projectList by style {
        width(100.percent)
        minHeight(80.px)
        maxHeight(300.px)

        overflowY("auto")
    }

    val projectItem by style {
        padding(5.px, 6.px)
        borderRadius(3.px)

        fontSize(14.px)
        cursor("pointer")

        self + hover style {
            backgroundColor(CssModalColor.ItemHoverBackground)
        }
    }

    val noItems by style {
        height(50.px)
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.Center)
        alignItems(AlignItems.Center)
        color(CssModalColor.IndicatorColor)
    }
}
