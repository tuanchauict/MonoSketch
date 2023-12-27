/**
 * An object to contains all handling key-codes.
 */
export class Key {
    static readonly KEY_ESC = 27;
    static readonly KEY_SHIFT = 16;
    static readonly KEY_ENTER = 13;
    static readonly KEY_BACKSPACE = 8;
    static readonly KEY_DELETE = 46;
    static readonly KEY_ARROW_LEFT = 37;
    static readonly KEY_ARROW_UP = 38;
    static readonly KEY_ARROW_RIGHT = 39;
    static readonly KEY_ARROW_DOWN = 40;
    static readonly KEY_A = 65;
    static readonly KEY_C = 67;
    static readonly KEY_D = 68;
    static readonly KEY_L = 76;
    static readonly KEY_R = 82;
    static readonly KEY_T = 84;
    static readonly KEY_V = 86;
    static readonly KEY_X = 88;
    static readonly KEY_Z = 90;
}

export class Characters {
    static readonly TRANSPARENT_CHAR: string = String.fromCharCode(0);
    static readonly HALF_TRANSPARENT_CHAR: string = String.fromCharCode(1);
    static readonly NBSP: string = String.fromCharCode(0x00A0);

    static copyChars(
            src: string[],
            srcOffset: number,
            dest: string[],
            destOffset: number,
            length: number
    ): void {
        src.slice(srcOffset, srcOffset + length).forEach((char, index) => {
            if (char !== Characters.TRANSPARENT_CHAR) {
                dest[destOffset + index] = char;
            }
        });
    }

    static isTransparent(char: string): boolean {
        return char === Characters.TRANSPARENT_CHAR;
    }

    static isHalfTransparent(char: string): boolean {
        return char === Characters.HALF_TRANSPARENT_CHAR;
    }
}

/**
 * A class for enumerating all in-use mouse cursors.
 */
export enum MouseCursor {
    DEFAULT = "default",
    TEXT = "text",
    CROSSHAIR = "crosshair",
    MOVE = "move",
    RESIZE_NWSE = "nwse-resize",
    RESIZE_NS = "ns-resize",
    RESIZE_NESW = "nesw-resize",
    RESIZE_EW = "ew-resize",
    RESIZE_ROW = "row-resize",
    RESIZE_COL = "col-resize"
}
