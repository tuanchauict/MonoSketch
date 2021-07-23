package mono.shape.extra.manager

import mono.shape.extra.manager.model.AnchorChar
import mono.shape.extra.manager.model.RectangleBorderStyle
import mono.shape.extra.manager.model.RectangleFillStyle
import mono.shape.extra.manager.predefined.PredefinedAnchorChar
import mono.shape.extra.manager.predefined.PredefinedRectangleBorderStyle
import mono.shape.extra.manager.predefined.PredefinedRectangleFillStyle

/**
 * A manager class for managing shape extras
 */
object ShapeExtraManager {
    val RECTANGLE_STYLE_NO_FILLED = PredefinedRectangleFillStyle.NOFILLED_STYLE
    val RECTANGLE_STYLE_NO_BORDER = PredefinedRectangleBorderStyle.NO_BORDER

    private val DEFAULT_RECTANGLE_FILL_STYLE = PredefinedRectangleFillStyle.PREDEFINED_STYLES[0]
    private val DEFAULT_RECTANGLE_BORDER_STYLE = PredefinedRectangleBorderStyle.PREDEFINED_STYLES[0]

    private val DEFAULT_ANCHOR_CHAR = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0]

    fun getRectangleFillStyle(
        id: String?,
        default: RectangleFillStyle = DEFAULT_RECTANGLE_FILL_STYLE
    ): RectangleFillStyle = PredefinedRectangleFillStyle.PREDEFINED_STYLE_MAP[id] ?: default

    fun getAllPredefinedRectangleFillStyles(): List<RectangleFillStyle> =
        PredefinedRectangleFillStyle.PREDEFINED_STYLES

    fun getRectangleBorderStyle(
        id: String?,
        default: RectangleBorderStyle = DEFAULT_RECTANGLE_BORDER_STYLE
    ): RectangleBorderStyle = PredefinedRectangleBorderStyle.PREDEFINED_STYLE_MAP[id] ?: default

    fun getAllPredefinedRectangleBorderStyles(): List<RectangleBorderStyle> =
        PredefinedRectangleBorderStyle.PREDEFINED_STYLES

    fun getAnchorChar(id: String?, default: AnchorChar = DEFAULT_ANCHOR_CHAR): AnchorChar =
        PredefinedAnchorChar.PREDEFINED_ANCHOR_CHAR_MAP[id] ?: default

    fun getAllPredefinedAnchorChars(): List<AnchorChar> =
        PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS
}
