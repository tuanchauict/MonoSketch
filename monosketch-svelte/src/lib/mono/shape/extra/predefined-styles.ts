/*
 * Copyright (c) 2024, tuanchauict
 */


import { HALF_TRANSPARENT_CHAR, NBSP, TRANSPARENT_CHAR } from "$mono/common/character";
import {
    CharDrawable,
    NinePatchDrawable,
    NinePatchDrawablePattern,
    RepeatRepeatableRange,
} from "$mono/monobitmap/drawable";
import { AnchorChar, RectangleBorderStyle, RectangleFillStyle, StraightStrokeStyle } from "$mono/shape/extra/style";

/**
 * An object for listing all predefined rectangle fill styles.
 */
export class PredefinedRectangleFillStyle {
    static readonly NOFILLED_STYLE = new RectangleFillStyle(
        "F0",
        "No Fill",
        new CharDrawable(TRANSPARENT_CHAR),
    );

    static readonly PREDEFINED_STYLES: RectangleFillStyle[] = [
        RectangleFillStyle.create({ id: "F1", displayName: NBSP, drawable: new CharDrawable(' ') }),
        RectangleFillStyle.create({ id: "F2", displayName: "█", drawable: new CharDrawable('█') }),
        RectangleFillStyle.create({ id: "F3", displayName: "▒", drawable: new CharDrawable('▒') }),
        RectangleFillStyle.create({ id: "F4", displayName: "░", drawable: new CharDrawable('░') }),
        RectangleFillStyle.create({ id: "F5", displayName: "▚", drawable: new CharDrawable('▚') }),
    ];

    static readonly PREDEFINED_STYLE_MAP: { [id: string]: RectangleFillStyle } = Object.fromEntries(
        PredefinedRectangleFillStyle.PREDEFINED_STYLES.map(style => [style.id, style]),
    );
}

/**
 * An object for listing all predefined StraightStrokeStyle
 */
export class PredefinedStraightStrokeStyle {
    static readonly NO_STROKE = new StraightStrokeStyle(
        "S0",
        "No Stroke",
        HALF_TRANSPARENT_CHAR,
        HALF_TRANSPARENT_CHAR,
        HALF_TRANSPARENT_CHAR,
        HALF_TRANSPARENT_CHAR,
        HALF_TRANSPARENT_CHAR,
        HALF_TRANSPARENT_CHAR,
    );

    private static readonly ALL_STYLES: StraightStrokeStyle[] = [
        PredefinedStraightStrokeStyle.NO_STROKE,
        StraightStrokeStyle.create({
            id: "S1",
            displayName: "─",
            horizontal: '─',
            vertical: '│',
            downLeft: '┐',
            upRight: '┌',
            upLeft: '┘',
            downRight: '└',
        }),
        StraightStrokeStyle.create({
            id: "S2",
            displayName: "━",
            horizontal: '━',
            vertical: '┃',
            downLeft: '┓',
            upRight: '┏',
            upLeft: '┛',
            downRight: '┗',
        }),
        StraightStrokeStyle.create({
            id: "S3",
            displayName: "═",
            horizontal: '═',
            vertical: '║',
            downLeft: '╗',
            upRight: '╔',
            upLeft: '╝',
            downRight: '╚',
        }),
        StraightStrokeStyle.create({
            id: "S4",
            displayName: "▢",
            horizontal: '─',
            vertical: '│',
            downLeft: '╮',
            upRight: '╭',
            upLeft: '╯',
            downRight: '╰',
        }),
    ];

    private static readonly ID_TO_STYLE_MAP: { [id: string]: StraightStrokeStyle } = Object.fromEntries(
        PredefinedStraightStrokeStyle.ALL_STYLES.map(style => [style.id, style]),
    );

    private static readonly STYLE_TO_ROUNDED_CORNER_STYLE_MAP: { [id: string]: string } = {
        "S1": "S4",
    };

    static readonly PREDEFINED_STYLES: StraightStrokeStyle[] = ["S1", "S2", "S3"].map(id => PredefinedStraightStrokeStyle.ID_TO_STYLE_MAP[id]);

    static getStyle(id: string, isRounded: boolean = false): StraightStrokeStyle | null {
        const adjustedId = isRounded ? PredefinedStraightStrokeStyle.STYLE_TO_ROUNDED_CORNER_STYLE_MAP[id] || id : id;
        return PredefinedStraightStrokeStyle.ID_TO_STYLE_MAP[adjustedId];
    }

    static isCornerRoundable(id: string | null | undefined): boolean {
        if (id === null || id === undefined) {
            return false;
        }
        return id in PredefinedStraightStrokeStyle.STYLE_TO_ROUNDED_CORNER_STYLE_MAP;
    }
}

