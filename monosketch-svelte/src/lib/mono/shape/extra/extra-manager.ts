/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import {
    PredefinedAnchorChar,
    PredefinedRectangleFillStyle,
    PredefinedStraightStrokeStyle,
} from "$mono/shape/extra/predefined-styles";
import {
    AnchorChar, RectangleBorderCornerPattern,
    RectangleFillStyle,
    StraightStrokeDashPattern, StraightStrokeStyle,
    TextAlign,
    TextHorizontalAlign,
    TextVerticalAlign,
} from "$mono/shape/extra/style";

export interface ILineExtra {
    isStrokeEnabled: boolean;
    userSelectedStrokeStyle: StraightStrokeStyle;
    isStartAnchorEnabled: boolean;
    userSelectedStartAnchor: AnchorChar;
    isEndAnchorEnabled: boolean;
    userSelectedEndAnchor: AnchorChar;
    dashPattern: StraightStrokeDashPattern;
    isRoundedCorner: boolean;
}

export interface IRectangleExtra {
    isFillEnabled: boolean;
    userSelectedFillStyle: RectangleFillStyle;
    isBorderEnabled: boolean;
    userSelectedBorderStyle: StraightStrokeStyle;
    dashPattern: StraightStrokeDashPattern;
    corner: RectangleBorderCornerPattern;
}

interface IShapeExtraManager {
    defaultRectangleExtra: IRectangleExtra;
    defaultLineExtra: ILineExtra;
    defaultTextAlign: TextAlign;
    defaultExtraStateUpdateFlow: Flow<void>;

    setDefaultValues(params: {
        isFillEnabled?: boolean;
        fillStyleId?: string;
        isBorderEnabled?: boolean;
        borderStyleId?: string;
        isBorderRoundedCorner?: boolean;
        borderDashPattern?: StraightStrokeDashPattern;
        isLineStrokeEnabled?: boolean;
        lineStrokeStyleId?: string;
        isLineStrokeRoundedCorner?: boolean;
        lineDashPattern?: StraightStrokeDashPattern;
        isStartHeadAnchorCharEnabled?: boolean;
        startHeadAnchorCharId?: string;
        isEndHeadAnchorCharEnabled?: boolean;
        endHeadAnchorCharId?: string;
        textHorizontalAlign?: TextHorizontalAlign;
        textVerticalAlign?: TextVerticalAlign;
    }): void;

    getRectangleFillStyle(id?: string, defaultStyle?: RectangleFillStyle): RectangleFillStyle;

    getAllPredefinedRectangleFillStyles(): RectangleFillStyle[];

    getRectangleBorderStyle(id?: string, defaultStyle?: StraightStrokeStyle): StraightStrokeStyle;

    getLineStrokeStyle(id?: string, defaultStyle?: StraightStrokeStyle): StraightStrokeStyle;

    getAllPredefinedStrokeStyles(): StraightStrokeStyle[];

    getStartHeadAnchorChar(id?: string, defaultChar?: AnchorChar): AnchorChar;

    getEndHeadAnchorChar(id?: string, defaultChar?: AnchorChar): AnchorChar;

    getAllPredefinedAnchorChars(): AnchorChar[];
}

class ShapeExtraManagerImpl implements IShapeExtraManager {
    defaultRectangleExtra: IRectangleExtra = {
        isFillEnabled: false,
        userSelectedFillStyle: PredefinedRectangleFillStyle.PREDEFINED_STYLES[0],
        isBorderEnabled: true,
        userSelectedBorderStyle: PredefinedStraightStrokeStyle.PREDEFINED_STYLES[0],
        dashPattern: StraightStrokeDashPattern.SOLID,
        corner: RectangleBorderCornerPattern.DISABLED,
    };

