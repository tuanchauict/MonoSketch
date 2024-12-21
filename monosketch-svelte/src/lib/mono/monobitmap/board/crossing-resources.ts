import type { Char } from '$libs/char';
import { DEBUG_MODE } from "$mono/build_environment";

const DEBUG = false;

export namespace CrossingResources {
    const STANDARDIZED_CHARS: { [key: string]: string } = {
        '-': '─',
        '|': '│',
        '+': '┼',
        '╮': '┐',
        '╭': '┌',
        '╯': '┘',
        '╰': '└',
    };

    // Constants representing bit masks for directions (single lines)
    const MASK_SINGLE_LEFT = 0b0001;
    const MASK_SINGLE_RIGHT = 0b0010;
    const MASK_SINGLE_TOP = 0b0100;
    const MASK_SINGLE_BOTTOM = 0b1000;
    const MASK_SINGLE_HORIZONTAL = MASK_SINGLE_LEFT | MASK_SINGLE_RIGHT;
    const MASK_SINGLE_VERTICAL = MASK_SINGLE_TOP | MASK_SINGLE_BOTTOM;
    const MASK_SINGLE_CROSS = MASK_SINGLE_HORIZONTAL | MASK_SINGLE_VERTICAL;

    // Constants representing bit masks for directions (bold lines)
    const MASK_BOLD_LEFT = MASK_SINGLE_LEFT << 4; // Left-shift by 4 for bold lines
    const MASK_BOLD_RIGHT = MASK_SINGLE_RIGHT << 4;
    const MASK_BOLD_TOP = MASK_SINGLE_TOP << 4;
    const MASK_BOLD_BOTTOM = MASK_SINGLE_BOTTOM << 4;
    const MASK_BOLD_HORIZONTAL = MASK_SINGLE_HORIZONTAL << 4;
    const MASK_BOLD_VERTICAL = MASK_SINGLE_VERTICAL << 4;
    const MASK_BOLD_CROSS = MASK_SINGLE_CROSS << 4;

    // Constants representing bit masks for directions (double lines)
    const MASK_DOUBLE_LEFT = MASK_SINGLE_LEFT << 8; // Left-shift by 8 for double lines
    const MASK_DOUBLE_RIGHT = MASK_SINGLE_RIGHT << 8;
    const MASK_DOUBLE_TOP = MASK_SINGLE_TOP << 8;
    const MASK_DOUBLE_BOTTOM = MASK_SINGLE_BOTTOM << 8;
    const MASK_DOUBLE_HORIZONTAL = MASK_SINGLE_HORIZONTAL << 8;
    const MASK_DOUBLE_VERTICAL = MASK_SINGLE_VERTICAL << 8;
    const MASK_DOUBLE_CROSS = MASK_SINGLE_CROSS << 8;

    // Constants representing combined masks for all line styles (single, bold, double)
    const MASK_LEFT = MASK_SINGLE_LEFT | MASK_BOLD_LEFT | MASK_DOUBLE_LEFT;
    const MASK_RIGHT = MASK_SINGLE_RIGHT | MASK_BOLD_RIGHT | MASK_DOUBLE_RIGHT;
    const MASK_TOP = MASK_SINGLE_TOP | MASK_BOLD_TOP | MASK_DOUBLE_TOP;
    const MASK_BOTTOM = MASK_SINGLE_BOTTOM | MASK_BOLD_BOTTOM | MASK_DOUBLE_BOTTOM;
    const MASK_CROSS = MASK_SINGLE_CROSS | MASK_BOLD_CROSS | MASK_DOUBLE_CROSS;