/**
 * An object for listing all predefined anchor chars.
 */
export class PredefinedAnchorChar {
    static readonly PREDEFINED_ANCHOR_CHARS: AnchorChar[] = [
        AnchorChar.create({ id: "A1", displayName: "▶", left: '◀', right: '▶', top: '▲', bottom: '▼' }),
        AnchorChar.create({ id: "A12", displayName: "▷", left: '◁', right: '▷', top: '△', bottom: '▽' }),
        AnchorChar.create({ id: "A13", displayName: "►", left: '◄', right: '►', top: '▲', bottom: '▼' }),
        AnchorChar.create({ id: "A14", displayName: "▻", left: '◅', right: '▻', top: '△', bottom: '▽' }),
        AnchorChar.create({ id: "A2", displayName: "■", all: '■' }),
        AnchorChar.create({ id: "A21", displayName: "□", all: '□' }),
        AnchorChar.create({ id: "A220", displayName: "◆", all: '◆' }),
        AnchorChar.create({ id: "A221", displayName: "◇", all: '◇' }),
        AnchorChar.create({ id: "A3", displayName: "○", all: '○' }),
        AnchorChar.create({ id: "A4", displayName: "◎", all: '◎' }),
        AnchorChar.create({ id: "A5", displayName: "●", all: '●' }),
        AnchorChar.create({ id: "A6", displayName: "├", left: '├', right: '┤', top: '┬', bottom: '┴' }),
        AnchorChar.create({ id: "A61", displayName: "┣", left: '┣', right: '┫', top: '┳', bottom: '┻' }),
        AnchorChar.create({ id: "A62", displayName: "╠", left: '╠', right: '╣', top: '╦', bottom: '╩' }),
    ];

    static readonly PREDEFINED_ANCHOR_CHAR_MAP: { [id: string]: AnchorChar } = Object.fromEntries(
        PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS.map(char => [char.id, char]),
    );
}

/**
 * An object for listing all predefined rectangle border styles.
 */
export class PredefinedRectangleBorderStyle {
    private static readonly PATTERN_TEXT_NO_BORDER = `
+++
+ +
+++`.trim().replace(/\+/g, HALF_TRANSPARENT_CHAR);

    private static readonly PATTERN_TEXT_0 = `
┌─┐
│ │
└─┘
    `.trim();

    private static readonly PATTERN_TEXT_1 = `
┏━┓
┃ ┃
┗━┛
    `.trim();

    private static readonly PATTERN_TEXT_2 = `
╔═╗
║ ║
╚═╝
    `.trim();

    private static readonly REPEATABLE_RANGE_0 = new RepeatRepeatableRange(1, 1);

    static readonly NO_BORDER = new RectangleBorderStyle(
        "B0",
        "No border",
        new NinePatchDrawable(
            NinePatchDrawablePattern.fromText(PredefinedRectangleBorderStyle.PATTERN_TEXT_NO_BORDER),
            PredefinedRectangleBorderStyle.REPEATABLE_RANGE_0,
            PredefinedRectangleBorderStyle.REPEATABLE_RANGE_0
        )
    );

    static readonly PREDEFINED_STYLES: RectangleBorderStyle[] = [
        new RectangleBorderStyle(
            "B1",
            "─",
            new NinePatchDrawable(
                NinePatchDrawablePattern.fromText(PredefinedRectangleBorderStyle.PATTERN_TEXT_0),
                PredefinedRectangleBorderStyle.REPEATABLE_RANGE_0,
                PredefinedRectangleBorderStyle.REPEATABLE_RANGE_0
            )
        ),
        new RectangleBorderStyle(
            "B2",
            "━",
            new NinePatchDrawable(
                NinePatchDrawablePattern.fromText(PredefinedRectangleBorderStyle.PATTERN_TEXT_1),
                PredefinedRectangleBorderStyle.REPEATABLE_RANGE_0,
                PredefinedRectangleBorderStyle.REPEATABLE_RANGE_0
            )
        ),
        new RectangleBorderStyle(
            "B3",
            "═",
            new NinePatchDrawable(
                NinePatchDrawablePattern.fromText(PredefinedRectangleBorderStyle.PATTERN_TEXT_2),
                PredefinedRectangleBorderStyle.REPEATABLE_RANGE_0,
                PredefinedRectangleBorderStyle.REPEATABLE_RANGE_0
            )
        )
    ];

    static readonly PREDEFINED_STYLE_MAP: { [id: string]: RectangleBorderStyle } = Object.fromEntries(
        PredefinedRectangleBorderStyle.PREDEFINED_STYLES.map(style => [style.id, style])
    );
}
