import type { Comparable } from "$libs/comparable";
import { Flow } from "$libs/flow";
import {
    PredefinedAnchorChar,
    PredefinedRectangleFillStyle,
    PredefinedStraightStrokeStyle,
} from "$mono/shape/extra/predefined-styles";
import {
    AnchorChar,
    RectangleBorderCornerPattern,
    RectangleFillStyle,
    StraightStrokeDashPattern,
    StraightStrokeStyle,
    TextAlign, TextHorizontalAlign, TextVerticalAlign,
} from "$mono/shape/extra/style";
import {
    SerializableLineExtra,
    type SerializableRectExtra,
    SerializableTextExtra,
} from "$mono/shape/serialization/serializable-shape";

/**
 * An interface for extra properties of a shape.
 */
export interface ShapeExtra extends Comparable {
}

export const NoExtra: ShapeExtra = {
    equals(other: unknown): boolean {
        return other === NoExtra;
    },
};

/**
 * A {@link ShapeExtra} for a line.
 */
export class LineExtra implements ShapeExtra {
    constructor(
        public isStrokeEnabled: boolean,
        public userSelectedStrokeStyle: StraightStrokeStyle,
        public isStartAnchorEnabled: boolean,
        public userSelectedStartAnchor: AnchorChar,
        public isEndAnchorEnabled: boolean,
        public userSelectedEndAnchor: AnchorChar,
        public dashPattern: StraightStrokeDashPattern,
        public isRoundedCorner: boolean,
    ) {
    }

    get startAnchor(): AnchorChar | null {
        return this.isStartAnchorEnabled ? this.userSelectedStartAnchor : null;
    }

    get endAnchor(): AnchorChar | null {
        return this.isEndAnchorEnabled ? this.userSelectedEndAnchor : null;
    }

    get strokeStyle(): StraightStrokeStyle | null {
        return this.isStrokeEnabled
            ? PredefinedStraightStrokeStyle.getStyle(this.userSelectedStrokeStyle.id, this.isRoundedCorner)
            : null;
    }

    equals(other: unknown): boolean {
        if (!(other instanceof LineExtra)) {
            return false;
        }
        return (
            this.isStrokeEnabled === other.isStrokeEnabled &&
            this.userSelectedStrokeStyle.id === other.userSelectedStrokeStyle.id &&
            this.isStartAnchorEnabled === other.isStartAnchorEnabled &&
            this.userSelectedStartAnchor.id === other.userSelectedStartAnchor.id &&
            this.isEndAnchorEnabled === other.isEndAnchorEnabled &&
            this.userSelectedEndAnchor.id === other.userSelectedEndAnchor.id &&
            this.dashPattern.toSerializableValue() === other.dashPattern.toSerializableValue() &&
            this.isRoundedCorner === other.isRoundedCorner
        );
    }

    copy({
             isStrokeEnabled = this.isStrokeEnabled,
             userSelectedStrokeStyle = this.userSelectedStrokeStyle,
             isStartAnchorEnabled = this.isStartAnchorEnabled,
             userSelectedStartAnchor = this.userSelectedStartAnchor,
             isEndAnchorEnabled = this.isEndAnchorEnabled,
             userSelectedEndAnchor = this.userSelectedEndAnchor,
             dashPattern = this.dashPattern,
             isRoundedCorner = this.isRoundedCorner,
         }: Partial<LineExtra> = {}): LineExtra {
        return new LineExtra(
            isStrokeEnabled,
            userSelectedStrokeStyle,
            isStartAnchorEnabled,
            userSelectedStartAnchor,
            isEndAnchorEnabled,
            userSelectedEndAnchor,
            dashPattern,
            isRoundedCorner,
        );
    }

    static fromSerializable(serializableExtra: SerializableLineExtra): LineExtra {
        return new LineExtra(
            serializableExtra.isStrokeEnabled,
            ShapeExtraManager.getLineStrokeStyle(serializableExtra.userSelectedStrokeStyleId),
            serializableExtra.isStartAnchorEnabled,
            ShapeExtraManager.getStartHeadAnchorChar(serializableExtra.userSelectedStartAnchorId),
            serializableExtra.isEndAnchorEnabled,
            ShapeExtraManager.getEndHeadAnchorChar(serializableExtra.userSelectedEndAnchorId),
            StraightStrokeDashPattern.fromSerializableValue(serializableExtra.dashPattern),
            serializableExtra.isRoundedCorner,
        );
    }

    toSerializableExtra(): SerializableLineExtra {
        return {
            isStrokeEnabled: this.isStrokeEnabled,
            userSelectedStrokeStyleId: this.userSelectedStrokeStyle.id,
            isStartAnchorEnabled: this.isStartAnchorEnabled,
            userSelectedStartAnchorId: this.userSelectedStartAnchor.id,
            isEndAnchorEnabled: this.isEndAnchorEnabled,
            userSelectedEndAnchorId: this.userSelectedEndAnchor.id,
            dashPattern: this.dashPattern.toSerializableValue(),
            isRoundedCorner: this.isRoundedCorner,
        };
    }
}

/**
 * A {@link ShapeExtra} for a rectangle box.
 */
export class RectangleExtra implements ShapeExtra {
    constructor(
        public isFillEnabled: boolean,
        public userSelectedFillStyle: RectangleFillStyle,
        public isBorderEnabled: boolean,
        public userSelectedBorderStyle: StraightStrokeStyle,
        public dashPattern: StraightStrokeDashPattern,
        public corner: RectangleBorderCornerPattern,
    ) {
    }

