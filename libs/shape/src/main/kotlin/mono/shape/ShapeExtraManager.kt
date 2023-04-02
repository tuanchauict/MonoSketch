/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape

import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.shape.extra.LineExtra
import mono.shape.extra.RectangleExtra
import mono.shape.extra.manager.predefined.PredefinedAnchorChar
import mono.shape.extra.manager.predefined.PredefinedRectangleFillStyle
import mono.shape.extra.manager.predefined.PredefinedStraightStrokeStyle
import mono.shape.extra.style.AnchorChar
import mono.shape.extra.style.RectangleFillStyle
import mono.shape.extra.style.StraightStrokeDashPattern
import mono.shape.extra.style.StraightStrokeStyle
import mono.shape.extra.style.TextAlign

/**
 * A manager class for managing shape extras
 */
object ShapeExtraManager {
    var defaultRectangleExtra: RectangleExtra = RectangleExtra(
        isFillEnabled = false,
        userSelectedFillStyle = PredefinedRectangleFillStyle.PREDEFINED_STYLES[0],
        isBorderEnabled = true,
        userSelectedBorderStyle = PredefinedStraightStrokeStyle.PREDEFINED_STYLES[0],
        dashPattern = StraightStrokeDashPattern.SOLID
    )
        private set

    var defaultLineExtra: LineExtra = LineExtra(
        isStrokeEnabled = true,
        PredefinedStraightStrokeStyle.PREDEFINED_STYLES[0],

        isStartAnchorEnabled = false,
        userSelectedStartAnchor = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0],

        isEndAnchorEnabled = false,
        userSelectedEndAnchor = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0],

        dashPattern = StraightStrokeDashPattern.SOLID
    )
        private set

    var defaultTextAlign: TextAlign =
        TextAlign(TextAlign.HorizontalAlign.MIDDLE, TextAlign.VerticalAlign.MIDDLE)
        private set

    private val defaultExtraStateUpdateMutableLiveData = MutableLiveData(Unit)
    val defaultExtraStateUpdateLiveData: LiveData<Unit> = defaultExtraStateUpdateMutableLiveData

    fun setDefaultValues(
        isFillEnabled: Boolean? = null,
        fillStyleId: String? = null,

        isBorderEnabled: Boolean? = null,
        borderStyleId: String? = null,

        isLineStrokeEnabled: Boolean? = null,
        lineStrokeStyleId: String? = null,

        dashPattern: StraightStrokeDashPattern? = null,

        isStartHeadAnchorCharEnabled: Boolean? = null,
        startHeadAnchorCharId: String? = null,

        isEndHeadAnchorCharEnabled: Boolean? = null,
        endHeadAnchorCharId: String? = null,

        textHorizontalAlign: TextAlign.HorizontalAlign? = null,
        textVerticalAlign: TextAlign.VerticalAlign? = null
    ) {
        defaultRectangleExtra = RectangleExtra(
            isFillEnabled ?: defaultRectangleExtra.isFillEnabled,
            getRectangleFillStyle(fillStyleId),
            isBorderEnabled ?: defaultRectangleExtra.isBorderEnabled,
            getRectangleBorderStyle(borderStyleId),
            dashPattern ?: defaultRectangleExtra.dashPattern
        )

        defaultLineExtra = LineExtra(
            isStrokeEnabled = isLineStrokeEnabled ?: defaultLineExtra.isStrokeEnabled,
            userSelectedStrokeStyle = getLineStrokeStyle(lineStrokeStyleId),

            isStartAnchorEnabled = isStartHeadAnchorCharEnabled
                ?: defaultLineExtra.isStartAnchorEnabled,
            userSelectedStartAnchor = getStartHeadAnchorChar(startHeadAnchorCharId),

            isEndAnchorEnabled = isEndHeadAnchorCharEnabled ?: defaultLineExtra.isEndAnchorEnabled,
            userSelectedEndAnchor = getEndHeadAnchorChar(endHeadAnchorCharId),

            dashPattern = StraightStrokeDashPattern.SOLID
        )

        defaultTextAlign = TextAlign(
            textHorizontalAlign ?: defaultTextAlign.horizontalAlign,
            textVerticalAlign ?: defaultTextAlign.verticalAlign
        )

        defaultExtraStateUpdateMutableLiveData.value = Unit
    }

    fun getRectangleFillStyle(
        id: String?,
        default: RectangleFillStyle = defaultRectangleExtra.userSelectedFillStyle
    ): RectangleFillStyle = PredefinedRectangleFillStyle.PREDEFINED_STYLE_MAP[id] ?: default

    fun getAllPredefinedRectangleFillStyles(): List<RectangleFillStyle> =
        PredefinedRectangleFillStyle.PREDEFINED_STYLES

    fun getRectangleBorderStyle(
        id: String?,
        default: StraightStrokeStyle = defaultRectangleExtra.userSelectedBorderStyle
    ): StraightStrokeStyle = PredefinedStraightStrokeStyle.PREDEFINED_STYLE_MAP[id] ?: default

    fun getLineStrokeStyle(
        id: String?,
        default: StraightStrokeStyle = defaultLineExtra.userSelectedStrokeStyle
    ): StraightStrokeStyle = PredefinedStraightStrokeStyle.PREDEFINED_STYLE_MAP[id] ?: default

    fun getAllPredefinedStrokeStyles(): List<StraightStrokeStyle> =
        PredefinedStraightStrokeStyle.PREDEFINED_STYLES

    fun getStartHeadAnchorChar(
        id: String?,
        default: AnchorChar = defaultLineExtra.userSelectedStartAnchor
    ): AnchorChar = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHAR_MAP[id] ?: default

    fun getEndHeadAnchorChar(
        id: String?,
        default: AnchorChar = defaultLineExtra.userSelectedEndAnchor
    ): AnchorChar = PredefinedAnchorChar.PREDEFINED_ANCHOR_CHAR_MAP[id] ?: default

    fun getAllPredefinedAnchorChars(): List<AnchorChar> =
        PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS
}
