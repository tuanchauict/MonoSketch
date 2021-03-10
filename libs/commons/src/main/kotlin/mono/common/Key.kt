package mono.common

/**
 * An utility class to store and perform key-related stuffs.
 *
 * TODO: Move this class into another module
 */
object Key {
    const val KEY_ESC = 27
    const val KEY_ENTER = 13
    private const val KEY_BACKSPACE = 8
    private const val KEY_DELETE = 46
    private const val KEY_ARROW_LEFT = 37
    private const val KEY_ARROW_UP = 38
    private const val KEY_ARROW_RIGHT = 39
    private const val KEY_ARROW_DOWN = 40
    private const val KEY_R = 82
    private const val KEY_T = 84

    private val KEYCODE_TO_COMMAND_MAP: Map<Int, KeyCommand> =
        KeyCommand.values().fold(mutableMapOf()) { map, type ->
            for (keyCode in type.keyCodes) {
                map[keyCode] = type
            }
            map
        }

    fun getCommandByKey(keyCode: Int): KeyCommand =
        KEYCODE_TO_COMMAND_MAP[keyCode] ?: KeyCommand.IDLE

    enum class KeyCommand(vararg val keyCodes: Int) {
        IDLE,
        ESC(KEY_ESC),
        DELETE(KEY_BACKSPACE, KEY_DELETE),
        MOVE_LEFT(KEY_ARROW_LEFT),
        MOVE_UP(KEY_ARROW_UP),
        MOVE_RIGHT(KEY_ARROW_RIGHT),
        MOVE_DOWN(KEY_ARROW_DOWN),

        ADD_RECTANGLE(KEY_R),
        ADD_TEXT(KEY_T)
    }
}
