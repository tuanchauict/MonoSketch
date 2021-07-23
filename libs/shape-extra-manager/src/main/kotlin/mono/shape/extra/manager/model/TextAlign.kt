package mono.shape.extra.manager.model

/**
 * A model for defining text aligns.
 */
data class TextAlign(val horizontalAlign: HorizontalAlign, val verticalAlign: VerticalAlign) {
    constructor(
        textHorizontalAlign: Int,
        textVerticalAlign: Int
    ) : this(HorizontalAlign.ALL[textHorizontalAlign], VerticalAlign.ALL[textVerticalAlign])

    enum class HorizontalAlign {
        LEFT, MIDDLE, RIGHT;

        companion object {
            val ALL = values()
        }
    }

    enum class VerticalAlign {
        TOP, MIDDLE, BOTTOM;

        companion object {
            val ALL = values()
        }
    }
}
