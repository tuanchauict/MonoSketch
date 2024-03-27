import { describe, test } from 'vitest';

// This file is for testing a new idea for identifying the crossing point.
// Currently, the crossing point is identified by a Database of characters but this is hard to
// maintain.
// The new idea uses masks to identify the crossing point.
// The mask is a 4-bit number that represents the direction of the crossing point and its 4 sides.
// 1 crossing point is created by 2 overlapping characters using `bit-or` operator.
// 4 sides identifies a mask with bit-or operator.
// The mask is created by the 2 characters `bit-and` their 4 surrounding characters.

const surroundLeftChars = '─┌└┬┴├┼';
const surroundRightChars = '─┐┘┬┴┤┼';
const surroundTopChars = '│┌┐┬├┼';
const surroundBottomChars = '│└┘┴┤┼';

const M_LEFT = 0b1;
const M_RIGHT = 0b10;
const M_TOP = 0b100;
const M_BOTTOM = 0b1000;
const M_VERTICAL = M_TOP | M_BOTTOM;
const M_HORIZONTAL = M_LEFT | M_RIGHT;

const MASK_TO_CHAR: Map<number, string> = (() => {
    const result = new Map<number, string>();
    result.set(M_HORIZONTAL, '─');
    result.set(M_VERTICAL, '│');
    result.set(M_LEFT | M_VERTICAL, '┤');
    result.set(M_RIGHT | M_VERTICAL, '├');
    result.set(M_HORIZONTAL | M_TOP, '┴');
    result.set(M_HORIZONTAL | M_BOTTOM, '┬');
    result.set(M_LEFT | M_TOP, '┘');
    result.set(M_LEFT | M_BOTTOM, '┐');
    result.set(M_RIGHT | M_TOP, '└');
    result.set(M_RIGHT | M_BOTTOM, '┌');
    result.set(M_HORIZONTAL | M_VERTICAL, '┼');
    return result;
})();

const identifySurroundMask = (left: string, right: string, top: string, bottom: string): number => {
    let result = 0;
    if (surroundLeftChars.includes(left)) {
        result |= M_LEFT;
    }
    if (surroundRightChars.includes(right)) {
        result |= M_RIGHT;
    }
    if (surroundTopChars.includes(top)) {
        result |= M_TOP;
    }
    if (surroundBottomChars.includes(bottom)) {
        result |= M_BOTTOM;
    }
    return result;
};

const identifyInnerMask = (char: string): number => {
    let result = 0;
    // inner opposite to surround
    if (surroundLeftChars.includes(char)) {
        result |= M_RIGHT;
    }
    // inner opposite to surround
    if (surroundRightChars.includes(char)) {
        result |= M_LEFT;
    }
    // inner opposite to surround
    if (surroundTopChars.includes(char)) {
        result |= M_BOTTOM;
    }
    // inner opposite to surround
    if (surroundBottomChars.includes(char)) {
        result |= M_TOP;
    }
    return result;
};

const directionMask = (char1: string, char2: string, left: string, right: string, top: string, bottom: string): number => {
    const mask1 = identifyInnerMask(char1);
    const mask2 = identifyInnerMask(char2);
    console.log(toBinaryString(mask1), MASK_TO_CHAR.get(mask1), toBinaryString(mask2), MASK_TO_CHAR.get(mask2));
    const inner = mask1 | mask2;
    const surround = identifySurroundMask(left, right, top, bottom);
    console.log(toBinaryString(inner), MASK_TO_CHAR.get(inner), toBinaryString(surround));
    const result = inner & surround;
    console.log(toBinaryString(result));
    return result;
};

function toBinaryString(num: number): string {
    const text = num.toString(2);
    const missing = 8 - text.length;
    return '0'.repeat(missing) + text;
}

describe('CrossingResources', () => {
    test('try new approach', () => {
        const res = directionMask('─', '│', '─', '─', '│', '│');
        console.log(MASK_TO_CHAR.get(res));
    });
});
