import type { Char } from '$libs/char';

export const APP_CONTEXT = 'app-context';

// Transparent in both rendering and selection
export const TRANSPARENT_CHAR: Char = String.fromCharCode(0);

// Transparent in rendering but visible for selection
export const HALF_TRANSPARENT_CHAR: Char = String.fromCharCode(1);

export const NBSP: Char = String.fromCharCode(0x00A0);

export const isTransparentChar = (char: Char): boolean => {
    return char === TRANSPARENT_CHAR;
}

export const isHalfTransparentChar = (char: Char): boolean => {
    return char === HALF_TRANSPARENT_CHAR;
}
