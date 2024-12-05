/*
 * Copyright (c) 2024, tuanchauict
 */


import { HALF_TRANSPARENT_CHAR, NBSP, TRANSPARENT_CHAR } from "$mono/common/character";
import { CharDrawable } from "$mono/monobitmap/drawable";
import { AnchorChar, RectangleFillStyle, StraightStrokeStyle } from "$mono/shape/extra/style";

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
        new RectangleFillStyle("F1", NBSP, new CharDrawable(' ')),
        new RectangleFillStyle("F2", "█", new CharDrawable('█')),
        new RectangleFillStyle("F3", "▒", new CharDrawable('▒')),
        new RectangleFillStyle("F4", "░", new CharDrawable('░')),
        new RectangleFillStyle("F5", "▚", new CharDrawable('▚')),
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
        new StraightStrokeStyle("S1", "─", '─', '│', '┐', '┌', '┘', '└',),
        new StraightStrokeStyle("S2", "━", '━', '┃', '┓', '┏', '┛', '┗',),
        new StraightStrokeStyle("S3", "═", '═', '║', '╗', '╔', '╝', '╚',),
        new StraightStrokeStyle("S4", "▢", '─', '│', '╮', '╭', '╯', '╰',),
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

    static isCornerRoundable(id: string): boolean {
        return id in PredefinedStraightStrokeStyle.STYLE_TO_ROUNDED_CORNER_STYLE_MAP;
    }
}

/**
 * An object for listing all predefined anchor chars.
 */
export class PredefinedAnchorChar {
    static readonly PREDEFINED_ANCHOR_CHARS: AnchorChar[] = [
        new AnchorChar("A1", "▶", '◀', '▶', '▲', '▼'),
        new AnchorChar("A12", "▷", '◁', '▷', '△', '▽'),
        AnchorChar.fromSingle("A2", "■", '■'),
        AnchorChar.fromSingle("A21", "□", '□'),
        AnchorChar.fromSingle("A220", "◆", '◆'),
        AnchorChar.fromSingle("A221", "◇", '◇'),
        AnchorChar.fromSingle("A3", "○", '○'),
        AnchorChar.fromSingle("A4", "◎", '◎'),
        AnchorChar.fromSingle("A5", "●", '●'),
        new AnchorChar("A6", "├", '├', '┤', '┬', '┴'),
        new AnchorChar("A61", "┣", '┣', '┫', '┳', '┻'),
        new AnchorChar("A62", "╠", '╠', '╣', '╦', '╩'),
    ];

    static readonly PREDEFINED_ANCHOR_CHAR_MAP: { [id: string]: AnchorChar } = Object.fromEntries(
        PredefinedAnchorChar.PREDEFINED_ANCHOR_CHARS.map(char => [char.id, char]),
    );
}