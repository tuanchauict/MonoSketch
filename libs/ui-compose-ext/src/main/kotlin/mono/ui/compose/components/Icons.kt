/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.ui.compose.components

import androidx.compose.runtime.Composable
import mono.ui.compose.ext.Svg
import mono.ui.compose.ext.SvgPath
import mono.ui.compose.ext.fill
import mono.ui.compose.ext.size
import mono.ui.compose.ext.viewBox

@Composable
fun IconClose(iconSize: Int = 16) {
    Svg(
        attrs = {
            size(iconSize, iconSize)
            viewBox(16, 16)
            fill("currentColor")
        }
    ) {
        SvgPath(
            "M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z"
        )
        SvgPath(
            "M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z"
        )
    }
}

@Composable
fun IconChevronDown(iconSize: Int = 16) {
    Svg(
        attrs = {
            size(iconSize, iconSize)
            viewBox(16, 16)
            fill("currentColor")
        }
    ) {
        SvgPath(
            "M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z" // ktlint-disable max-line-length
        )
    }
}