    // Map of characters to their corresponding bit masks
    const CHAR_TO_MASK_MAP: { [key: string]: number } = {
        '─': MASK_SINGLE_HORIZONTAL,
        '│': MASK_SINGLE_VERTICAL,
        '┘': (MASK_SINGLE_LEFT | MASK_SINGLE_TOP),
        '┐': (MASK_SINGLE_LEFT | MASK_SINGLE_BOTTOM),
        '┤': (MASK_SINGLE_LEFT | MASK_SINGLE_VERTICAL),
        '└': (MASK_SINGLE_RIGHT | MASK_SINGLE_TOP),
        '┌': (MASK_SINGLE_RIGHT | MASK_SINGLE_BOTTOM),
        '├': (MASK_SINGLE_RIGHT | MASK_SINGLE_VERTICAL),
        '┴': (MASK_SINGLE_HORIZONTAL | MASK_SINGLE_TOP),
        '┬': (MASK_SINGLE_HORIZONTAL | MASK_SINGLE_BOTTOM),
        '┼': (MASK_SINGLE_HORIZONTAL | MASK_SINGLE_VERTICAL),

        '━': MASK_BOLD_HORIZONTAL,
        '┃': MASK_BOLD_VERTICAL,
        '┛': (MASK_BOLD_LEFT | MASK_BOLD_TOP),
        '┓': (MASK_BOLD_LEFT | MASK_BOLD_BOTTOM),
        '┫': (MASK_BOLD_LEFT | MASK_BOLD_VERTICAL),
        '┗': (MASK_BOLD_RIGHT | MASK_BOLD_TOP),
        '┏': (MASK_BOLD_RIGHT | MASK_BOLD_BOTTOM),
        '┣': (MASK_BOLD_RIGHT | MASK_BOLD_VERTICAL),
        '┻': (MASK_BOLD_HORIZONTAL | MASK_BOLD_TOP),
        '┳': (MASK_BOLD_HORIZONTAL | MASK_BOLD_BOTTOM),
        '╋': (MASK_BOLD_HORIZONTAL | MASK_BOLD_VERTICAL),

        '═': MASK_DOUBLE_HORIZONTAL,
        '║': MASK_DOUBLE_VERTICAL,
        '╝': (MASK_DOUBLE_LEFT | MASK_DOUBLE_TOP),
        '╗': (MASK_DOUBLE_LEFT | MASK_DOUBLE_BOTTOM),
        '╣': (MASK_DOUBLE_LEFT | MASK_DOUBLE_VERTICAL),
        '╚': (MASK_DOUBLE_RIGHT | MASK_DOUBLE_TOP),
        '╔': (MASK_DOUBLE_RIGHT | MASK_DOUBLE_BOTTOM),
        '╠': (MASK_DOUBLE_RIGHT | MASK_DOUBLE_VERTICAL),
        '╩': (MASK_DOUBLE_HORIZONTAL | MASK_DOUBLE_TOP),
        '╦': (MASK_DOUBLE_HORIZONTAL | MASK_DOUBLE_BOTTOM),
        '╬': (MASK_DOUBLE_HORIZONTAL | MASK_DOUBLE_VERTICAL),

        // Complex (SINGLE, BOLD) combinations
        '╼': (MASK_SINGLE_LEFT | MASK_BOLD_RIGHT),
        '╾': (MASK_BOLD_LEFT | MASK_SINGLE_RIGHT),

        '╽': (MASK_SINGLE_TOP | MASK_BOLD_BOTTOM),
        '╿': (MASK_BOLD_TOP | MASK_SINGLE_BOTTOM),

        '┚': (MASK_SINGLE_LEFT | MASK_BOLD_TOP),
        '┙': (MASK_BOLD_LEFT | MASK_SINGLE_TOP),

        '┒': (MASK_SINGLE_LEFT | MASK_BOLD_BOTTOM),
        '┑': (MASK_BOLD_LEFT | MASK_SINGLE_BOTTOM),

        '┨': (MASK_SINGLE_LEFT | MASK_BOLD_TOP | MASK_BOLD_BOTTOM),
        '┦': (MASK_SINGLE_LEFT | MASK_BOLD_TOP | MASK_SINGLE_BOTTOM),
        '┧': (MASK_SINGLE_LEFT | MASK_SINGLE_TOP | MASK_BOLD_BOTTOM),

        '┥': (MASK_BOLD_LEFT | MASK_SINGLE_TOP | MASK_SINGLE_BOTTOM),
        '┩': (MASK_BOLD_LEFT | MASK_BOLD_TOP | MASK_SINGLE_BOTTOM),
        '┪': (MASK_BOLD_LEFT | MASK_SINGLE_TOP | MASK_BOLD_BOTTOM),

        '┖': (MASK_SINGLE_RIGHT | MASK_BOLD_TOP),
        '┕': (MASK_BOLD_RIGHT | MASK_SINGLE_TOP),

        '┎': (MASK_SINGLE_RIGHT | MASK_BOLD_BOTTOM),
        '┍': (MASK_BOLD_RIGHT | MASK_SINGLE_BOTTOM),

        '┠': (MASK_SINGLE_RIGHT | MASK_BOLD_TOP | MASK_BOLD_BOTTOM),
        '┞': (MASK_SINGLE_RIGHT | MASK_BOLD_TOP | MASK_SINGLE_BOTTOM),
        '┟': (MASK_SINGLE_RIGHT | MASK_SINGLE_TOP | MASK_BOLD_BOTTOM),

        '┝': (MASK_BOLD_RIGHT | MASK_SINGLE_TOP | MASK_SINGLE_BOTTOM),
        '┡': (MASK_BOLD_RIGHT | MASK_BOLD_TOP | MASK_SINGLE_BOTTOM),
        '┢': (MASK_BOLD_RIGHT | MASK_SINGLE_TOP | MASK_BOLD_BOTTOM),

        '┷': (MASK_BOLD_LEFT | MASK_BOLD_RIGHT | MASK_SINGLE_TOP),
        '┶': (MASK_SINGLE_LEFT | MASK_BOLD_RIGHT | MASK_SINGLE_TOP),
        '┵': (MASK_BOLD_LEFT | MASK_SINGLE_RIGHT | MASK_SINGLE_TOP),

        '┸': (MASK_SINGLE_LEFT | MASK_SINGLE_RIGHT | MASK_BOLD_TOP),
        '┹': (MASK_BOLD_LEFT | MASK_SINGLE_RIGHT | MASK_BOLD_TOP),
        '┺': (MASK_SINGLE_LEFT | MASK_BOLD_RIGHT | MASK_BOLD_TOP),

        '┯': (MASK_BOLD_LEFT | MASK_BOLD_RIGHT | MASK_SINGLE_BOTTOM),
        '┭': (MASK_BOLD_LEFT | MASK_SINGLE_RIGHT | MASK_SINGLE_BOTTOM),
        '┮': (MASK_SINGLE_LEFT | MASK_BOLD_RIGHT | MASK_SINGLE_BOTTOM),

        '┰': (MASK_SINGLE_LEFT | MASK_SINGLE_RIGHT | MASK_BOLD_BOTTOM),
        '┱': (MASK_BOLD_LEFT | MASK_SINGLE_RIGHT | MASK_BOLD_BOTTOM),
        '┲': (MASK_SINGLE_LEFT | MASK_BOLD_RIGHT | MASK_BOLD_BOTTOM),

        '┽': (MASK_BOLD_LEFT | MASK_SINGLE_RIGHT | MASK_SINGLE_TOP | MASK_SINGLE_BOTTOM),
        '┾': (MASK_SINGLE_LEFT | MASK_BOLD_RIGHT | MASK_SINGLE_TOP | MASK_SINGLE_BOTTOM),
        '╀': (MASK_SINGLE_LEFT | MASK_SINGLE_RIGHT | MASK_BOLD_TOP | MASK_SINGLE_BOTTOM),
        '╁': (MASK_SINGLE_LEFT | MASK_SINGLE_RIGHT | MASK_SINGLE_TOP | MASK_BOLD_BOTTOM),

        '╂': (MASK_SINGLE_LEFT | MASK_SINGLE_RIGHT | MASK_BOLD_TOP | MASK_BOLD_BOTTOM),
        '┿': (MASK_BOLD_LEFT | MASK_BOLD_RIGHT | MASK_SINGLE_TOP | MASK_SINGLE_BOTTOM),
        '╃': (MASK_BOLD_LEFT | MASK_SINGLE_RIGHT | MASK_BOLD_TOP | MASK_SINGLE_BOTTOM),
        '╄': (MASK_SINGLE_LEFT | MASK_BOLD_RIGHT | MASK_BOLD_TOP | MASK_SINGLE_BOTTOM),
        '╅': (MASK_BOLD_LEFT | MASK_SINGLE_RIGHT | MASK_SINGLE_TOP | MASK_BOLD_BOTTOM),
        '╆': (MASK_SINGLE_LEFT | MASK_BOLD_RIGHT | MASK_SINGLE_TOP | MASK_BOLD_BOTTOM),

        '╇': (MASK_BOLD_LEFT | MASK_BOLD_RIGHT | MASK_BOLD_TOP | MASK_SINGLE_BOTTOM),
        '╈': (MASK_BOLD_LEFT | MASK_BOLD_RIGHT | MASK_SINGLE_TOP | MASK_BOLD_BOTTOM),
        '╉': (MASK_BOLD_LEFT | MASK_SINGLE_RIGHT | MASK_BOLD_TOP | MASK_BOLD_BOTTOM),
        '╊': (MASK_SINGLE_LEFT | MASK_BOLD_RIGHT | MASK_BOLD_TOP | MASK_BOLD_BOTTOM),

        // Complex (SINGLE, DOUBLE) combinations
        '╒': (MASK_DOUBLE_RIGHT | MASK_SINGLE_BOTTOM),
        '╓': (MASK_SINGLE_RIGHT | MASK_DOUBLE_BOTTOM),

        '╕': (MASK_DOUBLE_LEFT | MASK_SINGLE_BOTTOM),
        '╖': (MASK_SINGLE_LEFT | MASK_DOUBLE_BOTTOM),

        '╘': (MASK_DOUBLE_RIGHT | MASK_SINGLE_TOP),
        '╙': (MASK_SINGLE_RIGHT | MASK_DOUBLE_TOP),

        '╛': (MASK_DOUBLE_LEFT | MASK_SINGLE_TOP),
        '╜': (MASK_SINGLE_LEFT | MASK_DOUBLE_TOP),

        '╞': (MASK_DOUBLE_RIGHT | MASK_SINGLE_TOP | MASK_SINGLE_BOTTOM),
        '╟': (MASK_SINGLE_RIGHT | MASK_DOUBLE_TOP | MASK_DOUBLE_BOTTOM),

        '╡': (MASK_DOUBLE_LEFT | MASK_SINGLE_TOP | MASK_SINGLE_BOTTOM),
        '╢': (MASK_SINGLE_LEFT | MASK_DOUBLE_TOP | MASK_DOUBLE_BOTTOM),

        '╤': (MASK_DOUBLE_LEFT | MASK_DOUBLE_RIGHT | MASK_SINGLE_BOTTOM),
        '╥': (MASK_SINGLE_LEFT | MASK_SINGLE_RIGHT | MASK_DOUBLE_BOTTOM),

        '╧': (MASK_DOUBLE_LEFT | MASK_DOUBLE_RIGHT | MASK_SINGLE_TOP),
        '╨': (MASK_SINGLE_LEFT | MASK_SINGLE_RIGHT | MASK_DOUBLE_TOP),

        '╪': (MASK_DOUBLE_LEFT | MASK_DOUBLE_RIGHT | MASK_SINGLE_TOP | MASK_SINGLE_BOTTOM),
        '╫': (MASK_SINGLE_LEFT | MASK_SINGLE_RIGHT | MASK_DOUBLE_TOP | MASK_DOUBLE_BOTTOM),
    };

