import { expect, test } from 'vitest';
import {getCommandByType} from './keycommands';
import { KeyCommandType } from './interface';

test('same size command types and key commands', () => {
    const keyCommandTypes = Object.values(KeyCommandType);
    for (const keyCommandType of keyCommandTypes) {
        const command = getCommandByType(keyCommandType);
        expect(command, `command of ${keyCommandType} is not defined`).toBeDefined();
        expect(command.command).toBe(keyCommandType);
    }
});
