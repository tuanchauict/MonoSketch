/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.ui.theme

/**
 * A class for enumerating all semantic colors.
 * [lightColorCode] and [darkColorCode] are the hex color code of RGB or RGBA used in CSS.
 */
enum class ThemeColor(internal val lightColorCode: String, internal val darkColorCode: String) {
    // Drawing space - Axis
    AxisBackground("#EEEEEE", "#1E1E1E"),
    AxisText("#666666", "#666666"),
    AxisRule("#444444", "#666666"),

    // Drawing space - Grid
    GridBackground("#FFFFFF", "#121212"),
    GridLine("#d9d9d9", "#282828"),
    GridLineZero("#BBBBBB", "#323232"),

    // Drawing space - Shape
    Shape("#000000", "#F0F0F0"),
    ShapeSelected("#0D7CFF", "#FFD82F"),
    ShapeTextEditing("#0767C6", "#956d04"),
    ShapeLineConnectTarget("#21BF73", "#7DCE13"),

    // Drawing space - Selection
    SelectionAreaStroke("#858585", "#858585"),
    SelectionBoundStroke("#64b5f6", "#FDF7C3"),
    SelectionDotStroke("#64b5f6", "#f2ae00"),
    SelectionDotFill("#FFFFFF", "#1E1E1E")
    ;

    val colorCode: String
        get() = ThemeManager.getInstance().getColorCode(this)
}
