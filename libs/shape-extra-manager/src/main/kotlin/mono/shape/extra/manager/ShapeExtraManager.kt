package mono.shape.extra.manager

import mono.livedata.LiveData
import mono.livedata.MutableLiveData
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

    var defaultExtraState: DefaultExtraState =
        DefaultExtraState(
            isFillEnabled = false,
            fillStyle = PredefinedRectangleFillStyle.PREDEFINED_STYLES[0],
            isBorderEnabled = true,
            borderStyle = PredefinedRectangleBorderStyle.PREDEFINED_STYLES[0],
            isStartHeadAnchorCharEnabled = false,
            startHeadAnchorChar = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0],
            isEndHeadAnchorCharEnabled = false,
            endHeadAnchorChar = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0]
        )
        private set

    private val defaultExtraStateUpdateMutableLiveData = MutableLiveData(Unit)
    val defaultExtraStateUpdateLiveData: LiveData<Unit> = defaultExtraStateUpdateMutableLiveData

    fun setDefaultValues(
        isFillEnabled: Boolean? = null,
        fillStyleId: String? = null,
        isBorderEnabled: Boolean? = null,
        borderStyleId: String? = null,
        isStartHeadAnchorCharEnabled: Boolean? = null,
        startHeadAnchorCharId: String? = null,
        isEndHeadAnchorCharEnabled: Boolean? = null,
        endHeadAnchorCharId: String? = null
    ) {
        defaultExtraState = DefaultExtraState(
            isFillEnabled = isFillEnabled ?: defaultExtraState.isFillEnabled,
            fillStyle = getRectangleFillStyle(fillStyleId),
            isBorderEnabled = isBorderEnabled ?: defaultExtraState.isBorderEnabled,
            borderStyle = getRectangleBorderStyle(borderStyleId),
            isStartHeadAnchorCharEnabled = isStartHeadAnchorCharEnabled
                ?: defaultExtraState.isStartHeadAnchorCharEnabled,
            startHeadAnchorChar = getStartHeadAnchorChar(startHeadAnchorCharId),
            isEndHeadAnchorCharEnabled = isEndHeadAnchorCharEnabled
                ?: defaultExtraState.isEndHeadAnchorCharEnabled,
            endHeadAnchorChar = getEndHeadAnchorChar(endHeadAnchorCharId)
        )
        defaultExtraStateUpdateMutableLiveData.value = Unit
    }

    fun getRectangleFillStyle(
        id: String?,
        default: RectangleFillStyle = defaultExtraState.fillStyle
    ): RectangleFillStyle = PredefinedRectangleFillStyle.PREDEFINED_STYLE_MAP[id] ?: default

    fun getAllPredefinedRectangleFillStyles(): List<RectangleFillStyle> =
        PredefinedRectangleFillStyle.PREDEFINED_STYLES

    fun getRectangleBorderStyle(
        id: String?,
        default: RectangleBorderStyle = defaultExtraState.borderStyle
    ): RectangleBorderStyle = PredefinedRectangleBorderStyle.PREDEFINED_STYLE_MAP[id] ?: default

    fun getAllPredefinedRectangleBorderStyles(): List<RectangleBorderStyle> =
        PredefinedRectangleBorderStyle.PREDEFINED_STYLES

    fun getStartHeadAnchorChar(
        id: String?,
        default: AnchorChar = defaultExtraState.startHeadAnchorChar
    ): AnchorChar = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHAR_MAP[id] ?: default

    fun getEndHeadAnchorChar(
        id: String?,
        default: AnchorChar = defaultExtraState.endHeadAnchorChar
    ): AnchorChar = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHAR_MAP[id] ?: default

    fun getAllPredefinedAnchorChars(): List<AnchorChar> =
        PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS

    data class DefaultExtraState(
        val isFillEnabled: Boolean,
        val fillStyle: RectangleFillStyle,
        val isBorderEnabled: Boolean,
        val borderStyle: RectangleBorderStyle,
        val isStartHeadAnchorCharEnabled: Boolean,
        val startHeadAnchorChar: AnchorChar,
        val isEndHeadAnchorCharEnabled: Boolean,
        val endHeadAnchorChar: AnchorChar
    )
}
