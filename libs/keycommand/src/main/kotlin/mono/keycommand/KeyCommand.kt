package mono.keycommand

import mono.common.Key

/**
 * An enum class to contains all shortcut key command.
 */
enum class KeyCommand(vararg val keyCodes: Int) {
    IDLE,
    ESC(Key.KEY_ESC),
    DELETE(Key.KEY_BACKSPACE, Key.KEY_DELETE),
    MOVE_LEFT(Key.KEY_ARROW_LEFT),
    MOVE_UP(Key.KEY_ARROW_UP),
    MOVE_RIGHT(Key.KEY_ARROW_RIGHT),
    MOVE_DOWN(Key.KEY_ARROW_DOWN),

    ADD_RECTANGLE(Key.KEY_R),
    ADD_TEXT(Key.KEY_T);

    companion object {
        private val KEYCODE_TO_COMMAND_MAP: Map<Int, KeyCommand> =
            values().fold(mutableMapOf()) { map, type ->
                for (keyCode in type.keyCodes) {
                    map[keyCode] = type
                }
                map
            }

        fun getCommandByKey(keyCode: Int): KeyCommand =
            KEYCODE_TO_COMMAND_MAP[keyCode] ?: IDLE
    }
}
