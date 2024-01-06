export class Characters {
    static readonly TRANSPARENT_CHAR: string = String.fromCharCode(0);
    static readonly HALF_TRANSPARENT_CHAR: string = String.fromCharCode(1);
    static readonly NBSP: string = String.fromCharCode(0x00a0);

    static copyChars(
        src: string[],
        srcOffset: number,
        dest: string[],
        destOffset: number,
        length: number,
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
    DEFAULT = 'default',
    TEXT = 'text',
    CROSSHAIR = 'crosshair',
    MOVE = 'move',
    RESIZE_NWSE = 'nwse-resize',
    RESIZE_NS = 'ns-resize',
    RESIZE_NESW = 'nesw-resize',
    RESIZE_EW = 'ew-resize',
    RESIZE_ROW = 'row-resize',
    RESIZE_COL = 'col-resize',
}
