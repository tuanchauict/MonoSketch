package mono.shape.clipboard

import kotlinx.browser.document
import kotlinx.html.dom.append
import kotlinx.html.js.textArea
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
        document.onpaste = {
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
        val lines = text
            .split('\n')
            .flatMap { it.chunked(DEFAULT_TEXT_BOUND_WIDTH) }
        val width = lines.maxOf { it.length }
        val height = lines.size

        // Replace space chars with nbsp chars to avoid space chars are being trimmed by the browser
        val toBeUsedText = text.replace(' ', NON_BREAKING_SPACE_CHAR)
        return SerializableText(
            bound = Rect.Companion.byLTWH(0, 0, width, height),
            text = toBeUsedText,
            extra = TextExtra.NO_BOUND.toSerializableExtra()
        )
    }

    fun setClipboard(shapes: List<AbstractSerializableShape>) {
        val json = Json.encodeToString(shapes)
        setClipboardText(json)
    }

    fun setClipboardText(text: String) {
        body.append {
            val textBox = textArea(classes = "hidden", content = text)
            textBox.select()
            document.execCommand("copy")
            textBox.remove()
        }
    }

    companion object {
        private const val DEFAULT_TEXT_BOUND_WIDTH = 400
        private const val NON_BREAKING_SPACE_CHAR = '\u00a0'
    }
}
