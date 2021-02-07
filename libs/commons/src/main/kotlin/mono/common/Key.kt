package mono.common

/**
 * An utility class to store and perform key-related stuffs.
 */
object Key {
    private const val KEY_BACKSPACE = 8
    private const val KEY_DELETE = 46
    private const val KEY_ARROW_LEFT = 37
    private const val KEY_ARROW_UP = 38
    private const val KEY_ARROW_RIGHT = 39
    private const val KEY_ARROW_DOWN = 40

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
        DELETE(KEY_BACKSPACE, KEY_DELETE),
        MOVE_LEFT(KEY_ARROW_LEFT),
        MOVE_UP(KEY_ARROW_UP),
        MOVE_RIGHT(KEY_ARROW_RIGHT),
        MOVE_DOWN(KEY_ARROW_DOWN),
    }
}
