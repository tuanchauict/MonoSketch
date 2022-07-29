package mono.actionmanager

import mono.common.MouseCursor

/**
 * An enum class which defines all action types which repeatedly have effects after triggered.
 */
enum class RetainableActionType(val mouseCursor: MouseCursor) {
    IDLE(MouseCursor.DEFAULT),
    ADD_RECTANGLE(MouseCursor.CROSSHAIR),
    ADD_TEXT(MouseCursor.TEXT),
    ADD_LINE(MouseCursor.CROSSHAIR)
}
