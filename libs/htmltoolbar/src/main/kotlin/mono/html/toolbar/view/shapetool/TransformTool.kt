@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.InputType
import kotlinx.html.js.div
import kotlinx.html.js.input
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.span
import mono.graphics.geo.Rect
import mono.html.ext.Tag
import mono.html.toolbar.OneTimeActionType
import mono.html.toolbar.view.shapetool.Class.CENTER_VERTICAL
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.HALF
import mono.html.toolbar.view.shapetool.Class.INLINE_TITLE
import mono.html.toolbar.view.shapetool.Class.INPUT_TEXT
import mono.html.toolbar.view.shapetool.Class.MEDIUM
import mono.html.toolbar.view.shapetool.Class.ROW
import mono.html.toolbar.view.shapetool.Class.SHORT
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement

internal abstract class TransformToolViewController(
    rootView: HTMLDivElement
) : ToolViewController(rootView) {
    abstract fun setValue(bound: Rect)

    abstract fun setEnabled(isPositionEnabled: Boolean, isSizeEnabled: Boolean)
}

private class TransformToolViewControllerImpl(
    rootView: HTMLDivElement,
    private val xInput: HTMLInputElement,
    private val yInput: HTMLInputElement,
    private val wInput: HTMLInputElement,
    private val hInput: HTMLInputElement
) : TransformToolViewController(rootView) {
    override fun setValue(bound: Rect) {
        xInput.value = bound.left.toString()
        yInput.value = bound.top.toString()
        wInput.value = bound.width.toString()
        hInput.value = bound.height.toString()
    }

    override fun setEnabled(isPositionEnabled: Boolean, isSizeEnabled: Boolean) {
        xInput.disabled = !isPositionEnabled
        yInput.disabled = !isPositionEnabled
        wInput.disabled = !isSizeEnabled
        hInput.disabled = !isSizeEnabled
    }
}

internal fun Tag.TransformSection(
    setOneTimeAction: (OneTimeActionType) -> Unit
): TransformToolViewController {
    var xInput: HTMLInputElement? = null
    var yInput: HTMLInputElement? = null
    var wInput: HTMLInputElement? = null
    var hInput: HTMLInputElement? = null
    val rootView = Section("TRANSFORM") {
        Tool(hasMoreBottomSpace = true) {
            Row(true) {
                xInput = NumberCell("X", 0) {
                    setOneTimeAction(OneTimeActionType.ChangeShapeBound(newLeft = it))
                }
                wInput = NumberCell("W", 10, 1) {
                    setOneTimeAction(OneTimeActionType.ChangeShapeBound(newWidth = it))
                }
            }
            Row {
                yInput = NumberCell("Y", 0) {
                    setOneTimeAction(OneTimeActionType.ChangeShapeBound(newTop = it))
                }
                hInput = NumberCell("H", 10, 1) {
                    setOneTimeAction(OneTimeActionType.ChangeShapeBound(newHeight = it))
                }
            }
        }
    }
    return TransformToolViewControllerImpl(rootView, xInput!!, yInput!!, wInput!!, hInput!!)
}

private fun Tag.NumberCell(
    title: String,
    value: Int,
    minValue: Int? = null,
    onValueChange: (Int) -> Unit
): HTMLInputElement {
    var result: HTMLInputElement? = null
    div(classes(COLUMN, HALF)) {
        div(classes(ROW, CENTER_VERTICAL)) {
            span(classes(INLINE_TITLE, SHORT)) { +title }
            result = input(InputType.number, classes = classes(INPUT_TEXT, MEDIUM)) {
                if (minValue != null) {
                    attributes["min"] = minValue.toString()
                }
                this.value = value.toString()

                onChangeFunction = {
                    val target = it.currentTarget as HTMLInputElement
                    onValueChange(target.value.toInt())
                }
            }
        }
    }
    return result!!
}
