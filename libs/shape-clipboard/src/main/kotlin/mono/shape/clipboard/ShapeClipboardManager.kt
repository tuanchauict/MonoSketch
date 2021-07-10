package mono.shape.clipboard

import kotlinx.browser.document
import kotlinx.html.dom.append
import kotlinx.html.js.textArea
import kotlinx.html.style
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mono.graphics.geo.Rect
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.shape.serialization.AbstractSerializableShape
import mono.shape.serialization.SerializableText
import mono.shape.shape.extra.TextExtra
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
        clipboardShapeMutableLiveData.value =
            try {
                Json.decodeFromString(text)
            } catch (e: Exception) {
                listOf(createTextShapeFromText(text))
            }
    }

    private fun createTextShapeFromText(text: String): SerializableText {
        val width = text.length.coerceAtMost(DEFAULT_TEXT_BOUND_WIDTH)
        val height =
            if (width < text.length) {
                val chunks = text.chunked(DEFAULT_TEXT_BOUND_WIDTH)
                chunks.size
            } else {
                1
            }

        return SerializableText(
            null,
            Rect.Companion.byLTWH(0, 0, width, height),
            text,
            TextExtra(null)
        )
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

    companion object {
        private const val DEFAULT_TEXT_BOUND_WIDTH = 60
    }
}
