/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.keycommand

import mono.common.Key

/**
 * An enum class to contains all shortcut key command.
 */
enum class KeyCommand(
    vararg val keyCodes: Int,
    private val commandKeyState: MetaKeyState = MetaKeyState.ANY,
    private val shiftKeyState: MetaKeyState = MetaKeyState.ANY,
    val isKeyEventPropagationAllowed: Boolean = true,
    val isRepeatable: Boolean = false
) {
    IDLE,

    SELECT_ALL(Key.KEY_A, commandKeyState = MetaKeyState.ON, isKeyEventPropagationAllowed = false),
    DESELECTION(Key.KEY_ESC),
    DELETE(Key.KEY_BACKSPACE, Key.KEY_DELETE),

    MOVE_LEFT(Key.KEY_ARROW_LEFT, shiftKeyState = MetaKeyState.OFF, isRepeatable = true),
    MOVE_UP(Key.KEY_ARROW_UP, shiftKeyState = MetaKeyState.OFF, isRepeatable = true),
    MOVE_RIGHT(Key.KEY_ARROW_RIGHT, shiftKeyState = MetaKeyState.OFF, isRepeatable = true),
    MOVE_DOWN(Key.KEY_ARROW_DOWN, shiftKeyState = MetaKeyState.OFF, isRepeatable = true),

    FAST_MOVE_LEFT(Key.KEY_ARROW_LEFT, shiftKeyState = MetaKeyState.ON, isRepeatable = true),
    FAST_MOVE_UP(Key.KEY_ARROW_UP, shiftKeyState = MetaKeyState.ON, isRepeatable = true),
    FAST_MOVE_RIGHT(Key.KEY_ARROW_RIGHT, shiftKeyState = MetaKeyState.ON, isRepeatable = true),
    FAST_MOVE_DOWN(Key.KEY_ARROW_DOWN, shiftKeyState = MetaKeyState.ON, isRepeatable = true),

    ADD_RECTANGLE(Key.KEY_R),
    ADD_TEXT(Key.KEY_T),
    ADD_LINE(Key.KEY_L),

    ENTER_EDIT_MODE(Key.KEY_ENTER),
    SELECTION_MODE(Key.KEY_V, commandKeyState = MetaKeyState.OFF),

    COPY(Key.KEY_C, commandKeyState = MetaKeyState.ON, shiftKeyState = MetaKeyState.OFF),
    CUT(Key.KEY_X, commandKeyState = MetaKeyState.ON),
    DUPLICATE(Key.KEY_D, commandKeyState = MetaKeyState.ON, isKeyEventPropagationAllowed = false),

    COPY_TEXT(
        Key.KEY_C,
        commandKeyState = MetaKeyState.ON,
        shiftKeyState = MetaKeyState.ON,
        isKeyEventPropagationAllowed = false
    ),

    UNDO(Key.KEY_Z, commandKeyState = MetaKeyState.ON, shiftKeyState = MetaKeyState.OFF),
    REDO(Key.KEY_Z, commandKeyState = MetaKeyState.ON, shiftKeyState = MetaKeyState.ON),

    SHIFT_KEY(Key.KEY_SHIFT)
    ;

    private enum class MetaKeyState {
        ON {
            override fun isAccepted(hasKey: Boolean): Boolean = hasKey
        },
        OFF {
            override fun isAccepted(hasKey: Boolean): Boolean = !hasKey
        },
        ANY {
            override fun isAccepted(hasKey: Boolean): Boolean = true
        };

        abstract fun isAccepted(hasKey: Boolean): Boolean
    }

    companion object {
        private val KEYCODE_TO_COMMAND_MAP: Map<Int, List<KeyCommand>> =
            values().fold(mutableMapOf<Int, MutableList<KeyCommand>>()) { map, type ->
                for (keyCode in type.keyCodes) {
                    map.getOrPut(keyCode) { mutableListOf() }.add(type)
                }
                map
            }

        internal fun getCommandByKey(
            keyCode: Int,
            hasCommandKey: Boolean,
            hasShiftKey: Boolean
        ): KeyCommand =
            KEYCODE_TO_COMMAND_MAP[keyCode]
                ?.firstOrNull {
                    it.commandKeyState.isAccepted(hasCommandKey) &&
                        it.shiftKeyState.isAccepted(hasShiftKey)
                }
                ?: IDLE
    }
}
