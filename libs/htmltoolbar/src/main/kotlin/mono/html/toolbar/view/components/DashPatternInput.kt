@file:Suppress("FunctionName")

package mono.html.toolbar.view.components

import mono.html.Div
import mono.html.InputType
import org.w3c.dom.Element

/**
 * A component for adding Dash pattern.
 */
internal fun Element.DashPattern(
    callback: OnValueChangeCallback
): DashPatternValueBinder {
    val container = Div("comp-dash-layout")
    val dashInput = container.Pattern("Dash", 1) {
        callback.onValueChange(it, null, null)
    }
    val gapInput = container.Pattern("Gap", 0) {
        callback.onValueChange(null, it, null)
    }
    val offsetInput = container.Pattern("Shift", null) {
        callback.onValueChange(null, null, it)
    }
    return DashPatternValueBinder(dashInput, gapInput, offsetInput)
}

private fun Element.Pattern(
    name: String,
    minValue: Int?,
    onChange: (Int) -> Unit
): TextInputBoxViewHolder {
    val settings = if (minValue != null) {
        InputSettings(InputType.NUMBER, "mid" to minValue.toString())
    } else {
        InputSettings(InputType.NUMBER)
    }
    return Div("pattern")
        .TextInputBox(name, settings, isBoundIncludingParent = false) { onChange(it.toInt()) }
}

fun interface OnValueChangeCallback {
    fun onValueChange(dash: Int?, gap: Int?, offset: Int?)
}

internal class DashPatternValueBinder(
    private val dashInput: TextInputBoxViewHolder,
    private val gapInput: TextInputBoxViewHolder,
    private val offsetInput: TextInputBoxViewHolder
) {
    fun set(dash: Int, gap: Int, offset: Int) {
        dashInput.value = dash
        gapInput.value = gap
        offsetInput.value = offset
    }
}
