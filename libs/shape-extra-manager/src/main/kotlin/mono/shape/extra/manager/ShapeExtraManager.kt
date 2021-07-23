package mono.shape.extra.manager

import mono.shape.extra.manager.model.RectangleBorderStyle
import mono.shape.extra.manager.model.RectangleFillStyle
import mono.shape.extra.manager.predefined.PredefinedRectangleBorderStyle
import mono.shape.extra.manager.predefined.PredefinedRectangleFillStyle

/**
 * A manager class for managing shape extras
 */
object ShapeExtraManager {
    val RECTANGLE_STYLE_NO_FILLED = PredefinedRectangleFillStyle.NOFILLED_STYLE
    val RECTANGLE_STYLE_NO_BORDER = PredefinedRectangleBorderStyle.NO_BORDER

    val DEFAULT_RECTANGLE_FILL_STYLE = PredefinedRectangleFillStyle.PREDEFINED_STYLES[0]
    val DEFAULT_RECTANGLE_BORDER_STYLE = PredefinedRectangleBorderStyle.PREDEFINED_STYLES[0]

    fun getRectangleFillStyle(id: String?): RectangleFillStyle? =
        PredefinedRectangleFillStyle.PREDEFINED_STYLE_MAP[id]

    fun getAllPredefinedRectangleFillStyles(): List<RectangleFillStyle> =
        PredefinedRectangleFillStyle.PREDEFINED_STYLES

    fun getRectangleBorderStyle(id: String?): RectangleBorderStyle? =
        PredefinedRectangleBorderStyle.PREDEFINED_STYLE_MAP[id]

    fun getAllPredefinedRectangleBorderStyles(): List<RectangleBorderStyle> =
        PredefinedRectangleBorderStyle.PREDEFINED_STYLES
}
