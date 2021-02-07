package mono.common

/**
 * An utility class to store and perform key-related stuffs.
 */
object Key {
    private const val KEY_BACKSPACE = 8
    private const val KEY_DELETE = 46

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
        DELETE(KEY_BACKSPACE, KEY_DELETE)
    }
}