    defaultLineExtra: ILineExtra = {
        isStrokeEnabled: false,
        userSelectedStrokeStyle: PredefinedStraightStrokeStyle.PREDEFINED_STYLES[0],
        isStartAnchorEnabled: false,
        userSelectedStartAnchor: PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0],
        isEndAnchorEnabled: false,
        userSelectedEndAnchor: PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0],
        dashPattern: StraightStrokeDashPattern.SOLID,
        isRoundedCorner: false,
    };

    defaultTextAlign: TextAlign = new TextAlign(
        TextHorizontalAlign.MIDDLE,
        TextVerticalAlign.MIDDLE,
    );

    private defaultExtraStateUpdateMutableFlow = new Flow<void>();
    defaultExtraStateUpdateFlow: Flow<void> = this.defaultExtraStateUpdateMutableFlow.immutable();

    setDefaultValues({
        isFillEnabled,
        fillStyleId,
        isBorderEnabled,
        borderStyleId,
        isBorderRoundedCorner,
        borderDashPattern,
        isLineStrokeEnabled,
        lineStrokeStyleId,
        isLineStrokeRoundedCorner,
        lineDashPattern,
        isStartHeadAnchorCharEnabled,
        startHeadAnchorCharId,
        isEndHeadAnchorCharEnabled,
        endHeadAnchorCharId,
        textHorizontalAlign,
        textVerticalAlign,
    }: {
        isFillEnabled?: boolean;
        fillStyleId?: string;
        isBorderEnabled?: boolean;
        borderStyleId?: string;
        isBorderRoundedCorner?: boolean;
        borderDashPattern?: StraightStrokeDashPattern;
        isLineStrokeEnabled?: boolean;
        lineStrokeStyleId?: string;
        isLineStrokeRoundedCorner?: boolean;
        lineDashPattern?: StraightStrokeDashPattern;
        isStartHeadAnchorCharEnabled?: boolean;
        startHeadAnchorCharId?: string;
        isEndHeadAnchorCharEnabled?: boolean;
        endHeadAnchorCharId?: string;
        textHorizontalAlign?: TextHorizontalAlign;
        textVerticalAlign?: TextVerticalAlign;
    }): void {
        const borderCorner =
            isBorderRoundedCorner === undefined
                ? this.defaultRectangleExtra.corner
                : (isBorderRoundedCorner ? RectangleBorderCornerPattern.ENABLED : RectangleBorderCornerPattern.DISABLED);
        this.defaultRectangleExtra = {
            isFillEnabled: isFillEnabled ?? this.defaultRectangleExtra.isFillEnabled,
            userSelectedFillStyle: this.getRectangleFillStyle(fillStyleId),
            isBorderEnabled: isBorderEnabled ?? this.defaultRectangleExtra.isBorderEnabled,
            userSelectedBorderStyle: this.getRectangleBorderStyle(borderStyleId),
            dashPattern: borderDashPattern ?? this.defaultRectangleExtra.dashPattern,
            corner: borderCorner,
        };

        this.defaultLineExtra = {
            isStrokeEnabled: isLineStrokeEnabled ?? this.defaultLineExtra.isStrokeEnabled,
            userSelectedStrokeStyle: this.getLineStrokeStyle(lineStrokeStyleId),
            isStartAnchorEnabled: isStartHeadAnchorCharEnabled ?? this.defaultLineExtra.isStartAnchorEnabled,
            userSelectedStartAnchor: this.getStartHeadAnchorChar(startHeadAnchorCharId),
            isEndAnchorEnabled: isEndHeadAnchorCharEnabled ?? this.defaultLineExtra.isEndAnchorEnabled,
            userSelectedEndAnchor: this.getEndHeadAnchorChar(endHeadAnchorCharId),
            dashPattern: lineDashPattern ?? StraightStrokeDashPattern.SOLID,
            isRoundedCorner: isLineStrokeRoundedCorner ?? this.defaultLineExtra.isRoundedCorner,
        };

        this.defaultTextAlign = new TextAlign(
            textHorizontalAlign ?? this.defaultTextAlign.horizontalAlign,
            textVerticalAlign ?? this.defaultTextAlign.verticalAlign,
        );

        this.defaultExtraStateUpdateMutableFlow.value = undefined;
    }

    getRectangleFillStyle(
        id?: string,
        defaultStyle: RectangleFillStyle = this.defaultRectangleExtra.userSelectedFillStyle,
    ): RectangleFillStyle {
        if (id === undefined) {
            return defaultStyle;
        }
        return PredefinedRectangleFillStyle.PREDEFINED_STYLE_MAP[id] ?? defaultStyle;
    }

    getAllPredefinedRectangleFillStyles(): RectangleFillStyle[] {
        return PredefinedRectangleFillStyle.PREDEFINED_STYLES;
    }

    getRectangleBorderStyle(
        id?: string,
        defaultStyle: StraightStrokeStyle = this.defaultRectangleExtra.userSelectedBorderStyle,
    ): StraightStrokeStyle {
        if (id === undefined) {
            return defaultStyle;
        }
        return PredefinedStraightStrokeStyle.getStyle(id) ?? defaultStyle;
    }

    getLineStrokeStyle(
        id?: string,
        defaultStyle: StraightStrokeStyle = this.defaultLineExtra.userSelectedStrokeStyle,
    ): StraightStrokeStyle {
        if (id === undefined) {
            return defaultStyle;
        }
        return PredefinedStraightStrokeStyle.getStyle(id) ?? defaultStyle;
    }

    getAllPredefinedStrokeStyles(): StraightStrokeStyle[] {
        return PredefinedStraightStrokeStyle.PREDEFINED_STYLES;
    }

    getStartHeadAnchorChar(
        id?: string,
        defaultChar: AnchorChar = this.defaultLineExtra.userSelectedStartAnchor,
    ): AnchorChar {
        if (id === undefined) {
            return defaultChar;
        }
        return PredefinedAnchorChar.PREDEFINED_ANCHOR_CHAR_MAP[id] ?? defaultChar;
    }

    getEndHeadAnchorChar(
        id?: string,
        defaultChar: AnchorChar = this.defaultLineExtra.userSelectedEndAnchor,
    ): AnchorChar {
        if (id === undefined) {
            return defaultChar;
        }
        return PredefinedAnchorChar.PREDEFINED_ANCHOR_CHAR_MAP[id] ?? defaultChar;
    }

    getAllPredefinedAnchorChars(): AnchorChar[] {
        return PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS;
    }
}

export const ShapeExtraManager = new ShapeExtraManagerImpl();
