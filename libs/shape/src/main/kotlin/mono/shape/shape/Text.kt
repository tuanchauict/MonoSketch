package mono.shape.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size
import mono.shape.serialization.AbstractSerializableShape
import mono.shape.serialization.SerializableText
import mono.shape.shape.extra.ShapeExtra
import mono.shape.shape.extra.TextExtra
import kotlin.math.max

/**
 * A text shape which contains a bound and a text.
 *
 * TODO: Resize bound by text
 */
class Text(
    rect: Rect,
    id: String? = null,
    parentId: String? = null
) : AbstractShape(id = id, parentId = parentId) {
    private var userSettingSize: Size = Size.ZERO
        set(value) {
            field = value.takeIf { it.width >= 3 && it.height >= 3 } ?: Size.ZERO
        }

    // Text can be auto resized by text
    override var bound: Rect = rect

    val contentBound: Rect
        get() = if (extra.boundExtra.isBorderEnabled) {
            Rect.byLTWH(bound.left + 1, bound.top + 1, bound.width - 2, bound.height - 2)
        } else {
            bound
        }

    var text: String = ""
        private set

    override var extra: TextExtra = TextExtra.withDefault()
        private set

    var renderableText: RenderableText = RenderableText.EMPTY
        private set

    constructor(
        startPoint: Point,
        endPoint: Point,
        id: String? = null,
        parentId: String? = null
    ) : this(
        Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top),
        id = id,
        parentId = parentId
    )

    internal constructor(serializableText: SerializableText, parentId: String?) : this(
        serializableText.bound,
        id = serializableText.id,
        parentId = parentId
    ) {
        extra = TextExtra(serializableText.extra)
        setText(serializableText.text)
    }

    init {
        userSettingSize = rect.size
        updateRenderableText()
    }

    override fun toSerializableShape(isIdIncluded: Boolean): AbstractSerializableShape =
        SerializableText(
            id.takeIf { isIdIncluded },
            versionCode,
            bound,
            text,
            extra.toSerializableExtra()
        )

    override fun setBound(newBound: Rect) = update {
        val isUpdated = bound != newBound
        userSettingSize = newBound.size
        bound = newBound

        updateRenderableText()

        isUpdated
    }

    fun setText(newText: String) = update {
        val isTextChanged = newText != text
        text = newText
        updateRenderableText()
        isTextChanged
    }

    override fun setExtra(newExtra: ShapeExtra) {
        if (newExtra !is TextExtra || newExtra == extra) {
            return
        }
        update {
            extra = newExtra
            updateRenderableText()

            true
        }
    }

    private fun updateRenderableText() {
        val maxRowCharCount =
            if (extra.hasBorder()) bound.width - 2 else bound.width
        if (text != renderableText.text ||
            maxRowCharCount != renderableText.maxRowCharCount
        ) {
            renderableText = RenderableText(text, max(maxRowCharCount, 1))
        }
    }

    override fun isValid(): Boolean = text.isNotEmpty()

    fun isBoundValid(): Boolean {
        val hasBound = extra.hasBorder()
        val textBoundWidth = if (hasBound) bound.width - 2 else bound.width
        val textBoundHeight = if (hasBound) bound.height - 2 else bound.height
        return textBoundWidth >= 1 && textBoundHeight >= 1
    }

    /**
     * A class to generate renderable text.
     */
    class RenderableText(val text: String, val maxRowCharCount: Int) {
        private var renderableText: List<String>? = null
        fun getRenderableText(): List<String> {
            val nonNullRenderableText = renderableText ?: createRenderableText()
            renderableText = nonNullRenderableText
            return nonNullRenderableText
        }

        private fun createRenderableText(): List<String> =
            if (maxRowCharCount == 1) {
                text.map { it.toString() }
            } else {
                standardizeLines(text.split("\n"))
            }

        private fun standardizeLines(lines: List<String>): List<String> = lines
            .flatMap { line ->
                val adjustedLines = mutableListOf(StringBuilder())
                for (word in line.toStandardizedWords(maxRowCharCount)) {
                    val lastLine = adjustedLines.last()
                    val space = if (lastLine.isNotEmpty()) " " else ""
                    val newLineLength = lastLine.length + space.length + word.length
                    if (newLineLength <= maxRowCharCount) {
                        lastLine.append(space).append(word)
                    } else {
                        adjustedLines.add(StringBuilder(word))
                    }
                }
                adjustedLines
            }
            .map { it.toString() }

        private fun String.toStandardizedWords(maxCharCount: Int): List<String> =
            split(" ")
                .flatMap { word ->
                    if (word.length <= maxCharCount) listOf(word) else word.chunked(maxCharCount)
                }

        companion object {
            val EMPTY = RenderableText("", 0)
        }
    }
}
