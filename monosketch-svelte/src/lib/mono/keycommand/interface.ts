/**
 * An object to contains all handling key-codes.
 */
export class Key {
    static readonly ESC: KeyMap = { key: ['Escape'], keyCode: 27 };
    static readonly SHIFT: KeyMap = { key: ['Shift'], keyCode: 16 };
    static readonly ENTER: KeyMap = { key: ['Enter'], keyCode: 13 };
    static readonly BACKSPACE: KeyMap = { key: ['Backspace'], keyCode: 8 };
    static readonly DELETE: KeyMap = { key: ['Delete'], keyCode: 46 };
    static readonly ARROW_LEFT: KeyMap = { key: ['ArrowLeft'], keyCode: 37 };
    static readonly ARROW_UP: KeyMap = { key: ['ArrowUp'], keyCode: 38 };
    static readonly ARROW_RIGHT: KeyMap = { key: ['ArrowRight'], keyCode: 39 };
    static readonly ARROW_DOWN: KeyMap = { key: ['ArrowDown'], keyCode: 40 };
    static readonly A: KeyMap = { key: ['a', 'A'], keyCode: 65 };
    static readonly C: KeyMap = { key: ['c', 'C'], keyCode: 67 };
    static readonly D: KeyMap = { key: ['d', 'D'], keyCode: 68 };
    static readonly L: KeyMap = { key: ['l', 'L'], keyCode: 76 };
    static readonly R: KeyMap = { key: ['r', 'R'], keyCode: 82 };
    static readonly T: KeyMap = { key: ['t', 'T'], keyCode: 84 };
    static readonly V: KeyMap = { key: ['v', 'V'], keyCode: 86 };
    static readonly X: KeyMap = { key: ['x', 'X'], keyCode: 88 };
    static readonly Z: KeyMap = { key: ['z', 'Z'], keyCode: 90 };
}

export function getKeyFromEvent(e: KeyboardEvent): KeyMap | undefined {
    return Object.values(Key).find(keyMap => keyMap.key.includes(e.key));
}

export interface KeyMap {
    key: string[]; // Using array to support multiple key variations (e.g. 'a' and 'A')
    keyCode: number;
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
    command: KeyCommandType;
    key: KeyMap[];
    commandKeyState: MetaKeyState;
    shiftKeyState: MetaKeyState;
    isKeyEventPropagationAllowed: boolean;
    isRepeatable: boolean;
}
