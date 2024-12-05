/*
 * Copyright (c) 2024, tuanchauict
 */


import { Flow } from "$libs/flow";
import {
    PredefinedAnchorChar,
    PredefinedRectangleFillStyle,
    PredefinedStraightStrokeStyle,
} from "$mono/shape/extra/predefined-styles";
import { LineExtra, RectangleExtra } from "$mono/shape/extra/shape-extra";
import {
    type AnchorChar, RectangleFillStyle,
    StraightStrokeDashPattern, StraightStrokeStyle,
    TextAlign,
    TextHorizontalAlign,
    TextVerticalAlign,
} from "$mono/shape/extra/style";

/**
 * A manager for shape extras.
 */
export class ShapeExtraManager {
    static defaultRectangleExtra: RectangleExtra = RectangleExtra.create(
        false,
        PredefinedRectangleFillStyle.PREDEFINED_STYLES[0],
        true,
        PredefinedStraightStrokeStyle.PREDEFINED_STYLES[0],
        StraightStrokeDashPattern.SOLID,
        false,
    );

    static defaultLineExtra: LineExtra = new LineExtra(
        false,
        PredefinedStraightStrokeStyle.PREDEFINED_STYLES[0],
        false,
        PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0],
        false,
        PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS[0],
        StraightStrokeDashPattern.SOLID,
        false,
    );

    static defaultTextAlign: TextAlign = new TextAlign(
        TextHorizontalAlign.MIDDLE,
        TextVerticalAlign.MIDDLE,
    );

    private static defaultExtraStateUpdateMutableFlow = new Flow<void>();
    static defaultExtraStateUpdateFlow: Flow<void> = ShapeExtraManager.defaultExtraStateUpdateMutableFlow.immutable();


    static setDefaultValues(
        isFillEnabled?: boolean,
        fillStyleId?: string,
        isBorderEnabled?: boolean,
        borderStyleId?: string,
        isBorderRoundedCorner?: boolean,
        borderDashPattern?: StraightStrokeDashPattern,
        isLineStrokeEnabled?: boolean,
        lineStrokeStyleId?: string,
        isLineStrokeRoundedCorner?: boolean,
        lineDashPattern?: StraightStrokeDashPattern,
        isStartHeadAnchorCharEnabled?: boolean,
        startHeadAnchorCharId?: string,
        isEndHeadAnchorCharEnabled?: boolean,
        endHeadAnchorCharId?: string,
        textHorizontalAlign?: TextHorizontalAlign,
        textVerticalAlign?: TextVerticalAlign,
    ): void {
        ShapeExtraManager.defaultRectangleExtra = RectangleExtra.create(
            isFillEnabled ?? ShapeExtraManager.defaultRectangleExtra.isFillEnabled,
            ShapeExtraManager.getRectangleFillStyle(fillStyleId),
            isBorderEnabled ?? ShapeExtraManager.defaultRectangleExtra.isBorderEnabled,
            ShapeExtraManager.getRectangleBorderStyle(borderStyleId),
            borderDashPattern ?? ShapeExtraManager.defaultRectangleExtra.dashPattern,
            isBorderRoundedCorner ?? ShapeExtraManager.defaultRectangleExtra.isRoundedCorner,
        );

        ShapeExtraManager.defaultLineExtra = new LineExtra(
            isLineStrokeEnabled ?? ShapeExtraManager.defaultLineExtra.isStrokeEnabled,
            ShapeExtraManager.getLineStrokeStyle(lineStrokeStyleId),
            isStartHeadAnchorCharEnabled ?? ShapeExtraManager.defaultLineExtra.isStartAnchorEnabled,
            ShapeExtraManager.getStartHeadAnchorChar(startHeadAnchorCharId),
            isEndHeadAnchorCharEnabled ?? ShapeExtraManager.defaultLineExtra.isEndAnchorEnabled,
            ShapeExtraManager.getEndHeadAnchorChar(endHeadAnchorCharId),
            lineDashPattern ?? StraightStrokeDashPattern.SOLID,
            isLineStrokeRoundedCorner ?? ShapeExtraManager.defaultLineExtra.isRoundedCorner,
        );

        ShapeExtraManager.defaultTextAlign = new TextAlign(
            textHorizontalAlign ?? ShapeExtraManager.defaultTextAlign.horizontalAlign,
            textVerticalAlign ?? ShapeExtraManager.defaultTextAlign.verticalAlign,
        );

        ShapeExtraManager.defaultExtraStateUpdateMutableFlow.value = undefined;
    }

    static getRectangleFillStyle(
        id?: string,
        defaultStyle: RectangleFillStyle = ShapeExtraManager.defaultRectangleExtra.userSelectedFillStyle,
    ): RectangleFillStyle {
        if (id === undefined) {
            return defaultStyle;
        }
        return PredefinedRectangleFillStyle.PREDEFINED_STYLE_MAP[id] ?? defaultStyle;
    }

    static getAllPredefinedRectangleFillStyles(): RectangleFillStyle[] {
        return PredefinedRectangleFillStyle.PREDEFINED_STYLES;
    }

    static getRectangleBorderStyle(
        id?: string,
        defaultStyle: StraightStrokeStyle = ShapeExtraManager.defaultRectangleExtra.userSelectedBorderStyle,
    ): StraightStrokeStyle {
        if (id === undefined) {
            return defaultStyle;
        }
        return PredefinedStraightStrokeStyle.getStyle(id) ?? defaultStyle;
    }

    static getLineStrokeStyle(
        id?: string,
        defaultStyle: StraightStrokeStyle = ShapeExtraManager.defaultLineExtra.userSelectedStrokeStyle,
    ): StraightStrokeStyle {
        if (id === undefined) {
            return defaultStyle;
        }
        return PredefinedStraightStrokeStyle.getStyle(id) ?? defaultStyle;
    }

    static getAllPredefinedStrokeStyles(): StraightStrokeStyle[] {
        return PredefinedStraightStrokeStyle.PREDEFINED_STYLES;
    }

    static getStartHeadAnchorChar(
        id?: string,
        defaultChar: AnchorChar = ShapeExtraManager.defaultLineExtra.userSelectedStartAnchor,
    ): AnchorChar {
        if (id === undefined) {
            return defaultChar;
        }
        return PredefinedAnchorChar.PREDEFINED_ANCHOR_CHAR_MAP[id] ?? defaultChar;
    }

    static getEndHeadAnchorChar(
        id?: string,
        defaultChar: AnchorChar = ShapeExtraManager.defaultLineExtra.userSelectedEndAnchor,
    ): AnchorChar {
        if (id === undefined) {
            return defaultChar;
        }
        return PredefinedAnchorChar.PREDEFINED_ANCHOR_CHAR_MAP[id] ?? defaultChar;
    }

    static getAllPredefinedAnchorChars(): AnchorChar[] {
        return PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS;
    }
}