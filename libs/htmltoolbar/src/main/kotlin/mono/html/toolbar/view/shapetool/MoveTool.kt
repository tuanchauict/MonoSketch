@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.js.div
import mono.html.toolbar.view.SvgIcon
import mono.html.toolbar.view.SvgPath
import mono.html.toolbar.view.Tag
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON

internal fun Tag.MoveSection() {
    Section("", hasDivider = true, isSmallSpace = true) {
        Tool {
            Row(isCenterEvenSpace = true) {
                /* ktlint-disable max-line-length */
                Icon(
                    "M7.646 2.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 3.707 2.354 9.354a.5.5 0 1 1-.708-.708l6-6z",
                    "M7.646 6.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 7.707l-5.646 5.647a.5.5 0 0 1-.708-.708l6-6z"
                )
                Icon("M7.646 4.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 5.707l-5.646 5.647a.5.5 0 0 1-.708-.708l6-6z")
                Icon("M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z")
                Icon(
                    "M1.646 6.646a.5.5 0 0 1 .708 0L8 12.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z",
                    "M1.646 2.646a.5.5 0 0 1 .708 0L8 8.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
                )
                /* ktlint-enable max-line-length */
            }
        }
    }
}

private fun Tag.Icon(vararg svgPaths: String) {
    div(classes(ICON_BUTTON)) {
        SvgIcon(16, 16) {
            for (path in svgPaths) {
                SvgPath(path)
            }
        }
    }
}
