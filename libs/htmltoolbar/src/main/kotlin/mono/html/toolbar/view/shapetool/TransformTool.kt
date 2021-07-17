@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.InputType
import kotlinx.html.js.div
import kotlinx.html.js.input
import kotlinx.html.js.span
import mono.html.toolbar.view.Tag
import mono.html.toolbar.view.shapetool.Class.CENTER_VERTICAL
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.HALF
import mono.html.toolbar.view.shapetool.Class.INLINE_TITLE
import mono.html.toolbar.view.shapetool.Class.INPUT_TEXT
import mono.html.toolbar.view.shapetool.Class.MEDIUM
import mono.html.toolbar.view.shapetool.Class.ROW
import mono.html.toolbar.view.shapetool.Class.SHORT

internal fun Tag.TransformSection(
    left: Int,
    top: Int,
    width: Int,
    height: Int
) {
    Section("TRANSFORM") {
        Tool(hasMoreBottomSpace = true) {
            Row(true) {
                NumberCell("X", left)
                NumberCell("W", width, 1)
            }
            Row {
                NumberCell("Y", top)
                NumberCell("H", height, 1)
            }
        }
    }
}

private fun Tag.NumberCell(title: String, value: Int, minValue: Int? = null) {
    div(classes(COLUMN, HALF)) {
        div(classes(ROW, CENTER_VERTICAL)) {
            span(classes(INLINE_TITLE, SHORT)) { +title }
            input(InputType.number, classes = classes(INPUT_TEXT, MEDIUM)) {
                if (minValue != null) {
                    attributes["min"] = minValue.toString()
                }
                this.value = value.toString()
            }
        }
    }
}
