export namespace StringExt {
    /**
     * Trims the margin of a string.
     *
     * The margin is defined by the first line that starts with a margin prefix.
     * @param input
     * @param marginPrefix The prefix of the margin. Default is `|`.
     * @param trimEnd The end of the line. Default is true.
     */
    export const trimMargin = (
        input: string,
        marginPrefix: string = '|',
        trimEnd: boolean = true,
    ): string => {
        const lines = input.split('\n');

        const trimmedLines = lines.map((line) => {
            const index = line.indexOf(marginPrefix);
            if (index === -1) {
                return null;
            }
            const endExcluded = trimEnd ? line.length - 1 : line.length;
            return line.slice(index + marginPrefix.length, endExcluded);
        });
        return trimmedLines.filter((line) => line !== null).join('\n');
    };

    export const trimIndent = (str: string): string => {
        const lines = str.split('\n');
        const nonEmptyLines = lines.filter(line => line.trim().length > 0);
        const indentLengths = nonEmptyLines.map(line => line.match(/^\s*/)?.[0].length ?? 0);
        const minIndent = Math.min(...indentLengths);

        return lines.map(line => line.slice(minIndent)).join('\n').trim();
    }
}
