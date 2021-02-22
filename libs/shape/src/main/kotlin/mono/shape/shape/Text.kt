package mono.shape.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size

/**
 * A text shape which contains a bound and a text.
 */
class Text(rect: Rect, parentId: Int? = null) : AbstractShape(parentId = parentId) {
    private var userSettingSize: Size = Size.ZERO
        set(value) {
            field = value.takeIf { it.width >= 3 && it.height >= 3 } ?: Size.ZERO
        }

    // Text can be auto resized by text
    override var bound: Rect = rect

    override var extra: Extra = Extra(Rectangle.Extra.DEFAULT, text = "")
        private set

    var renderableText: List<String> = emptyList()
        private set

    constructor(startPoint: Point, endPoint: Point, parentId: Int?) : this(
        Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top),
        parentId
    )

    init {
        userSettingSize = rect.size
        updateRenderableText()
    }

    override fun setExtra(extra: Any) {
        if (extra !is Extra) {
            return
        }
        this.extra = extra
        updateRenderableText()
    }

    private fun updateRenderableText() {
        renderableText = if (extra.text.isNotEmpty()) {
            val maxCharCount = if (extra.boundExtra != null) bound.width - 2 else bound.width
            toRenderableText(extra.text, maxCharCount)
        } else {
            emptyList()
        }
    }

    private fun toRenderableText(text: String, maxCharCount: Int): List<String> =
        text
            .split("\n")
            .fold(mutableListOf(StringBuilder())) { adjustedLines, line ->
                for (word in line.toStandardizedWords(maxCharCount)) {
                    val lastLine = adjustedLines.last()
                    val space = if (lastLine.isNotEmpty()) " " else ""
                    val newLineLength =
                        lastLine.length + space.length + word.length
                    if (newLineLength <= maxCharCount) {
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

    data class Extra(
        val boundExtra: Rectangle.Extra?,
        val text: String
    )
}
