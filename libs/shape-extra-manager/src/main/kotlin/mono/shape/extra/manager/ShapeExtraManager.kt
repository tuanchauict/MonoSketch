package mono.shape.extra.manager

import mono.shape.extra.manager.model.RectangleBorderStyle
import mono.shape.extra.manager.model.RectangleFillStyle
import mono.shape.extra.manager.predefined.PredefinedRectangleBorderStyle
import mono.shape.extra.manager.predefined.PredefinedRectangleFillStyle

/**
 * A manager class for managing shape extras
 */
class ShapeExtraManager {
    fun getRectangleFillStyle(id: String = NO_ID): RectangleFillStyle =
        PredefinedRectangleFillStyle.PREDEFINED_STYLE_MAP[id]
            ?: PredefinedRectangleFillStyle.NOFILLED_STYLE

    fun getAllPredefinedRectangleFillStyles(): List<RectangleFillStyle> =
        PredefinedRectangleFillStyle.PREDEFINED_STYLES

    fun getRectangleBorderStyle(id: String = NO_ID): RectangleBorderStyle =
        PredefinedRectangleBorderStyle.PREDEFINED_STYLE_MAP[id]
            ?: PredefinedRectangleBorderStyle.NO_BORDER

    fun getAllPredefinedRectangleBorderStyles(): List<RectangleBorderStyle> =
        PredefinedRectangleBorderStyle.PREDEFINED_STYLES

    companion object {
        private const val NO_ID = ""
    }
}
