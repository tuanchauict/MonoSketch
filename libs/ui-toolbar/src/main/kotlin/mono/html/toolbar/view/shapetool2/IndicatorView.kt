/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool2

import androidx.compose.runtime.Composable
import mono.ui.compose.ext.Svg
import mono.ui.compose.ext.SvgPath
import mono.ui.compose.ext.fill
import mono.ui.compose.ext.size
import mono.ui.compose.ext.viewBox
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun IndicatorView(isVisible: Boolean) {
    if (!isVisible) {
        return
    }
    Div(
        attrs = {
            classes("tool-indicator")
        }
    ) {
        Span(
            attrs = { classes("indicator-text") }
        ) {
            Text("Select a shape for updating its properties here")
        }
    }
}

@Composable
internal fun FooterView() {
    Div(
        attrs = {
            classes("shape-tools__footer")
        }
    ) {
        A(
            attrs = {
                href("https://github.com/tuanchauict/MonoSketch")
                target(ATarget.Blank)
            }
        ) {
            Svg(
                attrs = {
                    size(16, 16)
                    viewBox(32, 32)
                    fill("currentColor")
                }
            ) {
                SvgPath(
                    "M16.288 0a16.291 16.291 0 0 0-5.148 31.747c.815.149 1.112-.353 1.112-.785 0-.387-.014-1.411-.022-2.771-4.531.985-5.487-2.183-5.487-2.183a4.315 4.315 0 0 0-1.809-2.383c-1.479-1.011.112-.99.112-.99a3.42 3.42 0 0 1 2.495 1.678 3.468 3.468 0 0 0 4.741 1.354 3.482 3.482 0 0 1 1.034-2.178c-3.617-.411-7.42-1.808-7.42-8.051a6.3 6.3 0 0 1 1.677-4.371 5.852 5.852 0 0 1 .16-4.311s1.367-.438 4.479 1.67a15.448 15.448 0 0 1 8.156 0c3.11-2.108 4.475-1.67 4.475-1.67a5.854 5.854 0 0 1 .163 4.311 6.286 6.286 0 0 1 1.674 4.371c0 6.258-3.809 7.635-7.438 8.038a3.889 3.889 0 0 1 1.106 3.017c0 2.178-.02 3.935-.02 4.469 0 .435.294.942 1.12.783A16.292 16.292 0 0 0 16.288 0z" // ktlint-disable max-line-length
                )
            }
            Span(
                attrs = {
                    style {
                        marginLeft(6.px)
                    }
                }
            ) {
                Text("GitHub")
            }

        }
    }
}
