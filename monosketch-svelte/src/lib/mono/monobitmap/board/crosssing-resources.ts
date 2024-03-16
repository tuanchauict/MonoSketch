import type { Char } from '$libs/char';

const M_LEFT = 0b1;
const M_RIGHT = 0b10;
const M_TOP = 0b100;
const M_BOTTOM = 0b1000;
const SINGLE_PAIRS: { [key: string]: string } = {
    '─': '─━═',
    '│': '│┃║',
    '┐': '┐┓╗',
    '┌': '┌┏╔',
    '┘': '┘┛╝',
    '└': '└┗╚',
    '┬': '┬┳╦',
    '┴': '┴┻╩',
    '├': '├┣╠',
    '┤': '┤┫╣',
    '┼': '┼╋╬',
};

const extendChars = (key: string): string[] =>
    Array.from({ length: 3 }, (_, index) => getSingleKey(key, index));

const getSingleKey = (key: string, index: number): string =>
    key
        .split('')
        .map((char) => SINGLE_PAIRS[char]![index])
        .join('');

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

    const CONNECTABLE_CHARS: Set<Char> = [...'─│┌└┐┘┬┴├┤┼']
        .map(extendChars)
        .flat()
        .reduce((acc, char) => new Set([...acc, char]), new Set<Char>());

    const LEFT_IN_CHARS: Set<Char> = [...'─┌└┬┴├┼']
        .map(extendChars)
        .flat()
        .reduce((acc, char) => new Set([...acc, char]), new Set<Char>());

    const RIGHT_IN_CHARS: Set<Char> = [...'─┐┘┬┴┤┼']
        .map(extendChars)
        .flat()
        .reduce((acc, char) => new Set([...acc, char]), new Set<Char>());

    const TOP_IN_CHARS: Set<Char> = [...'│┌┐┬├┤┼']
        .map(extendChars)
        .flat()
        .reduce((acc, char) => new Set([...acc, char]), new Set<Char>());

    const BOTTOM_IN_CHARS: Set<Char> = [...'│└┘┴├┤┼']
        .map(extendChars)
        .flat()
        .reduce((acc, char) => new Set([...acc, char]), new Set<Char>());

    const standardize = (char: string): string => STANDARDIZED_CHARS[char] ?? char;

    export const isConnectable = (char: string): boolean => standardize(char) in CONNECTABLE_CHARS;

    export const hasLeft = (char: string): boolean => standardize(char) in LEFT_IN_CHARS;

    export const hasRight = (char: string): boolean => standardize(char) in RIGHT_IN_CHARS;

    export const hasTop = (char: string): boolean => standardize(char) in TOP_IN_CHARS;

    export const hasBottom = (char: string): boolean => standardize(char) in BOTTOM_IN_CHARS;

    export const isConnectableChar = (char: string): boolean => {
        return CONNECTABLE_CHARS.has(char);
    };

    /**
     * A utility method for creating a mark vector for in-directions.
     */
    export function inDirectionMark(
        hasLeft: boolean,
        hasRight: boolean,
        hasTop: boolean,
        hasBottom: boolean,
    ): number {
        const leftMark = hasLeft ? M_LEFT : 0;
        const rightMark = hasRight ? M_RIGHT : 0;
        const topMark = hasTop ? M_TOP : 0;
        const bottomMark = hasBottom ? M_BOTTOM : 0;
        return leftMark | topMark | rightMark | bottomMark;
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const getDirectionMap = (char1: Char, char2: Char): Map<number, Char> | null => {
        throw new Error('Not implemented');
    };
}
