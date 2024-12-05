import type { Char } from "$libs/char";
import { TRANSPARENT_CHAR } from "$mono/common/character";
import { MonoBitmap } from "$mono/monobitmap/bitmap/monobitmap";
import type { Drawable } from "$mono/monobitmap/drawable/drawable";

/**
 * A simple 9-patch image which scales image based on repeating points in
 * horizontal and vertical repeatable range.
 */
export class NinePatchDrawable implements Drawable {
    private pattern: NinePatchDrawablePattern;
    private horizontalRepeatableRange: NinePatchDrawableRepeatableRange;
    private verticalRepeatableRange: NinePatchDrawableRepeatableRange;

    constructor(
        pattern: NinePatchDrawablePattern,
        horizontalRepeatableRange: NinePatchDrawableRepeatableRange = new ScaleRepeatableRange(0, pattern.width - 1),
        verticalRepeatableRange: NinePatchDrawableRepeatableRange = new ScaleRepeatableRange(0, pattern.height - 1),
    ) {
        this.pattern = pattern;
        this.horizontalRepeatableRange = horizontalRepeatableRange;
        this.verticalRepeatableRange = verticalRepeatableRange;
    }

    toBitmap(width: number, height: number): MonoBitmap.Bitmap {
        const builder = new MonoBitmap.Builder(width, height);
        const rowIndexes = this.verticalRepeatableRange.toIndexes(height, this.pattern.height);
        const colIndexes = this.horizontalRepeatableRange.toIndexes(width, this.pattern.width);
        for (let row = 0; row < height; row++) {
            for (let col = 0; col < width; col++) {
                builder.put(
                    row,
                    col,
                    this.pattern.getChar(rowIndexes[row], colIndexes[col]),
                    // TODO: Think about a way to support direction chars to 9-patch drawable
                    this.pattern.getChar(rowIndexes[row], colIndexes[col]),
                );
            }
        }
        return builder.toBitmap();
    }
}

/**
 * A data class which provides a basic render characters for 9-patch image.
 */
export class NinePatchDrawablePattern {

    constructor(public width: number, public height: number, private chars: Char[]) {
        if (chars.length < width * height) {
            throw new Error("Mismatch between size and number of chars provided");
        }
    }

    getChar(row: number, column: number): Char {
        const index = row * this.width + column;
        return this.chars[index];
    }

    static fromText(text: string, delimiter: Char = '\n', transparentChar: Char = ' '): NinePatchDrawablePattern {
        const array = text.split(delimiter);
        if (array.length === 0) {
            return new NinePatchDrawablePattern(0, 0, []);
        }
        const width = array[0].length;
        const height = array.length;
        const chars = array.map((char) => char === transparentChar ? TRANSPARENT_CHAR : char);
        return new NinePatchDrawablePattern(width, height, chars);
    }
}

/**
 * The algorithm for repeating the repeated range.
 */
export abstract class NinePatchDrawableRepeatableRange {
    protected start: number;
    protected endInclusive: number;

    constructor(start: number, endInclusive: number) {
        this.start = Math.max(0, Math.min(start, endInclusive));
        this.endInclusive = Math.max(start, endInclusive);
    }

    /**
     * Creates a list with size [size] of indexes whole value in range [0, [patternSize]).
     * If [patternSize] < [endInclusive], [endInclusive] will be used for the index value range.
     */
    toIndexes(size: number, patternSize: number): number[] {
        const adjustedEndInclusive = Math.min(patternSize - 1, this.endInclusive);
        const rangeSize = adjustedEndInclusive - this.start + 1;
        const repeatingLeft = this.start;
        const repeatingRight = size - (patternSize - adjustedEndInclusive);

        return Array.from({ length: size }, (_, index) => {
            if (index < repeatingLeft) return index;
            if (index > repeatingRight) return adjustedEndInclusive + index - repeatingRight;
            return this.scaleIndex(index, repeatingLeft, repeatingRight, rangeSize);
        });
    }

    protected abstract scaleIndex(index: number, minRepeatingIndex: number, maxRepeatingIndex: number, rangeSize: number): number;
}

/**
 * The algorithm for repeating the repeated range: `01` -> `00001111`
 */
export class ScaleRepeatableRange extends NinePatchDrawableRepeatableRange {
    scaleIndex(index: number, minRepeatingIndex: number, maxRepeatingIndex: number, rangeSize: number): number {
        const repeatingSize = maxRepeatingIndex - minRepeatingIndex + 1;
        return minRepeatingIndex + Math.floor((index - minRepeatingIndex) * rangeSize / repeatingSize);
    }
}

/**
 * The algorithm for repeating the repeated range: `01` -> `01010101`
 */
export class RepeatRepeatableRange extends NinePatchDrawableRepeatableRange {
    scaleIndex(index: number, minRepeatingIndex: number, maxRepeatingIndex: number, rangeSize: number): number {
        return minRepeatingIndex + (index - minRepeatingIndex) % rangeSize;
    }
}