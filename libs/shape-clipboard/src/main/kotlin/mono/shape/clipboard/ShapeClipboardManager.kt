package mono.shape.clipboard

import kotlinx.browser.document
import kotlinx.html.dom.append
import kotlinx.html.js.textArea
import kotlinx.html.style
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.shape.serialization.AbstractSerializableShape
import org.w3c.dom.HTMLElement

/**
 * A clipboard manager specializing for shapes.
 * This class handles storing shapes to clipboard and getting shapes in clipboard from paste action.
 */
class ShapeClipboardManager(private val body: HTMLElement) {

    private val clipboardShapeMutableLiveData: MutableLiveData<List<AbstractSerializableShape>> =
        MutableLiveData(emptyList())
    val clipboardShapeLiveData: LiveData<List<AbstractSerializableShape>> =
        clipboardShapeMutableLiveData

    init {
        body.onpaste = {
            it.preventDefault()
            it.stopPropagation()
            onPasteText(it.clipboardData?.getData("text/plain").orEmpty())
        }
    }

    private fun onPasteText(text: String) {
        if (text.isBlank()) {
            return
        }
        TODO("Parse clipboard text and update live data")
    }

    fun setClipboard(shapes: List<AbstractSerializableShape>) {
        val json = Json.encodeToString(shapes)
        setClipboard(json)
    }

    private fun setClipboard(text: String) {
        body.append {
            val textBox = textArea {
                style = "position: absolute; left: -1000px; width: 0px; height: 0px;"
                +text
            }
            textBox.select()
            document.execCommand("copy")
            textBox.remove()
        }
    }
}
