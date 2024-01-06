export enum TextAlignment {
    HORIZONTAL_LEFT,
    HORIZONTAL_MIDDLE,
    HORIZONTAL_RIGHT,
    VERTICAL_TOP,
    VERTICAL_MIDDLE,
    VERTICAL_BOTTOM,
}

export const horizontalAlignmentTypes = [
    TextAlignment.HORIZONTAL_LEFT,
    TextAlignment.HORIZONTAL_MIDDLE,
    TextAlignment.HORIZONTAL_RIGHT,
];

export const verticalAlignmentTypes = [
    TextAlignment.VERTICAL_TOP,
    TextAlignment.VERTICAL_MIDDLE,
    TextAlignment.VERTICAL_BOTTOM,
];

export const textAlignmentMap = {
    [TextAlignment.HORIZONTAL_LEFT]: {
        iconPath: 'M3 2h14v2H3zM3 6h8v2H3zM3 10h10v2H3z',
    },
    [TextAlignment.HORIZONTAL_MIDDLE]: {
        iconPath: 'M3 2h14v2H3zM6 6h8v2H6zM5 10h10v2H5z',
    },
    [TextAlignment.HORIZONTAL_RIGHT]: {
        iconPath: 'M3 2h14v2H3zM9 6h8v2H9zM7 10h10v2H7z',
    },
    [TextAlignment.VERTICAL_TOP]: {
        iconPath: 'M3 0h14v2H3zM3 4h14v2H3z',
    },
    [TextAlignment.VERTICAL_MIDDLE]: {
        iconPath: 'M3 4h14v2H3zM3 8h14v2H3z',
    },
    [TextAlignment.VERTICAL_BOTTOM]: {
        iconPath: 'M3 8h14v2H3zM3 12h14v2H3z',
    },
};
