package mono.shape.extra.manager

import mono.shape.extra.manager.model.RectangleFillStyle
import mono.shape.extra.manager.predefined.PredefinedRectangleFillStyle

/**
 * A manager class for managing shape extras
 */
class ShapeExtraManager {
    fun getRectangleFillStyle(id: String = NO_ID): RectangleFillStyle =
        PredefinedRectangleFillStyle.PREDEFINED_STYLE_MAP[id]
            ?: PredefinedRectangleFillStyle.NOFILLED_STYLE

    fun getAllPredefinedStyles(): List<RectangleFillStyle> =
        PredefinedRectangleFillStyle.PREDEFINED_STYLES

    companion object {
        private const val NO_ID = ""
    }
}
