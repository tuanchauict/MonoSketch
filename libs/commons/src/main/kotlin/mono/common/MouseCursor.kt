package mono.common

/**
 * A class for enumerating all in-use mouse cursors.
 */
enum class MouseCursor(val value: String) {
    DEFAULT("default"),
    CROSSHAIR("crosshair"),
    MOVE("move"),
    RESIZE_NWSE("nwse-resize"),
    RESIZE_NS("ns-resize"),
    RESIZE_NESW("nesw-resize"),
    RESIZE_EW("ew-resize"),
    RESIZE_ROW("row-resize"),
    RESIZE_COL("col-resize")
}
