import type { Char } from '$libs/char';
import { isHalfTransparentChar, isTransparentChar, TRANSPARENT_CHAR } from '$mono/common/character';
import type { Comparable } from '$libs/comparable';

/**
 * An interface for the pixel of the Mono Bitmap.
 */
export class Pixel implements Comparable {
    static TRANSPARENT = new Pixel();

    private visualCharInner: Char = TRANSPARENT_CHAR;
    private directionCharInner: Char = TRANSPARENT_CHAR;
    private highlightInner: HighlightType = HighlightType.NO;

    get isTransparent(): boolean {
        const char = this.visualCharInner;
        return isTransparentChar(char) || isHalfTransparentChar(char);
    }

    get visualChar(): Char {
        return this.visualCharInner;
    }

    get directionChar(): Char {
        return this.directionCharInner;
    }

    get highlight(): HighlightType {
        return this.highlightInner;
    }

    set = (visualChar: Char, directionChar: Char, highlight: HighlightType) => {
        this.visualCharInner = visualChar;
        this.directionCharInner = directionChar;
        this.highlightInner = highlight;
    };

    reset = () => {
        this.visualCharInner = TRANSPARENT_CHAR;
        this.directionCharInner = TRANSPARENT_CHAR;
        this.highlightInner = HighlightType.NO;
    };

    toString = (): string => (this.isTransparent ? ' ' : this.visualChar);

    equals(other: unknown): boolean {
        if (!(other instanceof Pixel)) {
            return false;
        }
        const otherPixel = other as Pixel;
        return (
            this.visualCharInner === otherPixel.visualCharInner &&
            this.highlightInner === otherPixel.highlightInner
        );
    }
}

export enum HighlightType {
    NO,
    SELECTED,
    TEXT_EDITING,
    LINE_CONNECT_FOCUSING,
}
