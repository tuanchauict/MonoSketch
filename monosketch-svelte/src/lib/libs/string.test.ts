import { describe, expect, test } from 'vitest';
import { StringExt } from '$libs/string';

describe('StringExt', () => {
    test('trimMargin', () => {
        const input = `
            | 1 |
            | 2 |
            | 3 |
        `;
        const expected = ` 1 |\n 2 |\n 3 |`;
        expect(StringExt.trimMargin(input)).toStrictEqual(expected);
    });

    test('trimMargin with custom prefix', () => {
        const input = `
            # 1 |
            # 2 |
            # 3 |
        `;
        const expected = ` 1 |\n 2 |\n 3 |`;
        expect(StringExt.trimMargin(input, '#')).toStrictEqual(expected);
    });
});
