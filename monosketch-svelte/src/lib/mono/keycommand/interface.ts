/**
 * An object to contains all handling key-codes.
 */
export class Key {
    static readonly ESC = 27;
    static readonly SHIFT = 16;
    static readonly ENTER = 13;
    static readonly BACKSPACE = 8;
    static readonly DELETE = 46;
    static readonly ARROW_LEFT = 37;
    static readonly ARROW_UP = 38;
    static readonly ARROW_RIGHT = 39;
    static readonly ARROW_DOWN = 40;
    static readonly A = 65;
    static readonly C = 67;
    static readonly D = 68;
    static readonly L = 76;
    static readonly R = 82;
    static readonly T = 84;
    static readonly V = 86;
    static readonly X = 88;
    static readonly Z = 90;
}

interface MetaKeyState {
    isAccepted(hasKey: boolean): boolean;
}

export const MetaKeyState: { [key: string]: MetaKeyState } = {
    ON: {
        isAccepted: (hasKey: boolean) => hasKey,
    },
    OFF: {
        isAccepted: (hasKey: boolean) => !hasKey,
    },
    ANY: {
        isAccepted: () => true,
    },
};

export enum KeyCommandType {
    IDLE = 'IDLE',

    SELECT_ALL = 'SELECT_ALL',
    DESELECTION = 'DESELECTION',
    DELETE = 'DELETE',

    MOVE_LEFT = 'MOVE_LEFT',
    MOVE_UP = 'MOVE_UP',
    MOVE_RIGHT = 'MOVE_RIGHT',
    MOVE_DOWN = 'MOVE_DOWN',

    FAST_MOVE_LEFT = 'FAST_MOVE_LEFT',
    FAST_MOVE_UP = 'FAST_MOVE_UP',
    FAST_MOVE_RIGHT = 'FAST_MOVE_RIGHT',
    FAST_MOVE_DOWN = 'FAST_MOVE_DOWN',

    ADD_RECTANGLE = 'ADD_RECTANGLE',
    ADD_TEXT = 'ADD_TEXT',
    ADD_LINE = 'ADD_LINE',
    ENTER_EDIT_MODE = 'ENTER_EDIT_MODE',
    SELECTION_MODE = 'SELECTION_MODE',

    COPY = 'COPY',
    CUT = 'CUT',
    DUPLICATE = 'DUPLICATE',

    COPY_TEXT = 'COPY_TEXT',

    UNDO = 'UNDO',
    REDO = 'REDO',
    SHIFT_KEY = 'SHIFT_KEY',
}

export interface KeyCommand {
    command: KeyCommandType,
    keyCodes: Key[];
    commandKeyState: MetaKeyState;
    shiftKeyState: MetaKeyState;
    isKeyEventPropagationAllowed: boolean;
    isRepeatable: boolean;
}
