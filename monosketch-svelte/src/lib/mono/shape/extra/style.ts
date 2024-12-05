import type { Char } from "$libs/char";
import type { Comparable } from "$libs/comparable";
import type { Drawable } from "$mono/monobitmap/drawable";

/**
 * A class for defining an anchor end-char.
 *
 * @param id is the key for retrieving predefined [AnchorChar] when serialization.
 * @param displayName is the text visible on the UI tool for selection.
 */
export class AnchorChar {
    constructor(
        public readonly id: string,
        public readonly displayName: string,
        public readonly left: Char,
        public readonly right: Char,
        public readonly top: Char,
        public readonly bottom: Char,
    ) {
    }

    // Constructor with the same character for all sides
    static fromSingle(id: string, displayName: string, all: Char): AnchorChar {
        return new AnchorChar(id, displayName, all, all, all, all);
    }
}

/**
 * A class for rectangle border corner pattern.
 */
export class RectangleBorderCornerPattern {

    constructor(
        public readonly leftTopRounded: boolean,
        public readonly leftBottomRounded: boolean,
        public readonly rightTopRounded: boolean,
        public readonly rightBottomRounded: boolean,
    ) {
    }

    toSerializableValue(): string {
        return [
            this.leftTopRounded,
            this.leftBottomRounded,
            this.rightTopRounded,
            this.rightBottomRounded,
        ]
            .map(flag => flag ? "Y" : "N")
            .join("");
    }

    static DISABLED: RectangleBorderCornerPattern =
        new RectangleBorderCornerPattern(false, false, false, false);
    static ENABLED: RectangleBorderCornerPattern =
        new RectangleBorderCornerPattern(true, true, true, true);

    static fromSerializableValue(value: string): RectangleBorderCornerPattern {
        return new RectangleBorderCornerPattern(
            value.charAt(0) === 'Y',
            value.charAt(1) === 'Y',
            value.charAt(2) === 'Y',
            value.charAt(3) === 'Y',
        );
    }
}

/**
 * A class for defining a border style for rectangle.
 *
 * @param id is the key for retrieving predefined [RectangleBorderStyle] when serialization.
 * @param displayName is the text visible on the UI tool for selection.
 */
export class RectangleBorderStyle {
    constructor(
        public readonly id: string,
        public readonly displayName: string,
        public readonly drawable: Drawable,
    ) {
    }
}

/**
 * A class for defining a fill style for rectangle.
 *
 * @param id is the key for retrieving predefined [RectangleFillStyle] when serialization.
 * @param displayName is the text visible on the UI tool for selection.
 */
export class RectangleFillStyle {
    constructor(
        public readonly id: string,
        public readonly displayName: string,
        public readonly drawable: Drawable,
    ) {
    }
}

/**
 * A class to define dash pattern of straight stroke.
 * @param dash is the solid of dash, min value is 1
 */
export class StraightStrokeDashPattern {
    private readonly adjustedSegment: number;
    private readonly adjustedGap: number;
    private readonly totalLength: number;
    private readonly adjustedOffset: number;

    constructor(
        public readonly dash: number,
        public readonly gap: number,
        public readonly offset: number,
    ) {
        this.adjustedSegment = Math.max(this.dash, 1);
        this.adjustedGap = Math.max(this.gap, 0);
        this.totalLength = this.adjustedSegment + this.adjustedGap;

        // Adjust offset to be in [0, length). Calculation for `isGap` does not work well with
        // negative number
        this.adjustedOffset = ((this.offset % this.totalLength) + this.totalLength) % this.totalLength;
    }

    isGap(index: number): boolean {
        return this.adjustedGap !== 0
            ? (index + this.adjustedOffset) % this.totalLength >= this.adjustedSegment
            : false;
    }

    toSerializableValue(): string {
        return `${this.dash}|${this.gap}|${this.offset}`;
    }

    static readonly SOLID = new StraightStrokeDashPattern(1, 0, 0);

    static fromSerializableValue(value: string): StraightStrokeDashPattern {
        const [dashStr, gapStr, offsetStr] = value.split('|');
        const dash = parseInt(dashStr, 10) || 1;
        const gap = parseInt(gapStr, 10) || 0;
        const offset = parseInt(offsetStr, 10) || 0;
        return new StraightStrokeDashPattern(dash, gap, offset);
    }
}

/**
 * A class for defining a stroke style of straight line.
 */
export class StraightStrokeStyle {
    constructor(
        public readonly id: string,
        public readonly displayName: string,
        public readonly horizontal: Char,
        public readonly vertical: Char,
        public readonly downLeft: Char,
        public readonly upRight: Char,
        public readonly upLeft: Char,
        public readonly downRight: Char,
    ) {
    }
}

export enum TextHorizontalAlign {
    LEFT,
    MIDDLE,
    RIGHT
}

export enum TextVerticalAlign {
    TOP,
    MIDDLE,
    BOTTOM
}

/**
 * A model for defining text aligns.
 */
export class TextAlign implements Comparable {
    constructor(
        public readonly horizontalAlign: TextHorizontalAlign,
        public readonly verticalAlign: TextVerticalAlign,
    ) {
    }

    private static ALL_HORIZONTAL_ALIGNS: TextHorizontalAlign[] = [
        TextHorizontalAlign.LEFT,
        TextHorizontalAlign.MIDDLE,
        TextHorizontalAlign.RIGHT,
    ];

    private static ALL_VERTICAL_ALIGNS: TextVerticalAlign[] = [
        TextVerticalAlign.TOP,
        TextVerticalAlign.MIDDLE,
        TextVerticalAlign.BOTTOM,
    ];

    // Additional constructor that takes indexes for horizontal and vertical aligns
    static from(textHorizontalAlign: number, textVerticalAlign: number): TextAlign {
        const horizontalAlign = this.ALL_HORIZONTAL_ALIGNS[textHorizontalAlign];
        const verticalAlign = this.ALL_VERTICAL_ALIGNS[textVerticalAlign];
        return new TextAlign(horizontalAlign, verticalAlign);
    }

    equals(other: unknown): boolean {
        if (!(other instanceof TextAlign)) {
            return false;
        }
        return (
            this.horizontalAlign === other.horizontalAlign &&
            this.verticalAlign === other.verticalAlign
        );
    }

    copy({
             horizontalAlign = this.horizontalAlign,
             verticalAlign = this.verticalAlign,
         }: Partial<TextAlign> = {}): TextAlign {
        return new TextAlign(horizontalAlign, verticalAlign);
    }
}