    const MASK_TO_CHAR_MAP: { [key: number]: Char } = Object.fromEntries(
        Object.entries(CHAR_TO_MASK_MAP).map(([key, value]) => [value, key]),
    );

    const standardize = (char: Char): Char => STANDARDIZED_CHARS[char] ?? char;

    export const hasLeft = (char: Char): boolean => getCharMask(standardize(char), MASK_RIGHT) > 0;

    export const hasRight = (char: Char): boolean => getCharMask(standardize(char), MASK_LEFT) > 0;

    export const hasTop = (char: Char): boolean => getCharMask(standardize(char), MASK_BOTTOM) > 0;

    export const hasBottom = (char: Char): boolean => getCharMask(standardize(char), MASK_TOP) > 0;

    export const isConnectableChar = (char: Char): boolean => (CHAR_TO_MASK_MAP[char] ?? 0) > 0;

    export function getCrossingChar(
        upper: string,
        adjacentLeftUpper: string,
        adjacentRightUpper: string,
        adjacentTopUpper: string,
        adjacentBottomUpper: string,
        lower: string,
        adjacentLeftLower: string,
        adjacentRightLower: string,
        adjacentTopLower: string,
        adjacentBottomLower: string,
    ): string | null {
        const maskUpper = getCharMask(upper, MASK_CROSS);
        // Directions exist in the upper char exclude the direction in the lower char.
        const maskLower = getCharMask(
            lower,
            createExcludeMask(maskUpper),
        );

        const maskLeft =
            (hasLeft(adjacentLeftUpper) || hasLeft(adjacentLeftLower)) ? MASK_LEFT : 0;
        const maskRight =
            (hasRight(adjacentRightUpper) || hasRight(adjacentRightLower)) ? MASK_RIGHT : 0;
        const maskTop =
            (hasTop(adjacentTopUpper) || hasTop(adjacentTopLower)) ? MASK_TOP : 0;
        const maskBottom =
            (hasBottom(adjacentBottomUpper) || hasBottom(adjacentBottomLower)) ? MASK_BOTTOM : 0;

        const innerMask = maskUpper | maskLower;
        const outerMask = maskLeft | maskRight | maskTop | maskBottom;
        const mask = innerMask & outerMask;

        if (DEBUG && DEBUG_MODE) {
            console.log(
                [
                    `${upper}:${maskToString(maskUpper)}`,
                    `${lower}:${maskToString(maskLower)}`,
                    `-> ${maskToString(innerMask)}`,
                ].toString(),
                [
                    `${adjacentLeftUpper}:${adjacentLeftLower}:${maskToString(maskLeft)}`,
                    `${adjacentRightUpper}:${adjacentRightLower}:${maskToString(maskRight)}`,
                    `${adjacentTopUpper}:${adjacentTopLower}:${maskToString(maskTop)}`,
                    `${adjacentBottomUpper}:${adjacentBottomLower}:${maskToString(maskBottom)}`,
                    `-> ${maskToString(outerMask)}`,
                ].toString(),
                maskToString(outerMask),
                "->",
                maskToString(mask),
                MASK_TO_CHAR_MAP[mask]?.toString() || "null",
            );
        }
        return MASK_TO_CHAR_MAP[mask] || null;
    }

    function getCharMask(char: Char, mask: number): number {
        const charMask = CHAR_TO_MASK_MAP[standardize(char)];
        return charMask !== undefined ? (charMask & mask) : 0;
    }

    function maskToString(mask: number): string {
        return mask.toString(2).padStart(12, '0');
    }

    /**
     * Creates a mask that excludes bits from any direction existing in the given mask.
     * For example, if mask is from `│` (`0000.0000.1100`), the result will be `0011.0011.0011`
     */
    function createExcludeMask(mask: number): number {
        // and with MASK_CROSS to remove the overflow bits.
        const allDirectionsMask =
            ((mask << 8) | (mask << 4) | mask | (mask >> 4) | (mask >> 8)) & MASK_CROSS;
        return MASK_CROSS ^ allDirectionsMask;
    }
}
