@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import mono.graphics.geo.Rect
import mono.html.Div
import mono.html.InputNumber
import mono.html.Span
import mono.html.setAttributes
import mono.html.setOnChangeListener
import mono.html.toolbar.OneTimeActionType
import mono.html.toolbar.view.shapetool.Class.CENTER_VERTICAL
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.HALF
import mono.html.toolbar.view.shapetool.Class.INLINE_TITLE
import mono.html.toolbar.view.shapetool.Class.INPUT_TEXT
import mono.html.toolbar.view.shapetool.Class.MEDIUM
import mono.html.toolbar.view.shapetool.Class.ROW
import mono.html.toolbar.view.shapetool.Class.SHORT
import org.w3c.dom.Element
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

internal fun Element.TransformSection(
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

private fun Element.NumberCell(
    title: String,
    value: Int,
    minValue: Int? = null,
    onValueChange: (Int) -> Unit
): HTMLInputElement {
    var result: HTMLInputElement? = null
    Div(classes(COLUMN, HALF)) {
        Div(classes(ROW, CENTER_VERTICAL)) {
            Span(classes(INLINE_TITLE, SHORT), title)
            result = InputNumber(classes(INPUT_TEXT, MEDIUM)) {
                if (minValue != null) {
                    setAttributes("min" to minValue)
                }
                this.value = value.toString()

                setOnChangeListener {
                    onValueChange(this.value.toInt())
                }
            }
        }
    }
    return result!!
}
