import { isCommandKeySupported } from '../../../mono/common/platform';

export interface Shortcut {
    description: string;
    keys: string[];
}

const commandKey = isCommandKeySupported() ? 'cmd' : 'ctrl';
const shiftKey = 'shift';

export const shortcuts: Shortcut[][] = [
    [
        { description: 'Rectangle', keys: ['R'] },
        { description: 'Text', keys: ['T'] },
        { description: 'Line', keys: ['L'] },
    ],
    [
        { description: 'Select tool', keys: ['V'] },
        { description: 'Select all', keys: [commandKey, 'A'] },
        { description: 'Deselect', keys: ['esc'] },
        { description: 'Undo', keys: [commandKey, 'Z'] },
        { description: 'Redo', keys: [commandKey, shiftKey, 'Z'] },
    ],
    [
        { description: 'Duplicate', keys: [commandKey, 'D'] },
        { description: 'Copy as text', keys: [commandKey, shiftKey, 'C'] },
        { description: 'Remove shapes', keys: ['delete'] },
        { description: 'Edit selected text', keys: ['enter'] },
    ],
];
