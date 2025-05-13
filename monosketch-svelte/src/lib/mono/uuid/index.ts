/**
 * An object class that generate unique ID.
 * ### Current version
 * Version 2:
 * ```
 * Structure: version datetime random random
 * Length   :   3        8      10     10
 * ```
 * Version: 02-
 * Datetime: Base64-like encoded current time (8 chars)
 * Random: Base64-like encoded random number (10 chars)
 * Characters: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
 *
 * ### Older versions:
 * Version 1:
 * ```
 * Structure: version datetime random random
 * Length   :   3        8      10     10
 * ```
 * Version: 01-
 * Datetime: Base64-like encoded current time (8 chars)
 * Random: Base64-like encoded random number (10 chars)
 * Characters: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+="
 */
export class UUID {
    private static readonly VERSION = '02';
    private static readonly BASE64_CHARS =
        'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_'.split('');

    private static now(): number {
        return Date.now();
    }

    static generate(): string {
        const part1 = this.toBase64(this.now()).slice(0, -2);
        const part2 = UUID.toBase64(UUID.getRandomLong());
        const part3 = UUID.toBase64(UUID.getRandomLong());
        return `${UUID.VERSION}-${part1}${part2}${part3}`;
    }

    private static toBase64(number: number): string {
        const chars: string[] = [];
        for (let i = 0; i < 10; i++) {
            const v = number & 0b0111111;
            number = number >>> 6;
            chars[i] = UUID.BASE64_CHARS[v];
        }
        return chars.join('');
    }

    private static getRandomLong(): number {
        return Math.floor(Math.random() * Number.MAX_SAFE_INTEGER);
    }
}
