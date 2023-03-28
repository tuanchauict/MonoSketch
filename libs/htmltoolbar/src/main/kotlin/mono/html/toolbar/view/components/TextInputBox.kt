@file:Suppress("FunctionName")

package mono.html.toolbar.view.components

import mono.html.Div
import mono.html.Input
import mono.html.Span
import mono.html.setAttributes
import mono.html.setOnChangeListener
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement

/**
 * A component for adding Text input box
 */
internal fun Element.TextInputBox(
    label: String,
    inputSettings: InputSettings,
    onValueChange: (String) -> Unit
): TextInputBoxViewHolder = TextInputBox(this, label, inputSettings, onValueChange)

internal fun TextInputBox(
    parent: Element,
    label: String,
    inputSettings: InputSettings,
    onValueChange: (String) -> Unit
): TextInputBoxViewHolder {
    val container = parent.Div("text-input-layout") {
        Span("text-input-label", text = label)
    }

    val textInput = container.Input(inputSettings.type, classes = "text-input") {
        setAttributes(*inputSettings.attributes)

        setOnChangeListener { onValueChange(value) }
    }
    return TextInputBoxViewHolder(textInput)
}

/**
 * A view holder for exposing modifiable value of the text input box.
 */
internal class TextInputBoxViewHolder(private val textInput: HTMLInputElement) {
    var value: Any
        get() = textInput.value
        set(value) {
            textInput.value = value.toString()
        }

    var isEnabled: Boolean
        get() = !textInput.disabled
        set(value) {
            textInput.disabled = !value
        }
}
