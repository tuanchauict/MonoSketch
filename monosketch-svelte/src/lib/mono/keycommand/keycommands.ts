import { Key, type KeyCommand, KeyCommandType, MetaKeyState } from './interface';

const KeyCommandOptionsDefaults: KeyCommand = {
    command: KeyCommandType.IDLE,
    keyCodes: [],
    commandKeyState: MetaKeyState.ANY,
    shiftKeyState: MetaKeyState.ANY,
    isKeyEventPropagationAllowed: true,
    isRepeatable: false,
};

export const KeyCommands: KeyCommand[] = [
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.IDLE,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.SELECT_ALL,
        keyCodes: [Key.A],
        commandKeyState: MetaKeyState.ON,
        isKeyEventPropagationAllowed: false,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.DESELECTION,
        keyCodes: [Key.ESC],
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.DELETE,
        keyCodes: [Key.DELETE, Key.BACKSPACE],
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.MOVE_LEFT,
        keyCodes: [Key.ARROW_LEFT],
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.MOVE_UP,
        keyCodes: [Key.ARROW_UP],
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.MOVE_RIGHT,
        keyCodes: [Key.ARROW_RIGHT],
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.MOVE_DOWN,
        keyCodes: [Key.ARROW_DOWN],
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.FAST_MOVE_LEFT,
        keyCodes: [Key.ARROW_LEFT],
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.FAST_MOVE_UP,
        keyCodes: [Key.ARROW_UP],
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.FAST_MOVE_RIGHT,
        keyCodes: [Key.ARROW_RIGHT],
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.FAST_MOVE_DOWN,
        keyCodes: [Key.ARROW_DOWN],
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.ADD_RECTANGLE,
        keyCodes: [Key.R],
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.ADD_TEXT,
        keyCodes: [Key.T],
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.ADD_LINE,
        keyCodes: [Key.L],
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.ENTER_EDIT_MODE,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.SELECTION_MODE,
        keyCodes: [Key.V],
        commandKeyState: MetaKeyState.OFF,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.COPY,
        keyCodes: [Key.C],
        commandKeyState: MetaKeyState.ON,
        shiftKeyState: MetaKeyState.OFF,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.CUT,
        keyCodes: [Key.X],
        commandKeyState: MetaKeyState.ON,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.DUPLICATE,
        keyCodes: [Key.D],
        commandKeyState: MetaKeyState.ON,
        isKeyEventPropagationAllowed: false,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.COPY_TEXT,
        keyCodes: [Key.C],
        commandKeyState: MetaKeyState.ON,
        shiftKeyState: MetaKeyState.ON,
        isKeyEventPropagationAllowed: false,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.UNDO,
        keyCodes: [Key.Z],
        commandKeyState: MetaKeyState.ON,
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.REDO,
        keyCodes: [Key.Z],
        commandKeyState: MetaKeyState.ON,
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.SHIFT_KEY,
        keyCodes: [Key.SHIFT],
    },
];

const KeyCommandMap: Map<KeyCommandType, KeyCommand> = new Map();
const KeyCodeToKeyCommandMap: Map<Key, KeyCommand[]> = new Map();

function initKeyToKeyCommandMap() {
    for (const keyCommand of KeyCommands) {
        KeyCommandMap.set(keyCommand.command, keyCommand);
    }

    for (const keyCommand of KeyCommands) {
        for (const keyCode of keyCommand.keyCodes) {
            const keyCommands = KeyCodeToKeyCommandMap.get(keyCode);
            if (keyCommands) {
                keyCommands.push(keyCommand);
            } else {
                KeyCodeToKeyCommandMap.set(keyCode, [keyCommand]);
            }
        }
    }
}

export function getCommandByKey(
    key: Key,
    hasCommandKey: boolean,
    hasShiftKey: boolean,
): KeyCommand {
    if (KeyCodeToKeyCommandMap.size === 0) {
        initKeyToKeyCommandMap();
    }
    const keyCommands = KeyCodeToKeyCommandMap.get(key);
    if (keyCommands) {
        for (const command of keyCommands) {
            if (
                command.commandKeyState.isAccepted(hasCommandKey) &&
                command.shiftKeyState.isAccepted(hasShiftKey)
            ) {
                return command;
            }
        }
    }
    return getCommandByType(KeyCommandType.IDLE)!!;
}

export function getCommandByType(base: KeyCommandType): KeyCommand {
    return KeyCommandMap.get(base)!!;
}
