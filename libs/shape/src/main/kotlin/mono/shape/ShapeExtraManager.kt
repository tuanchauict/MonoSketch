package mono.shape

import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.shape.extra.style.AnchorChar
import mono.shape.extra.style.RectangleFillStyle
import mono.shape.extra.style.StraightStrokeDashPattern
import mono.shape.extra.style.StraightStrokeStyle
import mono.shape.extra.style.TextAlign
import mono.shape.extra.manager.predefined.PredefinedAnchorChar
import mono.shape.extra.manager.predefined.PredefinedRectangleFillStyle
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle

/**
 * A manager class for managing shape extras
 */
object ShapeExtraManager {
    val RECTANGLE_STYLE_NO_FILLED = PredefinedRectangleFillStyle.NOFILLED_STYLE

    var defaultExtraState: DefaultExtraState =
        DefaultExtraState(
            isFillEnabled = false,
            fillStyle = PredefinedRectangleFillStyle.PREDEFINED_STYLES[0],
            isBorderEnabled = true,
            borderStyle = PredefinedStraightStrokeStyle.PREDEFINED_STYLES[0],
            isStartHeadAnchorCharEnabled = false,
            isDashEnabled = false,
            dashPattern = StraightStrokeDashPattern.SOLID,
            startHeadAnchorChar = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0],
            isEndHeadAnchorCharEnabled = false,
            endHeadAnchorChar = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0],
            textAlign = TextAlign(TextAlign.HorizontalAlign.MIDDLE, TextAlign.VerticalAlign.MIDDLE)
        )
        private set

    private val defaultExtraStateUpdateMutableLiveData = MutableLiveData(Unit)
    val defaultExtraStateUpdateLiveData: LiveData<Unit> = defaultExtraStateUpdateMutableLiveData

    fun setDefaultValues(
        isFillEnabled: Boolean? = null,
        fillStyleId: String? = null,
        isBorderEnabled: Boolean? = null,
        borderStyleId: String? = null,
        isDashEnabled: Boolean? = null,
        dashPattern: StraightStrokeDashPattern? = null,
        isStartHeadAnchorCharEnabled: Boolean? = null,
        startHeadAnchorCharId: String? = null,
        isEndHeadAnchorCharEnabled: Boolean? = null,
        endHeadAnchorCharId: String? = null,
        textHorizontalAlign: TextAlign.HorizontalAlign? = null,
        textVerticalAlign: TextAlign.VerticalAlign? = null
    ) {
        val newTextHorizontalAlign =
            textHorizontalAlign ?: defaultExtraState.textAlign.horizontalAlign
        val newTextVerticalAlign =
            textVerticalAlign ?: defaultExtraState.textAlign.verticalAlign
        defaultExtraState = DefaultExtraState(
            isFillEnabled = isFillEnabled ?: defaultExtraState.isFillEnabled,
            fillStyle = getRectangleFillStyle(fillStyleId),
            isBorderEnabled = isBorderEnabled ?: defaultExtraState.isBorderEnabled,
            borderStyle = getRectangleBorderStyle(borderStyleId),
            isDashEnabled = isDashEnabled ?: defaultExtraState.isDashEnabled,
            dashPattern = dashPattern ?: defaultExtraState.dashPattern,
            isStartHeadAnchorCharEnabled = isStartHeadAnchorCharEnabled
                ?: defaultExtraState.isStartHeadAnchorCharEnabled,
            startHeadAnchorChar = getStartHeadAnchorChar(startHeadAnchorCharId),
            isEndHeadAnchorCharEnabled = isEndHeadAnchorCharEnabled
                ?: defaultExtraState.isEndHeadAnchorCharEnabled,
            endHeadAnchorChar = getEndHeadAnchorChar(endHeadAnchorCharId),
            textAlign = TextAlign(newTextHorizontalAlign, newTextVerticalAlign)
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
        default: StraightStrokeStyle = defaultExtraState.borderStyle
    ): StraightStrokeStyle = PredefinedStraightStrokeStyle.PREDEFINED_STYLE_MAP[id] ?: default

    fun getAllPredefinedStrokeStyles(): List<StraightStrokeStyle> =
        PredefinedStraightStrokeStyle.PREDEFINED_STYLES

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
        val borderStyle: StraightStrokeStyle,
        val isDashEnabled: Boolean,
        val dashPattern: StraightStrokeDashPattern,
        val isStartHeadAnchorCharEnabled: Boolean,
        val startHeadAnchorChar: AnchorChar,
        val isEndHeadAnchorCharEnabled: Boolean,
        val endHeadAnchorChar: AnchorChar,
        val textAlign: TextAlign
    )
}
