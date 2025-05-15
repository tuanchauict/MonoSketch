export namespace StringExt {
    /**
     * Trims the margin of a string.
     *
     * The margin is defined by the first line that starts with a margin prefix.
     * @param input
     * @param marginPrefix The prefix of the margin. Default is `|`.
     */
    export const trimMargin = (
        input: string,
        marginPrefix: string = '|',
    ): string => {
        const lines = input.split('\n').filter(line => line.indexOf(marginPrefix) !== -1);
        if (!lines) {
            return input;
        }

        const marginLength = lines[0].indexOf(marginPrefix) + 1;
        const output = lines.map(line => line.slice(marginLength)).join('\n');
        return output;
    };

    export const trimIndent = (str: string): string => {
        const lines = str.split('\n');
        const nonEmptyLines = lines.filter(line => line.trim().length > 0);
        const indentLengths = nonEmptyLines.map(line => line.match(/^\s*/)?.[0].length ?? 0);
        const minIndent = Math.min(...indentLengths);

        return lines.map(line => line.slice(minIndent)).join('\n').trim();
    };
}
