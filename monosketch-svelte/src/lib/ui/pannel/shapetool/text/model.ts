import { TextHorizontalAlign } from "$mono/shape/extra/style";

export const HorizontalAlignIconMap: Record<TextHorizontalAlign, string> = {
    [TextHorizontalAlign.LEFT]: 'M3 2h14v2H3zM3 6h8v2H3zM3 10h10v2H3z',
    [TextHorizontalAlign.MIDDLE]: 'M3 2h14v2H3zM6 6h8v2H6zM5 10h10v2H5z',
    [TextHorizontalAlign.RIGHT]: 'M3 2h14v2H3zM9 6h8v2H9zM7 10h10v2H7z',
}

export const VerticalAlignIconMap: Record<TextHorizontalAlign, string> = {
    [TextHorizontalAlign.LEFT]: 'M3 0h14v2H3zM3 4h14v2H3z',
    [TextHorizontalAlign.MIDDLE]: 'M3 4h14v2H3zM3 8h14v2H3z',
    [TextHorizontalAlign.RIGHT]: 'M3 8h14v2H3zM3 12h14v2H3z',
}