    static create(
        isFillEnabled: boolean,
        userSelectedFillStyle: RectangleFillStyle,
        isBorderEnabled: boolean,
        userSelectedBorderStyle: StraightStrokeStyle,
        dashPattern: StraightStrokeDashPattern,
        isRoundedCorner: boolean,
    ): RectangleExtra {
        return new RectangleExtra(
            isFillEnabled,
            userSelectedFillStyle,
            isBorderEnabled,
            userSelectedBorderStyle,
            dashPattern,
            isRoundedCorner ? RectangleBorderCornerPattern.ENABLED : RectangleBorderCornerPattern.DISABLED,
        );
    }

    get isRoundedCorner(): boolean {
        return this.corner === RectangleBorderCornerPattern.ENABLED;
    }

    get fillStyle(): RectangleFillStyle | null {
        return this.isFillEnabled ? this.userSelectedFillStyle : null;
    }

    get strokeStyle(): StraightStrokeStyle | null {
        return this.isBorderEnabled
            ? PredefinedStraightStrokeStyle.getStyle(this.userSelectedBorderStyle.id, this.isRoundedCorner)
            : null;
    }

    equals(other: unknown): boolean {
        if (!(other instanceof RectangleExtra)) {
            return false;
        }
        return (
            this.isFillEnabled === other.isFillEnabled &&
            this.userSelectedFillStyle.id === other.userSelectedFillStyle.id &&
            this.isBorderEnabled === other.isBorderEnabled &&
            this.userSelectedBorderStyle.id === other.userSelectedBorderStyle.id &&
            this.dashPattern.toSerializableValue() === other.dashPattern.toSerializableValue() &&
            this.corner.toSerializableValue() === other.corner.toSerializableValue()
        );
    }

    copy({
             isFillEnabled = this.isFillEnabled,
             userSelectedFillStyle = this.userSelectedFillStyle,
             isBorderEnabled = this.isBorderEnabled,
             userSelectedBorderStyle = this.userSelectedBorderStyle,
             dashPattern = this.dashPattern,
             corner = this.corner,
         }: Partial<RectangleExtra> = {}): RectangleExtra {
        return new RectangleExtra(
            isFillEnabled,
            userSelectedFillStyle,
            isBorderEnabled,
            userSelectedBorderStyle,
            dashPattern,
            corner,
        );
    }

    toSerializableExtra(): SerializableRectExtra {
        return {
            isFillEnabled: this.isFillEnabled,
            userSelectedFillStyleId: this.userSelectedFillStyle.id,
            isBorderEnabled: this.isBorderEnabled,
            userSelectedBorderStyleId: this.userSelectedBorderStyle.id,
            dashPattern: this.dashPattern.toSerializableValue(),
            corner: this.corner.toSerializableValue(),
        };
    }

    static fromSerializable(serializableExtra: SerializableRectExtra): RectangleExtra {
        return new RectangleExtra(
            serializableExtra.isFillEnabled,
            ShapeExtraManager.getRectangleFillStyle(serializableExtra.userSelectedFillStyleId),
            serializableExtra.isBorderEnabled,
            ShapeExtraManager.getRectangleBorderStyle(serializableExtra.userSelectedBorderStyleId),
            StraightStrokeDashPattern.fromSerializableValue(serializableExtra.dashPattern),
            RectangleBorderCornerPattern.fromSerializableValue(serializableExtra.corner),
        );
    }
}

/**
 * A {@link ShapeExtra} for a text.
 */
export class TextExtra implements ShapeExtra {
    constructor(
        public boundExtra: RectangleExtra,
        public textAlign: TextAlign,
    ) {
    }

    hasBorder(): boolean {
        return this.boundExtra.isBorderEnabled;
    }

    equals(other: unknown): boolean {
        if (!(other instanceof TextExtra)) {
            return false;
        }
        return (
            this.boundExtra.equals(other.boundExtra) &&
            this.textAlign.equals(other.textAlign)
        );
    }

    copy({
             boundExtra = this.boundExtra,
             textAlign = this.textAlign,
         }: Partial<TextExtra> = {}): TextExtra {
        return new TextExtra(
            boundExtra,
            textAlign,
        );
    }

    toSerializableExtra(): SerializableTextExtra {
        return {
            boundExtra: this.boundExtra.toSerializableExtra(),
            textHorizontalAlign: this.textAlign.horizontalAlign,
            textVerticalAlign: this.textAlign.verticalAlign,
        };
    }

    static fromSerializable(serializableExtra: SerializableTextExtra): TextExtra {
        return new TextExtra(
            RectangleExtra.fromSerializable(serializableExtra.boundExtra),
            TextAlign.from(serializableExtra.textHorizontalAlign, serializableExtra.textVerticalAlign),
        );
    }

    static noBound(): TextExtra {
        return new TextExtra(
            ShapeExtraManager.defaultRectangleExtra.copy({
                isFillEnabled: false,
                isBorderEnabled: false,
            }),
            new TextAlign(TextHorizontalAlign.LEFT, TextVerticalAlign.TOP),
        );
    }

    static withDefault(): TextExtra {
        return new TextExtra(
            ShapeExtraManager.defaultRectangleExtra,
            ShapeExtraManager.defaultTextAlign,
        );
    }
}

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
        {
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
        }): void {
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
