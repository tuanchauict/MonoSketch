export class UUID {
    private static readonly VERSION = "02";
    private static readonly BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".split("");

    static generate(): string {
        const part1 = new Date().getTime().toString(2).slice(0, -3);
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
        return chars.join("");
    }

    private static getRandomLong(): number {
        return Math.floor(Math.random() * Number.MAX_SAFE_INTEGER);
    }
}
