import { Key, type KeyCommand, KeyCommandType, type KeyMap, MetaKeyState } from './interface';

const KeyCommandOptionsDefaults: KeyCommand = {
    command: KeyCommandType.IDLE,
    key: [],
    commandKeyState: MetaKeyState.ANY,
    shiftKeyState: MetaKeyState.ANY,
    isKeyEventPropagationAllowed: true,
    isRepeatable: false,
};

const KeyCommands: KeyCommand[] = [
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.IDLE,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.SELECT_ALL,
        key: [Key.A],
        commandKeyState: MetaKeyState.ON,
        isKeyEventPropagationAllowed: false,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.DESELECTION,
        key: [Key.ESC],
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.DELETE,
        key: [Key.DELETE, Key.BACKSPACE],
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.MOVE_LEFT,
        key: [Key.ARROW_LEFT],
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.MOVE_UP,
        key: [Key.ARROW_UP],
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.MOVE_RIGHT,
        key: [Key.ARROW_RIGHT],
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.MOVE_DOWN,
        key: [Key.ARROW_DOWN],
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.FAST_MOVE_LEFT,
        key: [Key.ARROW_LEFT],
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.FAST_MOVE_UP,
        key: [Key.ARROW_UP],
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.FAST_MOVE_RIGHT,
        key: [Key.ARROW_RIGHT],
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.FAST_MOVE_DOWN,
        key: [Key.ARROW_DOWN],
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.ADD_RECTANGLE,
        key: [Key.R],
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.ADD_TEXT,
        key: [Key.T],
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.ADD_LINE,
        key: [Key.L],
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.ENTER_EDIT_MODE,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.SELECTION_MODE,
        key: [Key.V],
        commandKeyState: MetaKeyState.OFF,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.COPY,
        key: [Key.C],
        commandKeyState: MetaKeyState.ON,
        shiftKeyState: MetaKeyState.OFF,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.CUT,
        key: [Key.X],
        commandKeyState: MetaKeyState.ON,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.DUPLICATE,
        key: [Key.D],
        commandKeyState: MetaKeyState.ON,
        isKeyEventPropagationAllowed: false,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.COPY_TEXT,
        key: [Key.C],
        commandKeyState: MetaKeyState.ON,
        shiftKeyState: MetaKeyState.ON,
        isKeyEventPropagationAllowed: false,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.UNDO,
        key: [Key.Z],
        commandKeyState: MetaKeyState.ON,
        shiftKeyState: MetaKeyState.OFF,
        isRepeatable: true,
    },
    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.REDO,
        key: [Key.Z],
        commandKeyState: MetaKeyState.ON,
        shiftKeyState: MetaKeyState.ON,
        isRepeatable: true,
    },

    {
        ...KeyCommandOptionsDefaults,
        command: KeyCommandType.SHIFT_KEY,
        key: [Key.SHIFT],
    },
];

const KeyCommandMap: Map<KeyCommandType, KeyCommand> = new Map();
const KeyCodeToKeyCommandMap: Map<Key, KeyCommand[]> = new Map();

function initKeyToKeyCommandMap() {
    for (const keyCommand of KeyCommands) {
        KeyCommandMap.set(keyCommand.command, keyCommand);
    }

    for (const keyCommand of KeyCommands) {
        for (const keyCode of keyCommand.key) {
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
    key: KeyMap,
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
    return getCommandByType(KeyCommandType.IDLE)!;
}

export function getCommandByType(base: KeyCommandType): KeyCommand {
    if (KeyCodeToKeyCommandMap.size === 0) {
        initKeyToKeyCommandMap();
    }
    return KeyCommandMap.get(base)!;
}
