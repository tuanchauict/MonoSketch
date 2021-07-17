@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.js.div
import kotlinx.html.js.span
import mono.html.toolbar.view.SvgIcon
import mono.html.toolbar.view.SvgPath
import mono.html.toolbar.view.Tag
import mono.html.toolbar.view.shapetool.Class.ADD_RIGHT_SPACE
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import mono.html.toolbar.view.shapetool.Class.MEDIUM
import mono.html.toolbar.view.shapetool.Class.QUARTER
import mono.html.toolbar.view.shapetool.Class.SELECTED

internal fun Tag.TextSection() {
    Section("TEXT", hasDivider = false) {
        Tool(true) {
            TextTool("Alignment") {
                /* ktlint-disable max-line-length */
                Icon(
                    "M2 12.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm0-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm0-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm0-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z"
                )
                Icon(
                    "M4 12.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-2-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm2-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-2-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z"
                )
                Icon(
                    "M6 12.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-4-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5zm4-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-4-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5z",
                    isSelected = true
                )
                /* ktlint-enable max-line-length */
            }

            TextTool("Position") {
                Icon(
                    "M8 11h3v10h2V11h3l-4-4-4 4zM4 3v2h16V3H4z",
                    viewPortSize = 24,
                    isSelected = true
                )
                Icon(
                    "M8 19h3v4h2v-4h3l-4-4-4 4zm8-14h-3V1h-2v4H8l4 4 4-4zM4 11v2h16v-2H4z",
                    viewPortSize = 24
                )
                Icon("M16 13h-3V3h-2v10H8l4 4 4-4zM4 19v2h16v-2H4z", viewPortSize = 24)
            }
        }
    }
}

private fun Tag.TextTool(name: String, iconBlock: Tag.() -> Unit) {
    Row(isVerticalCenter = true, isMoreBottomSpaceRequired = true) {
        div(classes(COLUMN, ADD_RIGHT_SPACE, QUARTER)) {
            +name
        }
        div(classes(COLUMN)) {
            Row {
                iconBlock()
            }
        }
    }
}

private fun Tag.Icon(svgPath: String, viewPortSize: Int = 16, isSelected: Boolean = false) {
    span(classes(ICON_BUTTON, MEDIUM, ADD_RIGHT_SPACE, SELECTED x isSelected)) {
        SvgIcon(16, 16, viewPortSize, viewPortSize) {
            SvgPath(svgPath)
        }
    }
}
