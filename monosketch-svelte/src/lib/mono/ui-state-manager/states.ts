export enum ScrollMode {
    BOTH = 'both',
    VERTICAL = 'vertical',
    HORIZONTAL = 'horizontal',
}

export enum ThemeMode {
    LIGHT = 'light',
    DARK = 'dark',
}

export interface ThemeColor {
    lightColorCode: string;
    darkColorCode: string;
}

/**
 * A set of colors used in the workspace.
 * Note: other UI elements should use CSS variables instead of hard-coded colors.
 */
export const ThemeColors = {
    // Drawing space - Axis
    AxisBackground: themeColor('#f5f5f5', '#1e1e1e'),
    AxisText: themeColor('#666666', '#666666'),
    AxisRule: themeColor('#444444', '#666666'),

    // Drawing space - Grid
    GridBackground: themeColor('#FFFFFF', '#121212'),
    GridLine: themeColor('#d9d9d9', '#282828'),
    GridLineZero: themeColor('#BBBBBB', '#323232'),

    // Drawing space - Shape
    Shape: themeColor('#000000', '#F0F0F0'),
    ShapeSelected: themeColor('#0D7CFF', '#FFD82F'),
    ShapeTextEditing: themeColor('#0767C6', '#956d04'),
    ShapeLineConnectTarget: themeColor('#E86A33', '#00FFF6'),

    // Drawing space - Selection
    SelectionAreaStroke: themeColor('#858585', '#858585'),
    SelectionBoundStroke: themeColor('#64b5f6', '#FDF7C3'),
    SelectionDotStroke: themeColor('#64b5f6', '#f2ae00'),
    SelectionDotFill: themeColor('#FFFFFF', '#1E1E1E'),
};

function themeColor(lightColorCode: string, darkColorCode: string): ThemeColor {
    return { lightColorCode, darkColorCode };
}
