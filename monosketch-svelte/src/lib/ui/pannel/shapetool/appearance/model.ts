class CloudOption {
    constructor(
        public id: string,
        public title: string,
        public useDashBorder: boolean = false,
    ) {}
}

export const fillOptions: CloudOption[] = [
    new CloudOption('F0', '×', true),
    new CloudOption('F1', '\u00A0'),
    new CloudOption('F2', '█'),
    new CloudOption('F3', '▒'),
    new CloudOption('F4', '░'),
    new CloudOption('F5', '▚'),
];

export const strokeOptions: CloudOption[] = [
    new CloudOption('B0', '\u00A0'),
    new CloudOption('B1', '─'),
    new CloudOption('B2', '━'),
    new CloudOption('B3', '═'),
];

export const lineAnchorOptions: CloudOption[] = [
    new CloudOption('A0', '\u00A0'),
    new CloudOption('A1', '▶'),
    new CloudOption('A12', '▷'),
    new CloudOption('A2', '■'),
    new CloudOption('A21', '□'),
    new CloudOption('A220', '◆'),
    new CloudOption('A221', '◇'),
    new CloudOption('A3', '○'),
    new CloudOption('A4', '◎'),
    new CloudOption('A5', '●'),
    new CloudOption('A6', '├'),
    new CloudOption('A61', '┣'),
    new CloudOption('A62', '╠'),
];
