package mono.ui.theme

/**
 * A class for enumerating all semantic colors.
 * [lightColorCode] and [darkColorCode] are the hex color code of RGB or RGBA used in CSS.
 */
enum class ThemeColor(internal val lightColorCode: String, internal val darkColorCode: String) {
    // Drawing space - Grid
    GridBackground("#FFFFFF", "#121212"),
    GridLine("#d9d9d9", "#282828"),
    GridLineZero("#BBBBBB", "#323232"),

    // Drawing space - Shape
    Shape("#000000", "#F0F0F0"),
    ShapeSelected("#0d47a1", "#1772DC"),
    ShapeTextEditing("#1976d2", "#005FCE"),
    ;

    val colorCode: String
        get() = ThemeManager.getInstance().getColorCode(this)
}
